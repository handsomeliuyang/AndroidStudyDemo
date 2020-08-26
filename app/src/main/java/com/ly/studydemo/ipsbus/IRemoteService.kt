package com.ly.studydemo.ipsbus

import com.ly.studydemo.binder.MyData

interface IRemoteService {
    fun getPid(): Int?
    fun getMyData(): MyData?
}