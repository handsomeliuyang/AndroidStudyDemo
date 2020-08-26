package com.ly.studydemo.binder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.atomic.AtomicReference

const val TAG = "BinderSimple"

class RemoteService : Service() {

    private var mMyData: MyData? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "[RemoteService] onCreate")
        initMyData()
    }

    private fun initMyData() {
        mMyData = MyData()
        mMyData?.data1 = 10
        mMyData?.data2 = 20
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "[RemoteService] onBind")
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "[RemoteService] onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.i(TAG, "[RemoteService] onDestroy")
        super.onDestroy()
    }

    private val mBinder: IRemoteService.Stub = object:IRemoteService.Stub() {
        override fun getPid(): Int {
            Log.i(TAG, "[RemoteService] getPid()=${android.os.Process.myPid()}")
            return android.os.Process.myPid()
        }

        override fun getMyData(): MyData? {
            Log.i(TAG, "[RemoteService] getMyData()=${this@RemoteService.mMyData}")
            return this@RemoteService.mMyData
        }
    }

}