package com.advancedsolutionsdevelopers.todoapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
//Абстрактный класс БД
@Database(entities = [TodoItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toDoItemDao(): ToDoItemDao
}