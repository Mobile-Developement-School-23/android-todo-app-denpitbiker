package com.advancedsolutionsdevelopers.todoapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.provider.Settings
import com.advancedsolutionsdevelopers.todoapp.data.database.AppDatabase
import com.advancedsolutionsdevelopers.todoapp.data.network.NetCallback
import com.advancedsolutionsdevelopers.todoapp.data.network.Retrofit
import com.advancedsolutionsdevelopers.todoapp.data.network.ToDoService
import com.advancedsolutionsdevelopers.todoapp.data.network.models.SingleItemResponse
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.device_id_key
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.lkr_key
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.sp_name
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.token_key
import com.advancedsolutionsdevelopers.todoapp.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.create
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean

object TodoItemsRepository {
    private lateinit var service: ToDoService
    lateinit var database: AppDatabase
    lateinit var sp: SharedPreferences
    lateinit var connectivityManager: ConnectivityManager
    private val converter = Converters()
    private var isOnlineMode = false
    var isOnline = AtomicBoolean(false)
    private val _codeChannel = Channel<Int>()
    val codeChannel = _codeChannel.receiveAsFlow()

    @SuppressLint("HardwareIds")
    fun init(context: Context) {
        service = Retrofit.getInstance(context).create()
        database = AppDatabase.getDatabase(context)
        sp = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        if (!sp.contains(device_id_key)) {
            sp.edit().putString(
                device_id_key, Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            ).apply()
        }
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        isOnlineMode = sp.contains(token_key)
        connectivityManager.registerDefaultNetworkCallback(NetCallback())
    }

    fun changeConnectionMode(dropData: Boolean = false) {
        isOnlineMode = sp.contains(token_key)
        if (dropData) {
            sp.edit().remove(token_key).remove(lkr_key).apply()
            CoroutineScope(Dispatchers.IO).launch {
                database.toDoItemDao().deleteTable()
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
                val response = service.getTasksList()
                updateRevision(response.body()!!.revision!!)
                val x = synchronizeData(response.body()!!.list)
                for (i in x[1]) {
                    deleteTask(i, true)
                }
                for (i in x[3]) {
                    service.addTask(SingleItemResponse(i))
                }
                for (i in x[2]) {
                    database.toDoItemDao().insertItem(i)
                }
                for (i in x[0]) {
                    updateTask(i, true)
                }
                _codeChannel.send(200)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        } else {
            _codeChannel.send(600)
        }
    }

    private suspend fun synchronizeData(serverData: List<TodoItem>): ArrayList<ArrayList<TodoItem>> {
        val res = ArrayList<ArrayList<TodoItem>>()
        for (i in 0..3) res.add(arrayListOf())
        val added = mutableSetOf<String>()
        for (i in serverData) {
            val item = database.toDoItemDao().getItemByIdNoFlow(i.id)
            if (item == null) {
                res[2].add(i)//newItemsFromServ
                added.add(i.id)
            } else {
                if (i.lastEditDate > item.lastEditDate) {
                    res[0].add(i)//needUpdateSomewhere
                    added.add(i.id)
                } else if (i.lastEditDate < item.lastEditDate) {
                    if (item.isDeleted) {
                        res[1].add(item)//deletedOnDevice(When was no internet)
                    } else {
                        res[0].add(item)//needUpdateSomewhere
                    }
                    added.add(item.id)
                } else if (item.isDeleted) {
                    res[1].add(item)//deletedOnDevice(When was no internet)
                    added.add(item.id)
                } else {
                    added.add(item.id)
                }
            }
        }
        for (i in database.toDoItemDao().getAllNoFlow()) {
            if (i.id !in added && !i.isDeleted) {
                res[3].add(i)//newItemsFromDB
            }
        }
        return res
    }

    fun getTaskById(id: String): Flow<TodoItem?> {
        return database.toDoItemDao().getItemById(id)
    }

    suspend fun addTask(todoItem: TodoItem, mute: Boolean = false) {
        database.toDoItemDao().insertItem(todoItem)
        if (isOnlineMode) {
            try {
                handleResponse(service.addTask(SingleItemResponse(todoItem)), mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }

        }
    }

    suspend fun updateTask(todoItem: TodoItem, mute: Boolean = false) {
        database.toDoItemDao().updateItem(todoItem)
        if (isOnlineMode) {
            try {
                handleResponse(service.updateTask(todoItem.id, SingleItemResponse(todoItem)), mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        }
    }

    suspend fun deleteTask(todoItem: TodoItem, mute: Boolean = false) {
        if (isOnlineMode && isOnline.get()) {
            database.toDoItemDao().deleteItem(todoItem)
            try {
                val response = service.deleteTask(todoItem.id)
                handleResponse(response, mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        } else if (isOnlineMode) {
            todoItem.isDeleted = true
            todoItem.lastEditDate = converter.dateToTimestamp(LocalDateTime.now())
            database.toDoItemDao().updateItem(todoItem)
        } else {
            database.toDoItemDao().deleteItem(todoItem)
        }
    }

    private fun updateRevision(code: Int) {
        sp.edit().putInt(lkr_key, code).apply()
    }
}