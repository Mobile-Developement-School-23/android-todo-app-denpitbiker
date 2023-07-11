package com.advancedsolutionsdevelopers.todoapp.data.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TOKEN_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//коллбек для контроля состояния сети
class NetCallback(private val repository: TodoItemsRepository) :
    ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        repository.isOnline.set(isOnline())
        if (repository.sp.contains(TOKEN_KEY)) {
            CoroutineScope(Dispatchers.IO).launch {
                repository.syncWithServer()
            }
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        repository.isOnline.set(isOnline())
    }

    private fun isOnline(): Boolean {
        val capabilities =
            repository.connectManager
                .getNetworkCapabilities(repository.connectManager.activeNetwork)
        if (capabilities != null) {
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
        return false
    }
}