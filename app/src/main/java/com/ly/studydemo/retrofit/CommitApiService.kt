package com.ly.studydemo.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface CommitApiService {
    @GET("/")
    fun fetchData(): Call<String>
}