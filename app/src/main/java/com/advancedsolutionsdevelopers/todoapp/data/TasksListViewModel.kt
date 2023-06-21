package com.advancedsolutionsdevelopers.todoapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.ArrayList

class TasksListViewModel : ViewModel() {
    var tasks: MutableLiveData<ArrayList<TodoItem>> = MutableLiveData(arrayListOf())
    fun deleteItem(item: TodoItem) {
        val tmp = tasks.value!!
        tmp.remove(item)
        tasks.value = tmp
    }
}