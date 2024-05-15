package com.example.dailytaskplanner.database

import com.example.dailytaskplanner.model.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun getAllTasks(): MutableList<Task> {
        return taskDao.getAll()
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