package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment

import android.content.Context
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.SP_NAME
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.domain.ChangeConnectionModeUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SyncWithServerUseCase
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TOKEN_KEY
import com.yandex.authsdk.YandexAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

//обработка результата авторизации
class RegisterResultCallback @Inject constructor(
    private val context: Context,
    repository: TodoItemsRepository
) :
    ActivityResultCallback<YandexAuthToken?> {
    private val changeConnectionModeUseCase = ChangeConnectionModeUseCase(repository)
    private val syncWithServerUseCase = SyncWithServerUseCase(repository)
    override fun onActivityResult(it: YandexAuthToken?) {
        if (it != null) {
            context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                .putString(TOKEN_KEY, it.value).apply()
            Toast.makeText(context, "Authorized!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Oke, login later!", Toast.LENGTH_LONG).show()
        }
        CoroutineScope(Dispatchers.IO).launch {
            changeConnectionModeUseCase(false)
            syncWithServerUseCase()
        }
    }
}