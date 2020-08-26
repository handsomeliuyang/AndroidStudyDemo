package com.ly.studydemo.ipsbus

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ly.studydemo.R
import com.ly.studydemo.ipsbus.lib.IPCBus

class IPCBusActivity : Activity(), View.OnClickListener {

    private var mTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipc)

        mTextView = findViewById<TextView>(R.id.textview)

        findViewById<Button>(R.id.dynamic_btn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.dynamic_btn -> getRemoteData()
        }
    }

    private fun getRemoteData() {
        val remoteService = IPCBus.get<IRemoteService>(IRemoteService::class.java)
        mTextView?.text = "服务进程id=${remoteService?.getPid()} \n Data数据=${remoteService?.getMyData()}"
    }

}