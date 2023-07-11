package com.advancedsolutionsdevelopers.todoapp.data

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
//фабрика для фонового загрузчика
class BackgroundWorkerFactory @Inject constructor(private val updateDataWorker: BackgroundSyncWorker.Factory) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return when (workerClassName) {
            BackgroundSyncWorker::class.java.name ->{
                updateDataWorker.create(appContext,workerParameters)
            }
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }
    }
}