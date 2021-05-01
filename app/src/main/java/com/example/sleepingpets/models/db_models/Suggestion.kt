package com.example.sleepingpets.models.db_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Time

@Entity(tableName = "suggestion_table")
data class Suggestion(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    @ColumnInfo(name = "suggest_time")
    var suggestTime: String,
    @ColumnInfo(name = "my_id")
    var myId:Int,
    @ColumnInfo(name = "my_pet")
    var myPet:Int,
    @ColumnInfo(name = "user_pet")
    var userPet:Int,
    @ColumnInfo(name="user_id")
    var userId:Int
): Serializable