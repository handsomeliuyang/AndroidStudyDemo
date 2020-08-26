package com.ly.studydemo.ipsbus.lib

import android.os.IBinder
import android.os.Parcel
import java.lang.Exception
import java.lang.reflect.Method

class IPCMethod(val code: Int, val method: Method, val interfaceName: String) {

    init {

    }

    /**
     * 客户端调用，传输调用的方法名称和方法参数
     */
    fun callRemote(server: IBinder, args: Array<Any>?): Any? {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {

            data.writeInterfaceToken(interfaceName)
            data.writeArray(args)
            server.transact(code, data, reply, 0)
            reply.readException()

            val result = reply.readValue(this.javaClass.classLoader)

            return result
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    /**
     * 服务端调用，从传输的数据里，转换为需要调用的方法和参数
     */
    fun handleTransact(server: Any, data: Parcel, reply: Parcel?){
        data.enforceInterface(interfaceName)
        val parameters = data.readArray(this.javaClass.classLoader)

        try {
            val res: Any?
            if (parameters == null) {
                res = method.invoke(server)
            } else {
                res = method.invoke(server, parameters)
            }
            reply?.writeNoException()
            reply?.writeValue(res)
        }catch (e: Exception) {
            reply?.writeException(e)
        }
    }

//    private fun readValue(reply: Parcel): Any? {
//        val result = reply.readValue(this.javaClass.classLoader)
//        // TODO-ly 如果返回值是一个数组，如何处理
//        return result
//    }

}