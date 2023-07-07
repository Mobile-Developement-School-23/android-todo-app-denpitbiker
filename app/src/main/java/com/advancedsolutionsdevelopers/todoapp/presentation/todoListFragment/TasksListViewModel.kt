package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_ID_KEY
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.models.blankTodoItem
import com.advancedsolutionsdevelopers.todoapp.domain.ChangeConnectionModeUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.DeleteItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetAllItemsUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetNumberOfCompletedUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetServerCodesUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetUncheckedItemsUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SaveItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SyncWithServerUseCase
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import com.advancedsolutionsdevelopers.todoapp.utils.cancelIfInUse
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.NavigationMode
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.NavigationState
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.ToDoItemUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TasksListViewModel(
    private val deleteItemUseCase: DeleteItemUseCase,
    private val saveItemUseCase: SaveItemUseCase,
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getUncheckedItemsUseCase: GetUncheckedItemsUseCase,
    private val getNumOfCompletedUseCase: GetNumberOfCompletedUseCase,
    private val getServerCodesUseCase: GetServerCodesUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase,
    private val changeConnectionModeUseCase: ChangeConnectionModeUseCase
) : ViewModel() {
    private var syncWithServerJob: Job? = null
    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState = _navigationState.asStateFlow()
    private val converter: TimeFormatConverters = TimeFormatConverters()
    var allTasks = flow {
        getAllItemsUseCase().collect {
            emit(
                it.map { item -> wrapToDoItem(item) } + wrapToDoItem(blankTodoItem())
            )
        }
    }
    val uncompletedTasks = flow {
        getUncheckedItemsUseCase().collect {
            emit(
                it.map { item -> wrapToDoItem(item) } + wrapToDoItem(blankTodoItem())
            )
        }
    }
    val completedTasksCount = flow {
        getNumOfCompletedUseCase().collect {
            emit(it)
        }
    }
    val serverCodes = flow {
        getServerCodesUseCase().collect {
            emit(it)
        }
    }

    fun syncWithServer() {
        syncWithServerJob?.cancelIfInUse()
        syncWithServerJob = viewModelScope.launch(Dispatchers.IO) {
            syncWithServerUseCase()
        }
    }

    fun changeConnectionMode(dropTable: Boolean) = changeConnectionModeUseCase(dropTable)

    private fun deleteItem(item: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteItemUseCase(item)
        }
    }

    private fun updateItem(item: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            saveItemUseCase(item, true)
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
                x.putString(TASK_ID_KEY, id)
                _navigationState.emit(NavigationState(bundle = x, mode = NavigationMode.Item))
            }
        }
    }

    suspend fun onNavigated() {
        _navigationState.emit(NavigationState())
    }
}