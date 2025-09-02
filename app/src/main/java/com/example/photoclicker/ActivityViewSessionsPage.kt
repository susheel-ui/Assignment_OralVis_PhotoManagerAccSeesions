package com.example.photoclicker

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.se.omapi.Session
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photoclicker.Room.AppDatabase
import com.example.photoclicker.Room.Sessions
import com.example.photoclicker.databinding.ActivityViewSessionsPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityViewSessionsPage : AppCompatActivity() {
    lateinit var binding: ActivityViewSessionsPageBinding
    lateinit var db :AppDatabase
    var images = ArrayList<Bitmap>()
    lateinit var adapter: ImageViewRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewSessionsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val intentSessionId = intent.getStringExtra("SessionId")
        db = AppDatabase.getDatabase(this)
        adapter = ImageViewRecyclerAdapter(images)
        binding.rcImages.layoutManager = GridLayoutManager(this,2)
        binding.rcImages.adapter = adapter
        Log.d("SessionPage", "onCreate: $intentSessionId")
        getData(intentSessionId.toString())


        val Uris = getImagesFromFolder(this,intentSessionId.toString())
        images.clear()
        for (uri in Uris){
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            images.add(bitmap)
            adapter.notifyDataSetChanged()
        }


    }

    fun getData(sessionId:String) {
        val Data =ArrayList<Sessions>()
       lifecycleScope.launch {
           var sessions = db.SessionsDao().getSessionById(sessionId)
           Data.clear()
           Data.addAll(sessions)
           withContext(Dispatchers.Main){
              setData(Data[0])
           }
       }
    }
    fun setData(session: Sessions){
            binding.textSessionId.text = session.Sessionid
            binding.textSessionName.text = session.Name
            binding.textSessionAge.text = session.Age
    }

    fun getImagesFromFolder(context: Context, folderName: String): List<Uri> {
        val imageUris = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,  // Folder name
            MediaStore.Images.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(folderName)

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )
                imageUris.add(contentUri)
            }
        }

        return imageUris
    }


}