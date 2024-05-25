package com.ls.dailytaskplanner.database

import com.ls.dailytaskplanner.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun getAllTasks(): Flow<MutableList<Task>> {
        return taskDao.getAllTask()
    }

    suspend fun getTasksByDate(dateInput: String) : MutableList<Task> {
        return taskDao.getTasksByDate(dateInput)
    }

    suspend fun addTask(task: Task) {
        task.lastTimeModified = System.currentTimeMillis()
        taskDao.insertAll(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    suspend fun updateTask(task: Task) {
        task.lastTimeModified = System.currentTimeMillis()
        taskDao.updateUsers(task)
    }
}