package com.example.dailytaskplanner.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskplanner.database.TaskRepository
import com.example.dailytaskplanner.model.Task
import com.example.dailytaskplanner.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
     private val taskRepository: TaskRepository
) : ViewModel() {

    var listTaskLiveData = MutableLiveData<MutableList<Task>>()
    companion object{
        const val TAG = "HomeViewModel"
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.addTask(task)
                Logger.d(TAG, "Task added successfully")
                getAllTasks()
            } catch (e: Exception) {
                Logger.e(TAG, "Error adding task: ${e.message}")
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.updateTask(task)
                Logger.d(TAG, "Task updated successfully")
                getAllTasks()
            } catch (e: Exception) {
                Logger.e(TAG, "Error updating task: ${e.message}")
            }
        }
    }

    fun getAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = taskRepository.getAllTasks()
                listTaskLiveData.postValue(list)
                Logger.d(TAG, "getAllTasks: ${list.size}")
            } catch (e: Exception) {
                Logger.e(TAG, "Error getting all tasks: ${e.message}")
            }
        }
    }
}