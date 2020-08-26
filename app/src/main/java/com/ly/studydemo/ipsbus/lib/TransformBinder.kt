package com.ly.studydemo.ipsbus.lib

import android.os.Binder
import android.os.Parcel

class TransformBinder(val serverInterface: ServerInterface, val server: Any) : Binder() {

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if(code == Binder.INTERFACE_TRANSACTION) {
            reply?.writeString(serverInterface.getInterfaceName())
            return true
        }
        val ipcMethod = serverInterface.getIPCMethod(code)
            ?: return super.onTransact(code, data, reply, flags)
        ipcMethod.handleTransact(server, data, reply)
        return true
    }

}
