package com.example.photoclicker.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sessions(
    @PrimaryKey
    val Sessionid:String,
   val Name:String,
    val Age:String
)