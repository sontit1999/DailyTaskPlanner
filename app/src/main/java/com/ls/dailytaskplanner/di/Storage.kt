package com.ls.dailytaskplanner.di


import android.content.Context
import androidx.room.Room
import com.ls.dailytaskplanner.database.AppDatabase
import com.ls.dailytaskplanner.database.TaskDao
import com.ls.dailytaskplanner.database.storage.LocalStorage
import com.ls.dailytaskplanner.database.storage.SharedPreferencesStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Storage {

    const val dbName = "daily_task_planner.db"

    @Singleton
    @Provides
    fun localStorage(pre: SharedPreferencesStorage): LocalStorage = pre

    @Singleton
    @Provides
    fun appDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideTaskDao(
        appDatabase: AppDatabase
    ): TaskDao {
        return appDatabase.taskDao()
    }

   /* private val migrationFrom1To2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `Wallpaper` ADD COLUMN `hasShared` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE `Ringtone` ADD COLUMN `hasShared` INTEGER NOT NULL DEFAULT 0")
        }
    }*/

}