package com.advancedsolutionsdevelopers.todoapp.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Фоновый загрузчик данных
class BackgroundSyncWorker @AssistedInject constructor(
    @Assisted context: Context, @Assisted params: WorkerParameters, private val repo: TodoItemsRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            withContext(Dispatchers.IO) {
                repo.syncWithServer()
            }
        } catch (ex: Exception) {
            return Result.retry()
        }
        return Result.success()
    }
    @AssistedFactory
    interface Factory {
        fun create(context: Context, workerParameters: WorkerParameters): BackgroundSyncWorker
    }
}