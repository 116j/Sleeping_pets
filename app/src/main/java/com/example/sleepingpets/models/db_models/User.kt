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
    @ColumnInfo(name = "wake_up_time")
    var wakeUpTime: String = "07:00:00",
    @ColumnInfo(name = "go_to_bed_time")
    var goToBedTime: String = "23:00:00",
    var email: String = "",
    var password: String = "",
    @ColumnInfo(name = "auth_type")
    var authType: String,
    @ColumnInfo(name = "social_net_id")
    var socialNetId: String = "",
    var balance: Long = 1000,
    @ColumnInfo(name = "pet_score")
    var petScore: Long = 0,
    @ColumnInfo(name = "sleep_score")
    var sleepScore: Byte = 0,
    var alarmMusic: String? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).path,
    var sleepMusic: String? = "",
    var sleepDuration: Long=0L
) : Serializable