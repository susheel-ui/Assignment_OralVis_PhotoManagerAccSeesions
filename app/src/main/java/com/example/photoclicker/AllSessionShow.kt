package com.example.photoclicker

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoclicker.Room.AppDatabase
import com.example.photoclicker.Room.Sessions
import com.example.photoclicker.databinding.ActivityAllSessionShowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllSessionShow : AppCompatActivity() {
    lateinit var db: AppDatabase
    lateinit var binding : ActivityAllSessionShowBinding
    var sessionsList = ArrayList<Sessions>()
    lateinit var adapter: SearchViewListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(this)
        binding = ActivityAllSessionShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = SearchViewListAdapter(sessionsList)
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this@AllSessionShow)
        binding.rvSearchResult.adapter = adapter

        getData()

        binding.searchTag.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextChange(p0: String?): Boolean {
                getData()
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    val  list = db.SessionsDao().getSessionById(p0.toString())
                    sessionsList.clear()
                    for (x in list){
                        Log.d("Data", "onQueryTextSubmit: ${x.Sessionid}")
                        sessionsList.add(x)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.notifyDataSetChanged()
                    }
                }
                return true
            }


        })
//
//
    }
    private fun getData(){

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val  list = db.SessionsDao().getAllSessions()
                sessionsList.clear()
                for (item in list){
                    sessionsList.add(item)
                }
            }.invokeOnCompletion {
                for(Sessions in sessionsList){
                    Log.d("Data", "onCreate: ${Sessions.Sessionid}")
                }
              CoroutineScope(Dispatchers.Main).launch {
                  adapter.notifyDataSetChanged()
              }
            }
        }catch (e:Exception){
            Log.d(TAG, "onCreate: ${e.message}")
        }

    }

}