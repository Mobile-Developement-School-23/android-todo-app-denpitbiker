package com.advancedsolutionsdevelopers.todoapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.di.component.MainActivityComponent
import com.advancedsolutionsdevelopers.todoapp.utils.applicationComponent


class MainActivity : AppCompatActivity() {
    lateinit var activityComponent: MainActivityComponent
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        activityComponent = applicationComponent.mainActivityComponent()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}