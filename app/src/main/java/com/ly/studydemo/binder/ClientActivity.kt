package com.ly.studydemo.binder

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import com.ly.studydemo.R
import android.content.Context
import android.util.Log
import android.widget.TextView
import java.lang.Exception


class ClientActivity : Activity(), View.OnClickListener {

    private var bindBtn: Button? = null
    private var unbindBtn: Button? = null
    private var killBtn: Button? = null
    private var textView: TextView? = null
    private var mIsBound: Boolean = false

    private var mRemoteService: IRemoteService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "[ClientActivity] onCreate")

        setContentView(R.layout.activity_client)

        bindBtn = findViewById(R.id.bind_btn)
        unbindBtn = findViewById(R.id.unbind_btn)
        killBtn = findViewById(R.id.kill_btn)

        bindBtn?.setOnClickListener(this)
        unbindBtn?.setOnClickListener(this)
        killBtn?.setOnClickListener(this)

        textView = findViewById(R.id.textview)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.bind_btn -> bindRemoteService()
            R.id.unbind_btn -> unbindRemoteService()
            R.id.kill_btn -> killRemoteService()
        }
    }

    private fun killRemoteService() {

        Log.i(TAG, "[ClientActivity] killRemoteService")

        if(mRemoteService == null) {
            return
        }

        try {
            android.os.Process.killProcess(mRemoteService?.pid ?: -1)
            textView?.text = "kill remote service success"
        } catch (e: Exception) {

        }
    }

    private fun unbindRemoteService() {
        if(!mIsBound) {
            return
        }

        Log.i(TAG, "[ClientActivity] unbindRemoteService")

        unbindService(mConnection)
        mIsBound = false
        textView?.text = "UNBIND Remote Service"
    }

    private fun bindRemoteService() {
        Log.i(TAG, "[ClientActivity] bindRemoteService")

        val intent: Intent = Intent(this@ClientActivity, RemoteService::class.java)
        intent.setAction("com.ly.studydemo.binder.IRemoteService")

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        mIsBound = true
        textView?.text = "Bind中"
    }

    private val mConnection: ServiceConnection = object: ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mRemoteService = IRemoteService.Stub.asInterface(service)

            var pidInfo = ""
            try {
                val myData: MyData? = mRemoteService?.myData
                pidInfo= "pid= ${mRemoteService?.pid}, data1=${myData?.data1}, data2=${myData?.data2}"
            } catch (e: Exception){
                pidInfo = "service connection error"
            }

            Log.i(TAG, "[ClientActivity] onServiceConnected pidInfo=${pidInfo}")

            textView?.text = pidInfo
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "[ClientActivity] onServiceDisconnected")

            mRemoteService = null
            textView?.text = "onServiceDisconnected"
        }

    }

}