package com.advancedsolutionsdevelopers.todoapp.presentation.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.domain.IAlarmScheduler
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_UUID_NOTIFICATION_KEY
import java.util.Calendar
import javax.inject.Inject


class AlarmScheduler @Inject constructor(
    private val context: Context
) : IAlarmScheduler {
    private val manager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleNotification(task: TodoItem) {
        val deadline = task.deadlineDate
        if (deadline != null && !task.isCompleted) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(TASK_UUID_NOTIFICATION_KEY, task.id)
            }
            val pendingID = task.id.hashCode()
            val pending = PendingIntent.getBroadcast(
                context,
                pendingID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = deadline * 1000
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pending
            )
        }
    }

    override fun cancelNotification(task: TodoItem) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingID = task.id.hashCode()
        val pending = PendingIntent.getBroadcast(
            context,
            pendingID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        manager.cancel(pending)
    }
}