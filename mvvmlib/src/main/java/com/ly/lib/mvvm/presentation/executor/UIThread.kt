package com.ly.lib.mvvm.presentation.executor

import com.ly.lib.mvvm.domain.executor.PostExecutionThread
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

class UIThread : PostExecutionThread {

    companion object {
        val INSTANCE: UIThread by lazy {
            UIThread()
        }
    }

    override fun getScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

}