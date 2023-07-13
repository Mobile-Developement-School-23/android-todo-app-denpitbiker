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
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.DEVICE_ID_KEY
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
    private val saveItemUseCase: SaveItemUseCase,
    private val sp:SharedPreferences
) :
    ViewModel() {
    var isNew = mutableStateOf(false)
    var etText = mutableStateOf("")
    var priority = Priority.basic
    var task = blankTodoItem()
    var isCalendarWasOpen = false
    var switchChecked = mutableStateOf(false)
    var mDate = mutableStateOf("")
    var deadlineDate: Long = 0L
    private val converter: TimeFormatConverters = TimeFormatConverters()
    fun getItem(id: String): Flow<TodoItem?> = getItemUseCase(id)
    fun deleteItem() {
        if(!isNew.value){
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
            if (isNew.value) UUID.randomUUID().toString() else task.id,
            etText.value,
            priority,
            if (isNew.value) false else task.isCompleted,
            if (isNew.value) curDate else task.creationDate,
            curDate,
            sp.getString(DEVICE_ID_KEY,"")!!,
            false,
            DeadlineIdentifier().identify(
                isNew.value,
                isCalendarWasOpen,
                switchChecked.value,
                deadlineDate,
                task
            )
        )
    }
}