package com.example.photoclicker

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photoclicker.Room.AppDatabase
import com.example.photoclicker.Room.Sessions
import com.example.photoclicker.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var binding: ActivityMainBinding
    private lateinit var SessionId:String;
    private var ImageCounter = 0;
    private val imageList = ArrayList<Bitmap>()
    lateinit var db: AppDatabase

    @SuppressLint("NotifyDataSetChanged")
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){ success->
            if(success){
                val currentImageBitmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver,imageUri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
                }
                imageList.add(currentImageBitmp)
                binding.ImageViewRV.adapter?.notifyDataSetChanged()
            }
//            binding.imageView.setImageURI(null)
//            binding.imageView.setImageURI(imageUri)


    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //creating instance of db
        db = AppDatabase.getDatabase(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //creating sessionId Automatically
        SessionId = createSessionId()
        // OnclickListener
        binding.btnClickPic.setOnClickListener {
            imageUri  = createImageUri()!!
            contract.launch(imageUri)

        }

        // OnclickListener for btnSave
        binding.btnSaveSession.setOnClickListener {
            if(binding.EtName.text.isNotEmpty() && binding.EtAge.text.isNotEmpty() && imageList.size>0){
                val name = binding.EtName.text.toString()
                val age =  binding.EtAge.text.toString()

                for (image in imageList){
                    saveBitmapToFile(image,SessionId)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    db.SessionsDao().insertSession(Sessions(SessionId,name,age))
                }.invokeOnCompletion { throwable ->
                    if (throwable != null) {
                        Log.e("JobComplete", "Coroutine failed: ${throwable.message}")
                    } else {
                        Log.d("JobComplete", "Coroutine finished successfully")
                        binding.EtName.text.clear()
                        binding.EtAge.text.clear()
                        imageList.clear()
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.ImageViewRV.adapter?.notifyDataSetChanged()
                            SessionId = createSessionId()
                            binding.sessionIdTextField.text = SessionId
                        }
//                        Toast.makeText(this@MainActivity, "Saved", Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(this, "pls Enter Name and Age Properly least One pic", Toast.LENGTH_SHORT).show()
            }
        }

        // adapter to preview image that You Clicked
        val adapter  = ImageViewRecyclerAdapter(imageList)
        binding.ImageViewRV.layoutManager = GridLayoutManager(this,2)
        binding.ImageViewRV.adapter = adapter

        binding.sessionIdTextField.text = SessionId


        // Onclick Search Btn
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this,AllSessionShow::class.java)
            startActivity(intent)
        }
    }
    private fun createSessionId():String{
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return "Session_"+(1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }
    private fun createImageUri(): Uri? {
        val image = File(applicationContext.filesDir,"Camera_photo.png")
        return  FileProvider.getUriForFile(applicationContext,"com.example.photoclicker.FileProvider",image)
    }

    private fun saveBitmapToFile(bitmap: Bitmap, sessionId: String): Uri? {
        val appName = getString(R.string.app_name)// change to your app name

//        val mediaDir = File(
//            Environment.getExternalStorageDirectory(),
//            "Android/Media/$appName/Sessions/$sessionId"
//        )
//
//
//        if (!mediaDir.exists()) {
//            mediaDir.mkdirs()
//        }
        val resolver = applicationContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            // Must be inside app package folder
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$appName/Sessions/$sessionId")    // path is not Android /Media /... is not working due to android security -> so  i use pictures/appname/sessionid.....
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return try {
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                }
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            return uri
        }catch (e:Exception){
            Log.d(TAG, "saveBitmapToFile: "+e.message)
            null
        }

//        val fileName = "img_${System.currentTimeMillis()}"
//        val file = File.createTempFile(fileName, ".jpg", mediaDir)

//        return try {
//            FileOutputStream(file).use { out ->
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//            }
//            Uri.fromFile(file)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
    }
}