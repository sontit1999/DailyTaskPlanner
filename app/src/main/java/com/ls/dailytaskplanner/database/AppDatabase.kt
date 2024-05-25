package com.ls.dailytaskplanner.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.ls.dailytaskplanner.model.Task

@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}