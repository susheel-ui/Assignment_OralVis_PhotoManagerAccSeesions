package com.example.photoclicker.Room.Dao

import androidx.room.*
import com.example.photoclicker.Room.Sessions

@Dao
interface SessionsDao {
    @Insert
    suspend fun insertSession(session: Sessions)
    @Delete
    suspend fun deleteSession(session: Sessions)
    @Query("SELECT * FROM Sessions")
    suspend fun getAllSessions(): List<Sessions>

    @Query("SELECT * FROM Sessions WHERE Sessionid LIKE '%' || :sessionId || '%'")
    suspend fun getSessionById(sessionId: String): List<Sessions>


}