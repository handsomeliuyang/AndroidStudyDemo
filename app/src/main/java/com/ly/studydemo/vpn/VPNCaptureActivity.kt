package com.ly.studydemo.vpn

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ly.studydemo.R

class VPNCaptureActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sslnio)

        findViewById<Button>(R.id.start_server_btn).setOnClickListener(this)
        findViewById<Button>(R.id.start_client_btn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.start_server_btn -> startServer()
            R.id.start_client_btn -> startClient()
        }
    }

    private fun startClient() {
        TODO("Not yet implemented")
    }

    private fun startServer() {

    }
}