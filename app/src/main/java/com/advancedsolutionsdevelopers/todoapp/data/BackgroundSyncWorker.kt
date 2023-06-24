package com.advancedsolutionsdevelopers.todoapp.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class BackgroundSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context,params) {
    override suspend fun doWork(): Result {
        try {
            //TODO fetchData
        } catch (ex: Exception) {
            return Result.retry()
        }
        return Result.success()
    }

}