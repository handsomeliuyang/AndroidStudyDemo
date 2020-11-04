package com.ly.studydemo.ipsbus.lib

import android.os.IBinder
import android.os.RemoteException
import androidx.core.app.BundleCompat
import com.ly.studydemo.StudyDemosApplication
import com.ly.studydemo.ipsbus.IServiceFetcher
import java.lang.reflect.Proxy

object IPCBus {

    /**
     * 此方法只会在服务进程访问
     */
    fun register(interfaceClass: Class<*>, server: Any) {
        val serverInterface = ServerInterface(interfaceClass)
        val binder = TransformBinder(serverInterface, server)
        ServiceCache.addService(
            serverInterface.getInterfaceName(),
            binder
        )
    }


    /**
     * 此方法只会在客户端进程访问
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(interfaceClass: Class<*>): T? {
        val serverInterface = ServerInterface(interfaceClass)
        // 通过AIDL:IServiceFetcher获取服务器注册的Binder对象
        val binder = getService(interfaceClass.name) ?: return null

        return Proxy.newProxyInstance(
            interfaceClass.classLoader,
            arrayOf(interfaceClass),
            IPCInvocationBridge(serverInterface, binder)
        ) as T
    }

    private fun getService(name: String): IBinder? {
        val fetcher = getServiceFetcher()
        if(fetcher != null) {
            try {
                return fetcher.getService(name)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private var sFetcher: IServiceFetcher? = null
    private fun getServiceFetcher(): IServiceFetcher?{

        val isBinderAlive = sFetcher?.asBinder()?.isBinderAlive ?: false
        if(!isBinderAlive) {
            synchronized(IPCBus::class.java) {

                val response = ProviderCall.Builder(StudyDemosApplication.getContext(), "com.ly.studydemo.virtual.service.BinderProvider")
                    .methodName("@")
                    .call()

                if (response != null) {
                    val binder = BundleCompat.getBinder(response, "_VA_|_binder_")

                    // 监听Binder是否Died
                    // linkBinderDied(binder)
                    sFetcher = IServiceFetcher.Stub.asInterface(binder)
                }

            }
        }
        return sFetcher
    }

}