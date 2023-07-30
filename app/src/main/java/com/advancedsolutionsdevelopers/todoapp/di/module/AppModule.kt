package com.advancedsolutionsdevelopers.todoapp.di.module

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.advancedsolutionsdevelopers.todoapp.di.ApplicationScope
import com.advancedsolutionsdevelopers.todoapp.domain.IAlarmScheduler
import com.advancedsolutionsdevelopers.todoapp.presentation.receiver.AlarmScheduler
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.SP_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides

//Делает вид, что главный
@Module
interface AppModule {
    companion object{
        @Provides
        @ApplicationScope
        fun provideSharedPreferences(context:Context): SharedPreferences =
            context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

        @Provides
        @ApplicationScope
        fun provideConnectivityManager(context: Context): ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    @Binds
    @ApplicationScope
    fun provideAlarmScheduler(manager: AlarmScheduler): IAlarmScheduler
}