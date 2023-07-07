package com.advancedsolutionsdevelopers.todoapp.data.network

import android.content.SharedPreferences
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.AUTH_HEADER
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.LAST_KNOWN_HEADER
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.LKR_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TOKEN_KEY
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

//Интерсептор, вставляющий заголовки
class ToDoInterceptor @Inject constructor(private val sp: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val lastRev = sp.getInt(LKR_KEY, 0)
        val newReq = request.newBuilder()
            .header(AUTH_HEADER, "OAuth " + sp.getString(TOKEN_KEY, ""))
            .header(LAST_KNOWN_HEADER, lastRev.toString()).build()
        return chain.proceed(newReq)
    }
}