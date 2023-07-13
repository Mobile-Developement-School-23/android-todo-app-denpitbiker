package com.advancedsolutionsdevelopers.todoapp.presentation.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_UUID_NOTIFICATION_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import com.advancedsolutionsdevelopers.todoapp.utils.applicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class PostponeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: TodoItemsRepository
    private val converters = TimeFormatConverters()

    override fun onReceive(context: Context, intent: Intent) {
        context.applicationComponent.inject(this)
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val id: String = getID(intent.extras)
                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(id.hashCode())
                when (val task = repository.getTaskByIdNoFlow(id)) {
                    null -> {}
                    else -> {
                        when (val deadline = task.deadlineDate) {
                            null -> {}
                            else -> {
                                try {
                                    repository.updateTask(
                                        task.copy(deadlineDate = converters.plusDay(deadline))
                                    )
                                } catch (exception: Exception) {
                                    Log.d(
                                        this::class.java.toString(),
                                        exception.stackTraceToString()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            Log.d(this::class.java.toString(), exception.stackTraceToString())
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun getID(arguments: Bundle?): String {
        return arguments?.getString(TASK_UUID_NOTIFICATION_KEY)
            ?: throw IllegalArgumentException("Null UUID!")
    }
}