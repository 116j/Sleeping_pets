package com.example.sleepingpets.models

import android.content.Context
import com.example.sleepingpets.models.db_models.*

class SleepingPetsService() {
    companion object {
        fun updateDatabase() {

        }

        fun updateUsers(){
        }

        fun updateUserPets(id:Int){

        }

        fun updateUserStatistics(id:Int){

        }

        fun updateUserSuggestions(id:Int){

        }

        fun addUser(user: User,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.insertUser(user)
        }

        fun deleteUser(user: User,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.deleteUser(user)
        }

        fun updateUser(user: User,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.updateUser(user)
        }

        fun addPet(pet: Pet,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.insertPet(pet)
        }

        fun updatePet(pet: Pet,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.updatePet(pet)
        }

        fun addWeekStatistics(week: WeekStatistics,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.insertWeek(week)
        }

        fun updateWeekStatistics(week: WeekStatistics,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.updateWeek(week)
        }

        fun addSuggestion(suggestion: Suggestion,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.insertSuggestion(suggestion)
        }

        fun deleteSuggestion(suggestion: Suggestion,context: Context) {
            SleepingPetsDatabase.getInstance(context).databaseDao.deleteSuggestion(suggestion)
        }
    }
}