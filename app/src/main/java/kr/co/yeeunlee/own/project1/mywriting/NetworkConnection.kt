package kr.co.yeeunlee.own.project1.mywriting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import java.lang.Exception

class NetworkConnection(private val context: Context) : LiveData<Boolean>() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        updateConnection()
        when{   // when에 인자가 없으면 if-else문으로 사용, the branch conditions are simply boolean expressionㄴ
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            {
                val builder = NetworkRequest.Builder()
                connectivityManager = context.getSystemService(
                    Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                connectivityManager.registerNetworkCallback(builder.build(), connectivityManagerCallback())
            }
            else ->
            {
                context.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    fun unregister(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            connectivityManager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            try {
                connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
            }catch (e: Exception){
                Log.d("콜백해제", "예외 발생")
            }
        }else
        {
            context.unregisterReceiver(networkReceiver)
        }
    }

    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            networkCallback = object : ConnectivityManager.NetworkCallback(){
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(true)
                }
            }
            return networkCallback
        } else throw IllegalAccessError("Error")
    }

    private val networkReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }

    private fun updateConnection(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!NetworkManager.checkNetworkState(context))
                postValue(false)
            else postValue(true)
        } else{
            val activveNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            postValue(activveNetwork?.isConnected == true)
        }
    }
}