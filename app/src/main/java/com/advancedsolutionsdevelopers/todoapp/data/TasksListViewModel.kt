package com.advancedsolutionsdevelopers.todoapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TasksListViewModel : ViewModel() {
    val tasks = MutableLiveData<List<TodoItem>>()
    val tasksUncompleted = MutableLiveData<List<TodoItem>>()
    val completedTasksCount = MutableLiveData<Int>()
    val serverCodes = MutableLiveData<Int>()
    var tasksJob: Job? = null
    var uncompletedTasksJob: Job? = null
    var completeTasksCountJob: Job? = null
    var serverCodesJob: Job? = null
    var syncWithServerJob: Job? = null
    fun subscribeOnTasksChanges() {
        tasksJob?.cancelIfInUse()
        tasksJob = viewModelScope.launch {
            TodoItemsRepository.database.toDoItemDao().getAll().collect {
                tasks.value = it
            }
        }
    }

    fun unsubscribeOnTasksChanges() {
        tasksJob?.cancel()
    }

    fun subscribeOnUncompletedTasksChanges() {
        uncompletedTasksJob?.cancelIfInUse()
        uncompletedTasksJob = viewModelScope.launch {
            TodoItemsRepository.database.toDoItemDao().getAllUncompleted().collect {
                tasksUncompleted.value = it
            }
        }
    }

    fun unsubscribeOnUncompletedTasksChanges() {
        uncompletedTasksJob?.cancel()
    }

    fun subscribeOnCompletedTasksCount() {
        completeTasksCountJob?.cancelIfInUse()
        completeTasksCountJob = viewModelScope.launch {
            TodoItemsRepository.database.toDoItemDao().getNumOfCompleted().collect {
                completedTasksCount.value = it
            }
        }
    }

    fun unsubscribeOnCompletedTasksCount() {
        completeTasksCountJob?.cancel()
    }

    fun subscribeOnServerCodes(){
        serverCodesJob?.cancelIfInUse()
        serverCodesJob = viewModelScope.launch {
            TodoItemsRepository.codeChannel.collect{
                serverCodes.value=it
            }
        }
    }

    fun unsubscribeOnServerCodes(){
        serverCodesJob?.cancel()
    }
    fun syncWithServer(){
        syncWithServerJob?.cancelIfInUse()
        syncWithServerJob=viewModelScope.launch(Dispatchers.IO) {
            TodoItemsRepository.syncWithServer()
        }
    }

    private fun Job.cancelIfInUse() {
        if (!isCompleted)
            cancel()
    }
}