package com.advancedsolutionsdevelopers.todoapp.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit


//TODO Позже перепишу на DI
abstract class Retrofit {
    companion object {
        @Volatile
        private var INSTANCE: Retrofit? = null
        private const val baseUrl = "https://beta.mrdekk.ru/todobackend/"
        fun getInstance(context: Context): Retrofit {
            val okHttpClient =
                OkHttpClient().newBuilder().addInterceptor(ToDoInterceptor(context)).build()
            return INSTANCE ?: Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
        }
    }
}