package com.example.dailytaskplanner.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailytaskplanner.base.BaseViewModel
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
) : BaseViewModel() {

    var listTaskLiveData = MutableLiveData<MutableList<Task>>()

    companion object {
        const val TAG = "HomeViewModel"
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.updateTask(task)
                Logger.d(TAG, "Task updated successfully")
            } catch (e: Exception) {
                Logger.e(TAG, "Error updating task: ${e.message}")
            }
        }
    }

    fun getAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.getAllTasks().collect {
                    listTaskLiveData.postValue(it)
                    Logger.d(TAG, "getAllTasks: ${it.size}")
                }
            } catch (e: Exception) {
                Logger.e(TAG, "Error getting all tasks: ${e.message}")
            }
        }
    }
}