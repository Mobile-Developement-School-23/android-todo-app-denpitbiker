package com.advancedsolutionsdevelopers.todoapp.data

import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.network.NetCallback
import com.advancedsolutionsdevelopers.todoapp.data.network.ToDoService
import com.advancedsolutionsdevelopers.todoapp.data.network.models.SingleItemResponse
import com.advancedsolutionsdevelopers.todoapp.di.ApplicationScope
import com.advancedsolutionsdevelopers.todoapp.domain.IAlarmScheduler
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.LKR_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TOKEN_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import com.advancedsolutionsdevelopers.todoapp.utils.ItemsMerger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

//Собственно, репозиторий
@ApplicationScope

class TodoItemsRepository @Inject constructor(
    private val table: ToDoItemDao, private val service: ToDoService, val sp: SharedPreferences,
    val connectManager: ConnectivityManager,     private val scheduler: IAlarmScheduler
) {
    private val converter = TimeFormatConverters()
    private val merger = ItemsMerger()
    private var isOnlineMode = sp.contains(TOKEN_KEY)
    private val _codeChannel = Channel<Int>()
    var isOnline = AtomicBoolean(false)
    val codeChannel = _codeChannel.receiveAsFlow()

    init {
        connectManager.registerDefaultNetworkCallback(NetCallback(this))
    }

    fun changeConnectionMode(dropData: Boolean = false) {
        isOnlineMode = sp.contains(TOKEN_KEY)
        if (dropData) {
            sp.edit().remove(TOKEN_KEY).remove(LKR_KEY).apply()
            CoroutineScope(Dispatchers.IO).launch {
                table.deleteTable()
            }
        }
    }

    private suspend fun <T> handleResponse(response: Response<T>, mute: Boolean = false) {
        if (!mute) _codeChannel.send(response.code())
        when (response.code()) {
            400, 404 -> syncWithServer()
            200 -> updateRevision((response.body()!! as SingleItemResponse).revision!!)
        }
    }

    suspend fun syncWithServer() {
        if (isOnlineMode) {
            try {
                syncAttempt()
                _codeChannel.send(200)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        } else {
            _codeChannel.send(600)
        }
    }

    private suspend fun syncAttempt() {
        val response = service.getTasksList()
        val revision = response.body()!!.revision!!
        if (revision > sp.getInt(LKR_KEY, 0)) {
            updateRevision(revision)
            dropTableAndInsertList(response.body()!!.list)
        } else {
            val x = merger.merge(response.body()!!.list, table.getAllNoFlow())
            for (i in x.deletedOnDevice) {
                deleteTask(i, true)
            }
            for (i in x.newItemsFromDB) {
                service.addTask(SingleItemResponse(i))
            }
            for (i in x.newItemsFromServ) {
                scheduler.scheduleNotification(i)
                table.insertItem(i)
            }
            for (i in x.needUpdate) {
                updateTask(i, true)
            }
        }
    }

    private suspend fun dropTableAndInsertList(list: List<TodoItem>) {
        table.deleteTable()
        table.insertAll(list)
    }

    fun getTaskById(id: String): Flow<TodoItem?> {
        return table.getItemById(id)
    }
    fun getTaskByIdNoFlow(id: String): TodoItem? {
        return table.getItemByIdNoFlow(id)
    }

    suspend fun addTask(todoItem: TodoItem, mute: Boolean = false) {
        table.insertItem(todoItem)
        scheduler.scheduleNotification(todoItem)
        if (isOnlineMode) {
            try {
                handleResponse(service.addTask(SingleItemResponse(todoItem)), mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }

        }
    }

    suspend fun updateTask(todoItem: TodoItem, mute: Boolean = false) {
        table.updateItem(todoItem)
        scheduler.scheduleNotification(todoItem)
        if (isOnlineMode) {
            try {
                handleResponse(service.updateTask(todoItem.id, SingleItemResponse(todoItem)), mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        }
    }

    suspend fun deleteTask(todoItem: TodoItem, mute: Boolean = false) {
        scheduler.cancelNotification(todoItem)
        if (isOnlineMode && isOnline.get()) {
            table.deleteItem(todoItem)
            try {
                val response = service.deleteTask(todoItem.id)
                handleResponse(response, mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        } else if (isOnlineMode) {
            todoItem.isDeleted = true
            todoItem.lastEditDate = converter.dateToTimestamp(LocalDateTime.now())
            table.updateItem(todoItem)
        } else {
            table.deleteItem(todoItem)
        }
    }

    private fun updateRevision(code: Int) {
        sp.edit().putInt(LKR_KEY, code).apply()
    }
}