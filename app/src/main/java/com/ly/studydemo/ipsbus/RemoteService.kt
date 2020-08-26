package com.ly.studydemo.ipsbus

import android.util.Log
import com.ly.studydemo.binder.MyData
import com.ly.studydemo.binder.TAG
import java.util.concurrent.atomic.AtomicReference

class RemoteService : IRemoteService {

    companion object {
        private val gService: AtomicReference<RemoteService> = AtomicReference()

        fun systemReady() {
            val instance = RemoteService()
            gService.set(instance)
        }
        fun get(): RemoteService {
            return gService.get()
        }
    }

    private val mMyData: MyData

    init {
        mMyData = MyData()
        mMyData.data1 = 10
        mMyData.data2 = 20
    }

    override fun getPid(): Int? {
        Log.i(TAG, "[RemoteService] getPid()=${android.os.Process.myPid()}")
        return android.os.Process.myPid()
    }

    override fun getMyData(): MyData? {
        return mMyData
    }

}