package com.ly.studydemo.ipsbus.lib

import android.os.IBinder
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class IPCInvocationBridge(val serverInterface: ServerInterface, val binder: IBinder) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
        val ipcMethod = serverInterface.getIPCMethod(method)
            ?: throw IllegalStateException("Can not found the ipc method : " + method?.declaringClass?.name + "@" + method?.name)

        return ipcMethod.callRemote(binder, args)
    }

}