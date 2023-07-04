package com.advancedsolutionsdevelopers.todoapp

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.advancedsolutionsdevelopers.todoapp.data.BackgroundSyncWorker
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.worker_name
import java.util.concurrent.TimeUnit


class ToDoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        addSyncWorker()
    }

    /*Запускаем переодическое обновление данных в фоне
    (раз в 8 часов, в случае ошибки - работа попытается перезапуститься через 10 минут)*/
    private fun addSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()
        val myWorkRequest = PeriodicWorkRequestBuilder<BackgroundSyncWorker>(
            8,
            TimeUnit.HOURS
        ).setConstraints(constraints).setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                worker_name,
                ExistingPeriodicWorkPolicy.UPDATE,
                myWorkRequest
            )
    }
}