package com.advancedsolutionsdevelopers.todoapp.di.component

import android.content.Context
import com.advancedsolutionsdevelopers.todoapp.ToDoApp
import com.advancedsolutionsdevelopers.todoapp.di.module.AppModule
import com.advancedsolutionsdevelopers.todoapp.di.ApplicationScope
import com.advancedsolutionsdevelopers.todoapp.di.module.DatabaseModule
import com.advancedsolutionsdevelopers.todoapp.di.module.NetworkModule
import dagger.BindsInstance
import dagger.Component
//компонент приложения
@ApplicationScope
@Component(modules = [DatabaseModule::class, NetworkModule::class, AppModule::class])
interface ApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

    fun mainActivityComponent(): MainActivityComponent
    fun inject(app: ToDoApp)
}