package io.chipotie.grindemo.util

import android.content.Context
import android.net.ConnectivityManager



class ConnectivityUtils{

    companion object {
        fun isInternetConnectionAvailable(context: Context) : Boolean{
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }
    }
}