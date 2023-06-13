package com.advancedsolutionsdevelopers.todoapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.advancedsolutionsdevelopers.todoapp.recyclerView.TodoItem
import java.util.ArrayList

class TasksListViewModel : ViewModel() {
    var tasks: MutableLiveData<ArrayList<TodoItem>> = MutableLiveData(arrayListOf())
    fun deleteItem(index: Int) {
        val tmp = tasks.value!!
        tmp.removeAt(index)
        tasks.value= tmp
    }
}