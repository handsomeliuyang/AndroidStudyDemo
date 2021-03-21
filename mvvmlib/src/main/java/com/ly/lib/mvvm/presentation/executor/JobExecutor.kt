package com.ly.lib.mvvm.presentation.executor

import com.ly.lib.mvvm.domain.executor.ThreadExecutor
import java.util.concurrent.*

class JobExecutor : ThreadExecutor {

    companion object {
        val INSTANCE: JobExecutor by lazy {
            JobExecutor()
        }
    }

    private val threadPoolExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(
            3,
            5,
            10,
            TimeUnit.SECONDS,
            LinkedBlockingQueue<Runnable>(),
            JobThreadFactory()
        )
    }

    override fun execute(command: Runnable?) {
        threadPoolExecutor.execute(command)
    }

    class JobThreadFactory : ThreadFactory {
        private var counter: Int = 0
        override fun newThread(r: Runnable): Thread {
            return Thread(
                r,
            "studydemo_${counter++}"
            )
        }

    }

}