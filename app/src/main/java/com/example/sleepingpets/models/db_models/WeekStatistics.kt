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
    var name:String?,
    var weekBegin:Long,
    var monday:Short,
    var tuesday:Short,
    var wednesday:Short,
    var thursday:Short,
    var friday:Short,
    var saturday:Short,
    var sunday:Short
): Serializable