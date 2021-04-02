package com.example.sleepingpets.models.db_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "pet_table")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    var name:String,
    var type:String,
    var isBorn:Boolean=false,
    @ColumnInfo(name="birth")
    var birth:String=Date().toString(),
    @ColumnInfo(name="user_id")
    var userId:Int,
    var score:Long=0,
    var lvl:Int=0
):Serializable