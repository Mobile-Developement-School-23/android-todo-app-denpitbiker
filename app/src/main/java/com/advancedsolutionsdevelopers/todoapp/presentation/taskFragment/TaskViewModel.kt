package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.advancedsolutionsdevelopers.todoapp.data.models.Priority
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.models.blankTodoItem
import com.advancedsolutionsdevelopers.todoapp.domain.DeleteItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SaveItemUseCase
import com.advancedsolutionsdevelopers.todoapp.utils.DeadlineIdentifier
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class TaskViewModel(
    private val getItemUseCase: GetItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val saveItemUseCase: SaveItemUseCase
) :
    ViewModel() {
    var isNew = false
    var etText = mutableStateOf("")
    var priority = Priority.basic
    var task = blankTodoItem()
    var isCalendarWasOpen = false
    var switchChecked = mutableStateOf(false)
    var mDate = mutableStateOf("")
    var deadlineDate: LocalDateTime = LocalDateTime.of(2000, 1, 1, 1, 1)
    private val converter: TimeFormatConverters = TimeFormatConverters()
    fun getItem(id: String): Flow<TodoItem?> = getItemUseCase(id)
    fun deleteItem() {
        if(!isNew){
            CoroutineScope(Dispatchers.IO).launch {
                deleteItemUseCase(task)
            }
        }
    }

    fun saveItem(isEditMode: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            saveItemUseCase(generateItem(), isEditMode)
        }
    }
    private fun generateItem(): TodoItem {
        val curDate = converter.dateToTimestamp(LocalDateTime.now())
        return TodoItem(
            if (isNew) UUID.randomUUID().toString() else task.id,
            etText.value,
            priority,
            if (isNew) false else task.isCompleted,
            if (isNew) curDate else task.creationDate,
            curDate,
            "dsaa",
            false,
            DeadlineIdentifier().identify(
                isNew,
                isCalendarWasOpen,
                switchChecked.value,
                deadlineDate,
                task
            )
        )
    }
}