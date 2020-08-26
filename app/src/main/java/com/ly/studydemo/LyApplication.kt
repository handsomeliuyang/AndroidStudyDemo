package com.ly.studydemo

import android.app.Application
import android.content.Context
import android.os.Debug

class LyApplication : Application() {

    companion object {
        private var application: Application? = null
        fun getContext(): Context? {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()

//        Debug.waitForDebugger()

        LyApplication.application = this



    }
}