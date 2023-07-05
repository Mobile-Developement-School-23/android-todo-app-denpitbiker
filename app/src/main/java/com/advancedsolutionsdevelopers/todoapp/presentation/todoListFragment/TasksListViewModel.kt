package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.task_id_key
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.data.blankTodoItem
import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import com.advancedsolutionsdevelopers.todoapp.domain.DeleteItemUseCase
import com.advancedsolutionsdevelopers.todoapp.utils.Converters
import com.advancedsolutionsdevelopers.todoapp.domain.GetAllItemsUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetNumberOfCompletedUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetServerCodesUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetUncheckedItemsUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SaveItemUseCase
import com.advancedsolutionsdevelopers.todoapp.utils.cancelIfInUse
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.NavigationMode
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.NavigationState
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.ToDoItemUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class TasksListViewModel() : ViewModel() {
    private var syncWithServerJob: Job? = null
    val navigationState = MutableStateFlow(NavigationState())
    private val converter: Converters = Converters()
    /*private val deleteItemUseCase = DeleteItemUseCase(repository)
    private val saveItemUseCase = SaveItemUseCase(repository)
    private val getAllItemsUseCase = GetAllItemsUseCase(dao)
    private val getUncheckedItemsUseCase = GetUncheckedItemsUseCase(dao)
    private val getNumOfCompletedUseCase = GetNumberOfCompletedUseCase(dao)
    private val getServerCodesUseCase = GetServerCodesUseCase(repository)*/
    var allTasks = flow {
        TodoItemsRepository.database.toDoItemDao().getAll().collect {
            emit(
                it.map { item -> wrapToDoItem(item) } + wrapToDoItem(blankTodoItem())
            )
        }
    }
    val uncompletedTasks = flow {
        TodoItemsRepository.database.toDoItemDao().getAllUncompleted().collect {
            emit(
                it.map { item -> wrapToDoItem(item) } + wrapToDoItem(blankTodoItem())
            )
        }
    }
    val completedTasksCount = flow {
        TodoItemsRepository.database.toDoItemDao().getNumOfCompleted().collect {
            emit(it)
        }
    }
    val serverCodes = flow {
        TodoItemsRepository.codeChannel.collect {
            emit(it)
        }
    }

    fun syncWithServer() {
        syncWithServerJob?.cancelIfInUse()
        syncWithServerJob = viewModelScope.launch(Dispatchers.IO) {
            TodoItemsRepository.syncWithServer()
        }
    }

    fun changeConnectionMode() {
        TodoItemsRepository.changeConnectionMode(true)
    }

    private fun deleteItem(item: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoItemsRepository.deleteTask(item)
        }
    }

    private fun updateItem(item: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoItemsRepository.updateTask(item)
        }
    }

    private fun wrapToDoItem(item: TodoItem): ToDoItemUIState {
        return ToDoItemUIState(todoItem = item, onDelete = {
            deleteItem(item)
        }, onCheck = {
            updateItem(
                item.copy(
                    isCompleted = !item.isCompleted, lastEditDate = converter.dateToTimestamp(
                        LocalDateTime.now()
                    )
                )
            )
        },
            onClick = {
                updNavigation(item)
            })
    }

    private fun updNavigation(item: TodoItem) {
        viewModelScope.launch {
            with(item) {
                val x = Bundle()
                x.putString(task_id_key, id)
                navigationState.emit(NavigationState(bundle = x, mode = NavigationMode.Item))
            }
        }
    }
}