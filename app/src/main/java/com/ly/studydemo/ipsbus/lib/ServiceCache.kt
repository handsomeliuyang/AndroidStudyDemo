package com.ly.studydemo.ipsbus.lib

import android.os.IBinder

object ServiceCache {

    private val sCache = HashMap<String, IBinder>(5)

    fun addService(name: String, service: IBinder) {
        sCache.put(name, service)
    }

    fun removeService(name: String): IBinder? {
        return sCache.remove(name)
    }

    fun getService(name: String): IBinder? {
        return sCache.get(name)
    }

}