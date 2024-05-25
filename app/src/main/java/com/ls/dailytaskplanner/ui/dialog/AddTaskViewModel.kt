package com.ls.dailytaskplanner.ui.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ls.dailytaskplanner.App
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.base.BaseViewModel
import com.ls.dailytaskplanner.database.TaskRepository
import com.ls.dailytaskplanner.model.ColorTask
import com.ls.dailytaskplanner.model.Task
import com.ls.dailytaskplanner.model.eventbus.RefreshDataTask
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.Logger
import com.ls.dailytaskplanner.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseViewModel() {

    var showToastLiveData = SingleLiveEvent<String>()
    var listColorLiveData = MutableLiveData<MutableList<ColorTask>>()
    var statusUpdateTask = SingleLiveEvent<Boolean>()


    var task = Task(
        0,
        R.drawable.icon_task,
        "#CCFFFF",
        "",
        "",
        "",
        AppUtils.getCurrentDate(),
        "00:00",
        AppUtils.getCurrentDate(),
        System.currentTimeMillis(),
        false,
        true,
        "",
        "",
        false
    )

    init {
        val listColor = mutableListOf<ColorTask>()
        listColor.add(ColorTask("#FFCCFF"))
        listColor.add(ColorTask("#FFCC66"))
        listColor.add(ColorTask("#FFFF99"))
        listColor.add(ColorTask("#99FF99"))
        listColor.add(ColorTask("#CCFFFF"))
        listColor.add(ColorTask("#CC99FF"))
        listColor.add(ColorTask("#00CC99"))
        listColor.add(ColorTask("#FF3300"))
        listColor.add(ColorTask("#00CCCC"))
        listColor.add(ColorTask("#6633CC"))
        listColor.add(ColorTask("#DDDDDD"))
        listColorLiveData.value = listColor
    }

    fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                task.id = System.currentTimeMillis()
                task.lastTimeModified = System.currentTimeMillis()
                taskRepository.addTask(task)
                showToastLiveData.postValue(App.mInstance.getString(R.string.add_task_success))
                statusUpdateTask.postValue(true)
                EventBus.getDefault().post(RefreshDataTask())
                Logger.d(TAG, "Task added successfully: $task")
            } catch (e: Exception) {
                Logger.e(TAG, "Error adding task: ${e.message}")
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.updateTask(task)
                EventBus.getDefault().post(RefreshDataTask())
                showToastLiveData.postValue(App.mInstance.getString(R.string.task_updated))
                statusUpdateTask.postValue(true)
                Logger.d(TAG, "Task updated successfully")
            } catch (e: Exception) {
                Logger.e(TAG, "Error updating task: ${e.message}")
            }
        }
    }

    companion object {
        const val TAG = "AddTaskViewModel"
    }
}