package com.example.sleepingpets.models.db_models

import android.media.RingtoneManager
import androidx.core.net.toFile
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.facebook.FacebookSdk.getApplicationContext
import java.io.Serializable
import java.sql.Time
import java.time.Duration

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var login: String = "",
    var image: String = "@drawable/prof_image",
    var wakeUpTime: String = "07:00:00",
    var goToBedTime: String = "23:00:00",
    var email: String = "",
    var password: String = "",
    var authType: String,
    var socialNetId: String = "",
    var balance: Long = 1000,
    @ColumnInfo(name = "pet_score")
    var petScore: Long = 0,
    @ColumnInfo(name = "sleep_score")
    var sleepScore: Byte = 0,
    var alarmMusic: String? = "",
    var sleepMusic: String? = "",
    var sleepDuration: Long=0L,
    var suggestionId:Int=0,
    var sleepPetId:Int=0
) : Serializable