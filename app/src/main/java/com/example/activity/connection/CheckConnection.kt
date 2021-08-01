package com.example.activity.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class CheckConnection {

    //this function is for checking the connectivity of the application ,that ,is it connected to the internet or not.
    fun checkConnectivity(context: Context):Boolean{

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        if (activeNetwork?.isConnected != null){
            return activeNetwork.isConnected
        }else{
            return false
        }
    }
}