package com.ly.studydemo.nio

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ly.studydemo.R
import com.ly.studydemo.nio.ssl.NioSslClient
import com.ly.studydemo.nio.ssl.NioSslServer


class SSLNioActivity : AppCompatActivity(), View.OnClickListener {

    private var mTextView: TextView? = null
    private var mNioSslServer: NioSslServer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sslnio)

        mTextView = findViewById<TextView>(R.id.content_view)

        findViewById<Button>(R.id.start_server_btn).setOnClickListener(this)
        findViewById<Button>(R.id.start_client_btn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.start_client_btn -> startNioSSL()
            R.id.start_server_btn -> startNioSslServer()
        }
    }

    private fun startNioSslServer() {
        Thread(Runnable {
            try {
                mNioSslServer = NioSslServer("TLS", "localhost", 9222)
                mNioSslServer?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    private fun startNioSSL() {
        Thread(Runnable {

            val host = "bj.58.com"
            val path = "/"

            val client = NioSslClient("TLS", host, 443)
            client.connect()

            val request = "GET ${path} HTTP/1.1\r\n" + // 请求行
                "Accept: */*\r\n" + // 请求头
                "Host: ${host}\r\n" + // 请求头
                "Connection: Close\r\n" + // 请求头
                "\r\n" // 空行

            client.write(request)
            client.read()
            client.shutdown()

//            val client = NioSslClient("TLS", "localhost", 9222)
//            client.connect()
//            client.write("Hello! I am a client!")
//            client.read()
//            client.shutdown()

//            mNioSslServer?.stop()
        }).start()
    }

    private fun startHttp(){
        val httpsClient = NioHttpClient("news.58.com", 80, "/api/windex/getconsultdetail")
        httpsClient.request()
    }
}