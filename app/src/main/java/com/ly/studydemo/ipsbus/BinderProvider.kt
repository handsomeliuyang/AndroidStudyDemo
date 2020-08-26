package com.ly.studydemo.ipsbus

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.BundleCompat
import com.ly.studydemo.ipsbus.lib.IPCBus
import com.ly.studydemo.ipsbus.lib.ServiceCache

class BinderProvider : ContentProvider() {

    private val mServiceFetcher: ServiceFetcher = ServiceFetcher()

    override fun onCreate(): Boolean {

        // 向IPCBus注册服务
        RemoteService.systemReady()
        IPCBus.register(IRemoteService::class.java, RemoteService.get())

        return true
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if ("@" == method) {
            val bundle = Bundle()
            BundleCompat.putBinder(bundle, "_VA_|_binder_", mServiceFetcher)
            return bundle
        }
        return null
    }


    private class ServiceFetcher : IServiceFetcher.Stub() {

        override fun getService(name: String?): IBinder? {
            if(name == null) {
                return null
            }
            return ServiceCache.getService(name)
        }

    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }


}