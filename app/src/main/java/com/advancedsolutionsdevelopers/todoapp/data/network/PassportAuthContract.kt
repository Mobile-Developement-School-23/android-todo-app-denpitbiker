package com.advancedsolutionsdevelopers.todoapp.data.network

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken
import java.lang.Exception

//контракт авторизации через яндекс пасспорт
class PassportAuthContract : ActivityResultContract<Any?, YandexAuthToken?>() {
    lateinit var sdk: YandexAuthSdk
    override fun createIntent(context: Context, input: Any?): Intent {
        sdk = YandexAuthSdk(
            context, YandexAuthOptions(context)
        )
        val loginOptionsBuilder = YandexAuthLoginOptions.Builder()
        return sdk.createLoginIntent(loginOptionsBuilder.build())
    }

    override fun parseResult(resultCode: Int, intent: Intent?): YandexAuthToken? {
        return try {
            sdk.extractToken(resultCode, intent)
        } catch (e: Exception) {
            null
        }

    }
}