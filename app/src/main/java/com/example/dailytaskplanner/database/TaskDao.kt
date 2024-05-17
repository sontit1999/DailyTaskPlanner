package com.example.dailytaskplanner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dailytaskplanner.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getAllTask(): Flow<MutableList<Task>>

    @Query("SELECT * FROM task WHERE dateStart = :dateInput")
    suspend fun getTasksByDate(dateInput: String): MutableList<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tasks: Task)

    @Delete
    suspend fun delete(task: Task)

    @Delete
    suspend fun deleteUsers(vararg tasks: Task)

    @Update
    suspend fun updateUsers(vararg tasks: Task)
}