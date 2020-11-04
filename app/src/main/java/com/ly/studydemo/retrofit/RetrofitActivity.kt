package com.ly.studydemo.retrofit

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ly.studydemo.R
import okhttp3.*
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitActivity : Activity(), View.OnClickListener {

//    private var mTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)

//        mTextView = findViewById<TextView>(R.id.textview)

        findViewById<Button>(R.id.request_btn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.request_btn -> requestData()
        }
    }

    private var retrofit: Retrofit? = null

    private fun requestData() {
        if (retrofit == null) {
            val httpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(object: Interceptor {
                    override fun intercept(chain: Interceptor.Chain?): Response? {
                        val request = chain?.request()
                        val value = request?.header("liuyang")
                        Log.e("liuyang", value?:"")
                        return chain?.proceed(request)
                    }
                })
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("http://baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .callFactory(object : Call.Factory {
                    override fun newCall(request: Request): Call {
                        val newRequest = request.newBuilder()
                            .addHeader("liuyang", "town")
                            .build()
                        return httpClient.newCall(newRequest)
                    }
                })
                .build()
        }

        val commitApiService = retrofit?.create(CommitApiService::class.java)?:return

        val call = commitApiService.fetchData()

        call.enqueue(object: Callback<String> {
            override fun onFailure(call: retrofit2.Call<String>?, t: Throwable?) {
                Log.e("liuyang", "请求失败", t)
            }

            override fun onResponse(
                call: retrofit2.Call<String>?,
                response: retrofit2.Response<String>?
            ) {
                Log.e("liuyang", "请求成功")
            }

        })
    }

}