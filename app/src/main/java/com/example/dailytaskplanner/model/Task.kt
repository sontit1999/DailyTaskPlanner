package com.example.dailytaskplanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey var id: Long,
    var icon : Int,
    var color : String,
    var title: String,
    var description: String,
    var category: String,
    var dateStart: String,
    var timeStart: String,
    var dateCreated: String,
    var lastTimeModified: Long,
    var isCompleted: Boolean,
    var isReminder: Boolean,
    var timeReminder : String,
    var subTasks: String,
    var didReminder: Boolean,
)

@Entity(tableName = "subtask")
data class SubTask(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,

)

