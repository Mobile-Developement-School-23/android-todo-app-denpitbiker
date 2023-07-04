package com.advancedsolutionsdevelopers.todoapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.provider.Settings
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.device_id_key
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.lkr_key
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.sp_name
import com.advancedsolutionsdevelopers.todoapp.data.network.models.ArrayResponse
import com.advancedsolutionsdevelopers.todoapp.data.network.Retrofit
import com.advancedsolutionsdevelopers.todoapp.data.network.models.SingleItemResponse
import com.advancedsolutionsdevelopers.todoapp.data.network.ToDoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.create
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.token_key
import com.advancedsolutionsdevelopers.todoapp.data.database.AppDatabase
import com.advancedsolutionsdevelopers.todoapp.utils.Converters
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

//TODO Result.failure<TodoItem>(IOException())


object TodoItemsRepository {
    private lateinit var service: ToDoService
    lateinit var database: AppDatabase
    lateinit var sp: SharedPreferences
    lateinit var connectivityManager: ConnectivityManager
    private var isOnlineMode = false
    var isOnline = AtomicBoolean(false)
    private val converter = Converters()
    private val _codeChannel = Channel<Int>()
    val codeChannel = _codeChannel.receiveAsFlow()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var checkConnectionJob: Job? = null

    @SuppressLint("HardwareIds")
    fun init(context: Context) {
        service = Retrofit.getInstance(context).create()
        database = AppDatabase.getDatabase(context)
        sp = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        sp.edit().putString(
            device_id_key, Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        ).apply()
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        isOnlineMode = sp.contains(token_key)
        checkConnectionJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(10000)
                if (isOnline()) {
                    if (sp.contains(token_key) && !isOnline.get()) {
                        syncWithServer()
                    }
                }
                isOnline.set(isOnline())
            }
        }

    }

    private fun isOnline(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
        return false
    }

    private fun getNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
    }

    fun changeConnectionMode(dropData: Boolean = false) {
        isOnlineMode = sp.contains(token_key)
        if (dropData) {
            sp.edit().remove(token_key).remove(lkr_key).apply()
            scope.launch {
                database.toDoItemDao().deleteTable()
            }
        }
    }


    private suspend fun handleResponse(
        response: Response<SingleItemResponse>,
        mute: Boolean = false
    ) {
        if (!mute) _codeChannel.send(response.code())
        when (response.code()) {
            400, 404 -> syncWithServer()
            200 -> updateRevision(response.body()!!.revision!!)
        }
    }

    private suspend fun cleanTableAndInsert(list: List<TodoItem>) {
        database.toDoItemDao().deleteTable()
        database.toDoItemDao().insertAll(list)
    }

    suspend fun syncWithServer() {
        if (isOnlineMode && isOnline()) {
            try {
                val response = service.getTasksList()
                val serv_rev = response.body()!!.revision!!
                updateRevision(serv_rev)
                    val response_merged= service.updateTasksList(ArrayResponse(database.toDoItemDao().getAllNoFlow()))
                    cleanTableAndInsert(response.body()!!.list)
                    _codeChannel.send(response.code())
                    when (response_merged.code()) {
                        400, 404 -> syncWithServer()
                        200 -> updateRevision(response_merged.body()!!.revision!!)
                    }
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        } else {
            _codeChannel.send(600)
        }
    }

    //Извиняюсь за говнокод, я перепишу, просто это все исправлялось в 2ч ночи после работы:)))
    private suspend fun synchronizeData(serverData: List<TodoItem>): Pair<Pair<List<TodoItem>, List<TodoItem>>, Pair<List<TodoItem>, List<TodoItem>>> {
        val resArr = arrayListOf<TodoItem>()
        val deleted = arrayListOf<TodoItem>()
        val newItemsDB = arrayListOf<TodoItem>()
        val newItemsServ = arrayListOf<TodoItem>()
        val added = mutableSetOf<String>()
        for (i in serverData) {
            try {
                val item = database.toDoItemDao().getItemByIdNoFlow(i.id)
                if (i.lastEditDate > item.lastEditDate) {
                    resArr.add(i)
                    added.add(i.id)
                } else if (i.lastEditDate < item.lastEditDate) {
                    if (item.isDeleted) {
                        deleted.add(item)
                    } else {
                        resArr.add(item)
                    }
                    added.add(item.id)
                } else if (item.isDeleted) {
                    deleted.add(item)
                    added.add(item.id)
                } else {
                    added.add(item.id)
                }
            } catch (e: Exception) {
                newItemsServ.add(i)
                added.add(i.id)
            }
        }
        for (i in database.toDoItemDao().getAllNoFlow()) {
            if (i.id !in added && !i.isDeleted) {
                newItemsDB.add(i)
            }
        }
        return Pair(Pair(resArr, newItemsServ), Pair(deleted, newItemsDB))
    }

    suspend fun getTaskById(id: String): Flow<TodoItem?> {
        return database.toDoItemDao().getItemById(id)
    }

    suspend fun addTask(todoItem: TodoItem, mute: Boolean = false) {
        database.toDoItemDao().insertItem(todoItem)
        if (isOnlineMode && isOnline()) {
            try {
                val response = service.addTask(SingleItemResponse(todoItem))
                handleResponse(response, mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }

        }
    }

    suspend fun updateTask(todoItem: TodoItem, mute: Boolean = false) {
        database.toDoItemDao().updateItem(todoItem)
        if (isOnlineMode && isOnline()) {
            try {
                val response = service.updateTask(todoItem.id, SingleItemResponse(todoItem))
                val x = sp.getInt(lkr_key,0)
                val c= x*2
                handleResponse(response, mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        }
    }

    suspend fun deleteTask(todoItem: TodoItem, mute: Boolean = false) {
        if (isOnlineMode && isOnline()) {
            todoItem.isDeleted = true
            todoItem.lastEditDate = converter.dateToTimestamp(LocalDate.now())
            database.toDoItemDao().updateItem(todoItem)
        } else {
            database.toDoItemDao().deleteItem(todoItem)
        }
        if (isOnlineMode && isOnline()) {
            try {
                val response = service.deleteTask(todoItem.id)
                handleResponse(response, mute)
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        }
    }

    suspend fun sendTasks(list: List<TodoItem>) {
        if (isOnlineMode && isOnline()) {
            try {
                val response = service.updateTasksList(ArrayResponse(list))
                _codeChannel.send(response.code())
                when (response.code()) {
                    400 -> syncWithServer()
                    200 -> updateRevision(response.body()!!.revision!!)
                }
            } catch (e: Exception) {
                _codeChannel.send(600)
            }
        }
    }

    fun updateRevision(code: Int) {
        sp.edit().putInt(lkr_key, code).apply()
    }

    fun destroy() {
        scope.cancel()
        checkConnectionJob?.cancel()
    }
}