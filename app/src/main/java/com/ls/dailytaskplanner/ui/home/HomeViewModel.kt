package com.ls.dailytaskplanner.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ls.dailytaskplanner.App
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.base.BaseViewModel
import com.ls.dailytaskplanner.database.TaskRepository
import com.ls.dailytaskplanner.model.Task
import com.ls.dailytaskplanner.model.eventbus.RefreshDataTask
import com.ls.dailytaskplanner.utils.AllEvents
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.SingleLiveEvent
import com.ls.dailytaskplanner.utils.TrackingHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseViewModel() {

    var listTaskLiveData = MutableLiveData<MutableList<Task>>()
    var showToastLiveData = SingleLiveEvent<String>()
    var statusDoneAllTaskLiveData = SingleLiveEvent<Boolean>()

    companion object {
        const val TAG = "HomeViewModel"
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.updateTask(task)
                EventBus.getDefault().post(RefreshDataTask())
                Logger.d(TAG, "Task updated successfully")
            } catch (e: Exception) {
                Logger.e(TAG, "Error updating task: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.deleteTask(task)
                showToastLiveData.postValue(App.mInstance.getString(R.string.task_deleted))
                TrackingHelper.logEvent(AllEvents.TASK_REMOVE)
                Logger.d(TAG, "Task deleted successfully")
            } catch (e: Exception) {
                Logger.e(TAG, "Error deleting task: ${e.message}")
            }
        }
    }


    fun getTaskByDate(dateString: String,isFirst : Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listTask = taskRepository.getTasksByDate(dateString)
                listTaskLiveData.postValue(listTask)
                if (listTask.isNotEmpty() && !isFirst) {
                    statusDoneAllTaskLiveData.postValue(listTask.all { it.isCompleted })
                }
                Logger.d(TAG, "getTaskByDate $dateString size: ${listTask.size}")

            } catch (e: Exception) {
                Logger.e(TAG, "Error getting all tasks: ${e.message}")
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