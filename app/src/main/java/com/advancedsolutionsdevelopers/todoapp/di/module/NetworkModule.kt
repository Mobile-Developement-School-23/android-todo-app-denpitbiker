package com.advancedsolutionsdevelopers.todoapp.di.module

import android.content.Context
import com.advancedsolutionsdevelopers.todoapp.data.network.ToDoInterceptor
import com.advancedsolutionsdevelopers.todoapp.data.network.ToDoService
import com.advancedsolutionsdevelopers.todoapp.di.ApplicationScope
import com.advancedsolutionsdevelopers.todoapp.utils.Constant
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

//модуль сети(создает ее инстанс)
@Module
interface NetworkModule {
    companion object {
        @Provides
        @ApplicationScope
        fun provideApiUrl(): String = "https://beta.mrdekk.ru/todobackend/"

        @Provides
        @ApplicationScope
        fun provideNetworkClient(context: Context): OkHttpClient {
            val sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
            return OkHttpClient().newBuilder().callTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(ToDoInterceptor(sp)).addInterceptor(HttpLoggingInterceptor().also {
                    it.level = HttpLoggingInterceptor.Level.BODY
                }).build()
        }


        @Provides
        @ApplicationScope
        fun provideRetrofit(
            url: String,
            client: OkHttpClient
        ): Retrofit = Retrofit.Builder().baseUrl(url).client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

        @Provides
        @ApplicationScope
        fun provideApi(retrofit: Retrofit): ToDoService = retrofit.create(ToDoService::class.java)
    }
}