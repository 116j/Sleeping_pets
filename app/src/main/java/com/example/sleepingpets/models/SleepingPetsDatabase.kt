package com.example.sleepingpets.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sleepingpets.models.db_models.*

@Database(
    entities = [Pet::class, Suggestion::class, User::class, WeekStatistics::class],
    version = 1,
    exportSchema = false
)
abstract class SleepingPetsDatabase : RoomDatabase() {

    abstract val databaseDao: SleepingPeteDao

    companion object {

        @Volatile
        private var INSTANCE: SleepingPetsDatabase? = null

        fun getInstance(context: Context): SleepingPetsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepingPetsDatabase::class.java,
                        "sleeping_pets_database"
                    )
                        .allowMainThreadQueries()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}