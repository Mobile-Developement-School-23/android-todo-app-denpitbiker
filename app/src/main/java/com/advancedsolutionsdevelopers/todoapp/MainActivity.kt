package com.advancedsolutionsdevelopers.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.advancedsolutionsdevelopers.todoapp.data.TasksListViewModel
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel: TasksListViewModel by viewModels()
        viewModel.tasks.value = TodoItemsRepository().getTasks()
    }
}