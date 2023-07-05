package com.advancedsolutionsdevelopers.todoapp.data.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.token_key
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//колбек, который должен был реагировать на изменение сети, но пока ведет себя неадекватно
class NetCallback : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        TodoItemsRepository.isOnline.set(isOnline())
        if (TodoItemsRepository.sp.contains(token_key)) {
            CoroutineScope(Dispatchers.IO).launch {
                TodoItemsRepository.syncWithServer()
            }
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        TodoItemsRepository.isOnline.set(isOnline())
    }

    private fun isOnline(): Boolean {
        val capabilities =
            TodoItemsRepository.connectivityManager.getNetworkCapabilities(TodoItemsRepository.connectivityManager.activeNetwork)
        if (capabilities != null) {
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
        return false
    }
}