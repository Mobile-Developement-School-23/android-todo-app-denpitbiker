package com.advancedsolutionsdevelopers.todoapp.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Фоновый загрузчик данных
class BackgroundSyncWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            withContext(Dispatchers.IO) {
                TodoItemsRepository.init(context)
                TodoItemsRepository.syncWithServer()
            }
        } catch (ex: Exception) {
            return Result.retry()
        }
        return Result.success()
    }

}