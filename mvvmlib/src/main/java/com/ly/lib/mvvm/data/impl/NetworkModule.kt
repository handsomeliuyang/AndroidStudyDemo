package com.ly.lib.mvvm.data.impl

import com.ly.lib.mvvm.model.Character
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT = 10L
private const val WRITE_TIMEOUT = 1L
private const val READ_TIMEOUT = 20L
private const val BASE_URL = "http://hp-api.herokuapp.com/"

object NetworkModule {
    val retrofit: Retrofit by lazy<Retrofit> {
        val okHttpClient = OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
        }.build()

        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: HarryPotterService by lazy { retrofit.create(HarryPotterService::class.java) }
}

interface HarryPotterService {
    @GET("api/characters/house/{house}")
    fun getCharacters(@Path("house") type: String): Call<List<Character>>
}