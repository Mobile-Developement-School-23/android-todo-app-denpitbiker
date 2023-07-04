package com.advancedsolutionsdevelopers.todoapp.data.network

import com.advancedsolutionsdevelopers.todoapp.data.network.models.ArrayResponse
import com.advancedsolutionsdevelopers.todoapp.data.network.models.SingleItemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ToDoService {

    @GET("list")
    suspend fun getTasksList(): Response<ArrayResponse>

    @GET("list/{id}")
    suspend fun getTaskById(@Path("id") id: String): Response<SingleItemResponse>

    @PATCH("list")
    suspend fun updateTasksList(@Body list: ArrayResponse): Response<ArrayResponse>

    @POST("list")
    suspend fun addTask(@Body item: SingleItemResponse): Response<SingleItemResponse>

    @PUT("list/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body item: SingleItemResponse
    ): Response<SingleItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<SingleItemResponse>
}