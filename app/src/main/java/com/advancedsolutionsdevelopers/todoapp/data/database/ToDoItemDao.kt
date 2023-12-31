package com.advancedsolutionsdevelopers.todoapp.data.database


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import kotlinx.coroutines.flow.Flow
//Интерфейс методов к таблице ToDoItem
@Dao
interface ToDoItemDao {

    @Query("SELECT * FROM TodoItem WHERE isDeleted=0 ORDER BY lastEditDate DESC")
    fun getAll(): Flow<List<TodoItem>>

    @Query("SELECT * FROM TodoItem WHERE id=:id")
    fun getItemById(id: String): Flow<TodoItem?>

    @Query("SELECT * FROM TodoItem WHERE id=:id")
    fun getItemByIdNoFlow(id: String): TodoItem?

    @Query("SELECT * FROM TodoItem WHERE isCompleted=0 AND isDeleted=0 ORDER BY lastEditDate DESC")
    fun getAllUncompleted(): Flow<List<TodoItem>>

    @Query("SELECT COUNT(*) FROM TodoItem WHERE isCompleted=1 AND isDeleted=0")
    fun getNumOfCompleted(): Flow<Int>

    @Query("SELECT * FROM TodoItem WHERE isDeleted=0")
    fun getAllNoFlow(): List<TodoItem>

    @Insert
    suspend fun insertAll(items: List<TodoItem>)

    @Update
    suspend fun updateItem(todoItem: TodoItem)

    @Insert
    suspend fun insertItem(item: TodoItem)

    @Delete
    suspend fun deleteItem(item: TodoItem)

    @Query("DELETE FROM TodoItem")
    fun deleteTable()
}