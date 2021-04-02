package com.example.sleepingpets.models

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

import com.example.sleepingpets.models.db_models.*

@Dao
interface SleepingPeteDao{
    @Insert
    fun insertUser(user: User)

    @Insert
    fun insertPet(pet: Pet)

    @Insert
    fun insertWeek(week:WeekStatistics)

    @Insert
    fun insertSuggestion(suggestion: Suggestion)

    @Update
    fun updateUser(user: User)

    @Update
    fun updatePet(pet: Pet)

    @Update
    fun updateWeek(week:WeekStatistics)

    @Update
    fun updateSuggestion(suggestion: Suggestion)

    @Delete
    fun deleteUser(user: User)

    @Delete
    fun deletePet(pet: Pet)

    @Delete
    fun deleteWeek(week:WeekStatistics)

    @Delete
    fun deleteSuggestion(suggestion: Suggestion)

    @Query("delete from suggestion_table where my_id==:userId")
    fun deleteUserSuggestions(userId: Int)

    @Query("delete from suggestion_table where id!=:suggestionId")
    fun deleteRejectedSuggestions(suggestionId: Int)

    @Query("select * from  pet_table where user_id == :userId order by birth ")
    fun getUserPets(userId: Int):LiveData<List<Pet>>

    @Query("select * from suggestion_table where my_id == :userId order by suggest_time")
    fun getUserSuggestions(userId: Int):LiveData<List<Suggestion>>

    @Query("select * from  week_statistics_table where user_id == :userId order by id")
    fun getUserWeeks(userId: Int):LiveData<List<WeekStatistics>>

    @Query("select * from user_table order by sleep_score,pet_score")
    fun getUsers():LiveData<List<User>>

    @Query("select * from user_table where id==:userId")
    fun getUser(userId: Int):LiveData<User>
}