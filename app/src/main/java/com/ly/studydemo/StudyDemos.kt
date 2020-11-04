package com.ly.studydemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ly.studydemo.binder.ClientActivity
import com.ly.studydemo.ipsbus.IPCBusActivity
import com.ly.studydemo.retrofit.RetrofitActivity

class StudyDemos : Activity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.aidl_btn).setOnClickListener(this@StudyDemos)
        findViewById<Button>(R.id.file_btn).setOnClickListener(this@StudyDemos)
        findViewById<Button>(R.id.ipc_btn).setOnClickListener(this@StudyDemos)
        findViewById<Button>(R.id.retrofit_btn).setOnClickListener(this@StudyDemos)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.aidl_btn -> startActivity(Intent(this@StudyDemos, ClientActivity::class.java))
            R.id.file_btn -> startActivity(Intent(this@StudyDemos, FileActivity::class.java))
            R.id.ipc_btn -> startActivity(Intent(this@StudyDemos, IPCBusActivity::class.java))
        }
    }
}
