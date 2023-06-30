package com.advancedsolutionsdevelopers.todoapp.todoListFragment

import android.content.Context
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import com.advancedsolutionsdevelopers.todoapp.data.Constant.sp_name
import com.advancedsolutionsdevelopers.todoapp.data.Constant.token_key
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.yandex.authsdk.YandexAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//обработка результата авторизации
class RegisterResultCallback(private val context: Context, private val fragmentScope: CoroutineScope) :
    ActivityResultCallback<YandexAuthToken?> {
    override fun onActivityResult(it: YandexAuthToken?) {
        if (it != null) {
            context.getSharedPreferences(sp_name, Context.MODE_PRIVATE).edit()
                .putString(token_key, it.value).apply()
            Toast.makeText(context, "Authorized!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Oke, login later!", Toast.LENGTH_LONG).show()
        }
        fragmentScope.launch(Dispatchers.IO) {
            TodoItemsRepository.changeConnectionMode()
            TodoItemsRepository.syncWithServer()
        }
    }
}