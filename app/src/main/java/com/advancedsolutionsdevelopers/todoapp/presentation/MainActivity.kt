package com.advancedsolutionsdevelopers.todoapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TodoItemsRepository.init(applicationContext)
    }
}