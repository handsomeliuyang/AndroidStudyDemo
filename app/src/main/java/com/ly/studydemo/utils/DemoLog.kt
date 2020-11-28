package com.ly.studydemo.utils

import android.util.Log

object DemoLog {
    var isDebug = true

    fun makeDebugLog(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    fun d(tag: String?, message: String?) {
        if (isDebug) {
            Log.d(tag, message?:"")
        }
    }

    fun v(tag: String?, message: String?) {
        Log.v(tag, message?:"")
    }

    fun i(tag: String?, message: String?) {
        Log.i(tag, message?:"")
    }

    fun w(tag: String?, message: String?) {
        Log.w(tag, message?:"")
    }

    fun w(tag: String?, message: String?, e: Throwable?) {
        Log.w(tag, message, e)
    }

    fun e(tag: String?, message: String?, e: Throwable) {
        Log.e(tag, message?:"", e)
    }
}