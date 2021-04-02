package com.example.sleepingpets.models.db_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "week_statistics_table")
data class WeekStatistics(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "user_id")
    var userId: Int,
    var monday:Int,
    var thusday:Int,
    var wendsday:Int,
    var thursday:Int,
    var friday:Int,
    var saturday:Int,
    var sunday:Int
): Serializable