package com.advancedsolutionsdevelopers.todoapp.di.module

import android.content.Context
import androidx.room.Room
import com.advancedsolutionsdevelopers.todoapp.data.database.AppDatabase
import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import com.advancedsolutionsdevelopers.todoapp.di.ApplicationScope
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.DB_NAME
import dagger.Module
import dagger.Provides
//модуль БД(создает ее инстанс)
@Module
interface DatabaseModule {
    companion object {
        @Provides
        @ApplicationScope
        fun provideDatabase(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        )
            .build()

        @Provides
        @ApplicationScope
        fun provideTaskDao(database: AppDatabase): ToDoItemDao = database.toDoItemDao()
    }
}