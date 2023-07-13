package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem

interface IAlarmScheduler {
    fun scheduleNotification(task: TodoItem)

    fun cancelNotification(task: TodoItem)
}