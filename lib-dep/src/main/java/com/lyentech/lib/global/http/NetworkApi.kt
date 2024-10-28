package com.lyentech.lib.global.http

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.lyentech.lib.bean.NetStatusBean
import com.lyentech.lib.utils.printD
import org.greenrobot.eventbus.EventBus

/**
 * @author by jason-何伟杰，2024/10/28
 * des:全局处理网络状态
 */
class NetworkApi(context: Context) {

    companion object {
        var instance: NetworkApi? = null
    }

    private var connectivityManager: ConnectivityManager
    private var networkCallback: NetworkCallback

    init {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                printD("onAvailable")
                pushEventSuc()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                printD("onLost")
                pushEventErr()
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                printD("onLosing")
            }

            override fun onUnavailable() {
                super.onUnavailable()
                printD("onUnavailable")
            }

        }
        instance = this
    }

    fun registerCallback() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun pushEventSuc() {
        EventBus.getDefault().post(NetStatusBean(NetStatusBean.SUC))
    }

    private fun pushEventErr() {
        EventBus.getDefault().post(NetStatusBean(NetStatusBean.ERR))
    }
}