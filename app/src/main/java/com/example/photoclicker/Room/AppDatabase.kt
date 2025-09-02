package com.example.photoclicker.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.photoclicker.Room.Dao.SessionsDao
@Database(entities = [Sessions::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun SessionsDao(): SessionsDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_database"
                )
                    .fallbackToDestructiveMigration() // auto-reset if version changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}