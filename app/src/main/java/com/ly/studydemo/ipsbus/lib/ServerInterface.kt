package com.ly.studydemo.ipsbus.lib

import android.os.Binder
import android.util.SparseArray
import java.lang.reflect.Method

class ServerInterface(val interfaceClass: Class<*>) {

    private val codeToInterfaceMethod: SparseArray<IPCMethod>
    private val methodToIPCMethodMap: Map<Method, IPCMethod>

    init {
        val methods = interfaceClass.methods
        codeToInterfaceMethod = SparseArray(methods.size)
        methodToIPCMethodMap = HashMap(methods.size)

        for((index, method) in methods.withIndex()){
            val code = Binder.FIRST_CALL_TRANSACTION + index
            val ipcMethod = IPCMethod(code, method, interfaceClass.name)
            codeToInterfaceMethod.put(code, ipcMethod)
            methodToIPCMethodMap.put(method, ipcMethod)
        }
    }

    fun getInterfaceName(): String {
        return interfaceClass.name
    }

    fun getIPCMethod(code: Int): IPCMethod? {
        return codeToInterfaceMethod.get(code)
    }

    fun getIPCMethod(method: Method?): IPCMethod? {
        return methodToIPCMethodMap.get(method)
    }

}