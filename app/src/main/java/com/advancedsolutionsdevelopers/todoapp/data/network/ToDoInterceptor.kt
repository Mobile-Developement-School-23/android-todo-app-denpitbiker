package com.advancedsolutionsdevelopers.todoapp.data.network

import android.content.Context
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.auth_header
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.last_known_header
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.lkr_key
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.sp_name
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.token_key
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
//Интерсептор запроса в сеть
class ToDoInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val sp = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        val lastRev = sp.getInt(lkr_key, 0)
        val newReq = request.newBuilder()
            .header(auth_header, "OAuth " + sp.getString(token_key, ""))
            .header(last_known_header, lastRev.toString()).build()
        return chain.proceed(newReq)
    }
}