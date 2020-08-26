package com.ly.studydemo.ipsbus.lib

import android.content.Context
import android.net.Uri
import android.os.Bundle

class ProviderCall {
    companion object {
        fun call(authority: String, context: Context?, method: String?, arg: String?, bundle: Bundle?): Bundle? {
            val uri = Uri.parse("content://${authority}")
            return context?.contentResolver?.call(uri, method ?: "", arg, bundle)
        }
    }

    class Builder(val context: Context?, val authority: String) {

        private var method: String? = null
        private var arg: String? = null

        fun methodName(name: String): Builder {
            this.method = name
            return this
        }

        fun arg(arg: String): Builder {
            this.arg = arg
            return this
        }

        fun call(): Bundle? {
            return ProviderCall.call(
                this@Builder.authority,
                this@Builder.context,
                this@Builder.method,
                this@Builder.arg,
                null)
        }
    }
}