package com.advancedsolutionsdevelopers.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TodoItemsRepository.init(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        TodoItemsRepository.destroy()
    }

}