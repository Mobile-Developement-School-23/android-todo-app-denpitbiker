package com.advancedsolutionsdevelopers.todoapp.presentation.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.domain.IAlarmScheduler
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.CHANNEL_ID
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.CHANNEL_NAME
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_ID_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_UUID_NOTIFICATION_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.applicationComponent
import com.advancedsolutionsdevelopers.todoapp.utils.toTextFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class NotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var scheduler: IAlarmScheduler

    @Inject
    lateinit var repository: TodoItemsRepository

    override fun onReceive(context: Context, intent: Intent) {
        println(System.currentTimeMillis())
        context.applicationComponent.inject(this)
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val id: String = getID(intent.extras)
                when (val task = repository.getTaskByIdNoFlow(id)) {
                    null -> {}
                    else -> {
                        val manager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        manager.createNotificationChannel(buildChannel())
                        val notification = buildNotification(
                            context,
                            R.drawable.check,
                            task.priority.toTextFormat(context),
                            "${task.text.padEnd(20, ' ').substring(0, 20).trim()}...",
                            buildNavigationIntent(context, id),
                            buildPostponeIntent(context, id)
                        )
                        scheduler.cancelNotification(task)
                        manager.notify(id.hashCode(), notification)
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

    private fun buildNavigationIntent(context: Context, id: String): PendingIntent =
        NavDeepLinkBuilder(context).setGraph(R.navigation.nav_graph)
            .setDestination(R.id.taskFragment).setArguments(bundleOf(TASK_ID_KEY to id))
            .createPendingIntent()

    private fun buildPostponeIntent(context: Context, id: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context, id.hashCode(), Intent(context, PostponeReceiver::class.java).apply {
                putExtra(TASK_UUID_NOTIFICATION_KEY, id)
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildNotification(
        context: Context,
        icon: Int,
        title: String,
        text: String,
        contentIntent: PendingIntent,
        deferringIntent: PendingIntent
    ): Notification =
        Notification.Builder(context, CHANNEL_ID).setSmallIcon(icon).setContentTitle(title)
            .setContentText(text).setContentIntent(contentIntent).setAutoCancel(true).addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(context, R.drawable.close),
                    context.getString(R.string.postpone),
                    deferringIntent
                ).build()
            ).build()

    private fun buildChannel() = NotificationChannel(
        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
    )
}

























