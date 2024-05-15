package com.example.dailytaskplanner.database

import com.example.dailytaskplanner.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun getAllTasks(): Flow<MutableList<Task>> {
        return taskDao.getAllTask()
    }

    suspend fun addTask(task: Task) {
        taskDao.insertAll(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateUsers(task)
    }
}