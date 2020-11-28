package com.ly.studydemo.tcpip

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ly.studydemo.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


class RequestActivity : AppCompatActivity(), View.OnClickListener {

    private var apiService: ApiService? = null
    private var contentText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.58.com")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        findViewById<Button>(R.id.start_http_btn).setOnClickListener(this)
        findViewById<Button>(R.id.start_https_btn).setOnClickListener(this)
        contentText = findViewById<TextView>(R.id.content_view)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.start_http_btn -> startHttpRequest()
            R.id.start_https_btn -> startHttpsRequest()
        }
    }

    private fun startHttpsRequest() {
        val call = apiService?.https() ?: return

        contentText?.text = "https 请求中..."

        call.enqueue(object : Callback<String> {
            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                contentText?.text = "https 请求失败：\n" +
                        "Failure=${t?.toString()}"
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                contentText?.text = "https请求成功：\n" +
                        "message=${response?.message()}\n" +
                        "body=${response?.body()}"
            }
        })
    }

    private fun startHttpRequest() {
        val call = apiService?.http() ?: return

        contentText?.text = "http 请求中..."

        call.enqueue(object : Callback<String> {
                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    contentText?.text = "http 请求失败：\n" +
                            "Failure=${t?.toString()}"
                }
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    contentText?.text = "http 请求成功：\n" +
                            "message=${response?.message()}\n" +
                            "body=${response?.body()}"
                }
            })
    }

//    private fun startHttpOnVolley(){
//        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
//        val url = "http://news.58.com/api/windex/getconsultdetail"
//
//        // Request a string response from the provided URL.
//        val stringRequest = StringRequest(
//            Request.Method.GET, url,
//            com.android.volley.Response.Listener<String> { response ->
//                contentText?.text = "https请求成功：\n ${response.toString()}"
//            },
//            com.android.volley.Response.ErrorListener {t ->
//                contentText?.text = "https 请求失败：\n" +
//                        "Failure=${t?.toString()}"
//            })
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }

}

interface ApiService {
    @GET("http://news.58.com/api/windex/getconsultdetail")
    fun http(): Call<String>

    @GET("https://bj.58.com/")
    fun https(): Call<String>
}