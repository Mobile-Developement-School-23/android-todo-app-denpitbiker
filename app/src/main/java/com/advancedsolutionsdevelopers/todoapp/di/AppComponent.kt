package com.advancedsolutionsdevelopers.todoapp.di

import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository

interface AppComponent {
    fun inject(repository:TodoItemsRepository)
}