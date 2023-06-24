package com.advancedsolutionsdevelopers.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.advancedsolutionsdevelopers.todoapp.data.TasksListViewModel
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken


class MainActivity : AppCompatActivity() {
    private val REQUEST_LOGIN_SDK = 1
    lateinit var sdk: YandexAuthSdk
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel: TasksListViewModel by viewModels()
        viewModel.tasks.value = TodoItemsRepository().getTasks()
        sdk = YandexAuthSdk(
            this, YandexAuthOptions(this)
        )
        val loginOptionsBuilder = YandexAuthLoginOptions.Builder()
        val intent: Intent = sdk.createLoginIntent(loginOptionsBuilder.build())
        startActivityForResult(intent, REQUEST_LOGIN_SDK)
    }
    //TODO arl
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //TODO спрашивать синхронизировать ли их задачи с яндексом(записать на сервер)
        // тк иначе при поздней авторизации они удалятся
        if (requestCode == REQUEST_LOGIN_SDK) {
            try {
                val yandexAuthToken: YandexAuthToken = sdk.extractToken(resultCode, data)!!
                if (yandexAuthToken != null) {
                    //TODO
                    Toast.makeText(this, yandexAuthToken.toString(), Toast.LENGTH_SHORT).show()
                }
            } catch (e: YandexAuthException) {
                Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}