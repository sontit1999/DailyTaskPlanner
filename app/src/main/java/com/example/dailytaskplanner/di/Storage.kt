package com.example.dailytaskplanner.di


import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dailytaskplanner.database.AppDatabase
import com.example.dailytaskplanner.database.TaskDao
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