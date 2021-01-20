package com.ly.studydemo.tcpip

import java.security.KeyStore
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class MergeTrustManager(keyStore: KeyStore?) : X509TrustManager {

    // 自定义的认证
//    private val addedTm: X509TrustManager
    // 系统默认认证
    private val javaTm: X509TrustManager

    init {
        javaTm = defaultTrustManager(null)
//        addedTm = defaultTrustManager(keyStore)
    }

    private fun defaultTrustManager(keyStore: KeyStore?): X509TrustManager {
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(keyStore)

        for(tm in tmf.trustManagers){
            if (tm is X509TrustManager) {
                return tm as X509TrustManager
            }
        }
        throw IllegalStateException("Missed X509TrustManager in ${tmf.trustManagers}")
    }

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//        addedTm.checkServerTrusted(chain, authType)
        javaTm.checkServerTrusted(chain, authType)

        javaTm.checkClientTrusted(chain, authType)
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        val intermediate = chain?.get(chain.size - 1) ?: return

        for(cert in javaTm.acceptedIssuers) {
            try{
                intermediate.verify(cert.publicKey);

                // root cert
                // 校验Root证书的Sha1值
                try {
                    val sha1Digest = MessageDigest.getInstance("SHA1")
                    val bytes = sha1Digest.digest(cert.encoded)

                    val builder = StringBuffer()
                    for (value in bytes) {
                        val appendString = Integer.toHexString(value.toInt() and 0xFF)
                        if (appendString.length == 1) builder.append("0")
                        builder.append(appendString)
                    }
                    val sha1Value = builder.toString()

                    if("593aaadffde2c98c209d73a10adcd920a185bff6" != sha1Value) {
                        throw CertificateException("非法证书")
                    }
                } catch (e: NoSuchAlgorithmException) {
                    //
                }
                return
            } catch (e: Exception){
                //
            }
        }
        throw CertificateException("非法证书")
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        val issuers: MutableList<X509Certificate> = ArrayList()
//        issuers.addAll(listOf(*addedTm.acceptedIssuers))
        issuers.addAll(listOf(*javaTm.acceptedIssuers))
        return issuers.toTypedArray()
    }

}