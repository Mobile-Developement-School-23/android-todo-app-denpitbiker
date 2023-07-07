package com.advancedsolutionsdevelopers.todoapp.utils

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import java.time.LocalDateTime

class DeadlineIdentifier {
    private val converter: TimeFormatConverters = TimeFormatConverters()
    private val curDate = converter.dateToTimestamp(LocalDateTime.now())

    fun identify(
        isNewItem: Boolean,
        isCalWasOpen: Boolean,
        isChecked: Boolean,
        deadlineDate: LocalDateTime,
        task: TodoItem? = null
    ): Long? = if (isNewItem) decideDateNew(
        isChecked,
        isCalWasOpen,
        curDate,
        deadlineDate
    ) else decideDateEdit(isChecked, isCalWasOpen, deadlineDate, task!!)


    private fun decideDateNew(
        isChecked: Boolean,
        isCalWasOpen: Boolean,
        curDate: Long,
        deadlineDate: LocalDateTime
    ): Long? =
        if (isChecked) {
            if (isCalWasOpen) converter.dateToTimestamp(deadlineDate)
            else curDate
        } else null

    private fun decideDateEdit(
        isChecked: Boolean,
        isCalWasOpen: Boolean,
        deadlineDate: LocalDateTime,
        task: TodoItem
    ): Long? =
        if (isChecked) {
            if (isCalWasOpen) converter.dateToTimestamp(deadlineDate)
            else {
                deadlineOrCurrentDate(task)
            }

        } else null

    private fun deadlineOrCurrentDate(task: TodoItem) =
        if (task.deadlineDate != null) task.deadlineDate else curDate
}