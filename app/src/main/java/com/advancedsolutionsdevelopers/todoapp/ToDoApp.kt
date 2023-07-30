package com.advancedsolutionsdevelopers.todoapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.advancedsolutionsdevelopers.todoapp.data.BackgroundSyncWorker
import com.advancedsolutionsdevelopers.todoapp.data.BackgroundWorkerFactory
import com.advancedsolutionsdevelopers.todoapp.di.component.ApplicationComponent
import com.advancedsolutionsdevelopers.todoapp.di.component.DaggerApplicationComponent
import com.advancedsolutionsdevelopers.todoapp.utils.Constant
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.SP_NAME
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.THEME_CODE_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.WORKER_NAME
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ToDoApp : Application() {
    private var _applicationComponent: ApplicationComponent? = null
    val applicationComponent: ApplicationComponent
        get() = requireNotNull(_applicationComponent!!) {
            "AppComponent must not be null!"
        }

    @Inject
    lateinit var workerFactory: BackgroundWorkerFactory
    private val configurationWorker by lazy {
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        _applicationComponent = DaggerApplicationComponent.factory().create(this)
        applicationComponent.inject(this)
        WorkManager.initialize(this, configurationWorker)
        val sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(sp.getInt(THEME_CODE_KEY, -1))
        if (!sp.contains(Constant.DEVICE_ID_KEY)) {
            sp.edit().putString(
                Constant.DEVICE_ID_KEY, Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            ).apply()
        }
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
                WORKER_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                myWorkRequest
            )
    }

}