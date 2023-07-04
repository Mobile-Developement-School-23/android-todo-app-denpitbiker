package com.advancedsolutionsdevelopers.todoapp.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.is_online_key
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.sp_name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Фоновый загрузчик данных
class BackgroundSyncWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            if (context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
                    .getBoolean(is_online_key, false)
            ) {
                withContext(Dispatchers.IO){
                    TodoItemsRepository.init(context)
                    TodoItemsRepository.syncWithServer()
                }
            }
        } catch (ex: Exception) {
            return Result.retry()
        }finally {
            TodoItemsRepository.destroy()
        }
        return Result.success()
    }

}