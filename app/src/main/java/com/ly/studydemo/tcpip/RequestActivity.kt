package com.ly.studydemo.tcpip

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ly.studydemo.R
import com.ly.studydemo.utils.DemoLog
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Url
import java.net.InetAddress
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


class RequestActivity : AppCompatActivity(), View.OnClickListener {

    private var apiService: ApiService? = null
    private var contentText: TextView? = null
    private var executor: ThreadPoolExecutor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        executor = ThreadPoolExecutor(
            3,
            5,
            10L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(1024),
            ThreadFactory { r ->
                val thread = Thread(r)
                thread.name = "HttpsRequestDemo"
                thread
            }
        )

        val trustManager = MergeTrustManager(null)//createTrustManager()
        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(createSslSocketFactory(trustManager), trustManager)
            .dns(object: Dns {
                override fun lookup(hostname: String): MutableList<InetAddress> {
                    if (hostname.endsWith("wubacom.com")
                        or hostname.endsWith("wuba.com")) {
                        return Arrays.asList(InetAddress.getByName("10.252.214.142"))
                    }
                    return Dns.SYSTEM.lookup(hostname)
                }
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.58.com")
//            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        findViewById<Button>(R.id.startReqBtn).setOnClickListener(this)
        findViewById<Button>(R.id.startReqBatch).setOnClickListener(this)
        contentText = findViewById<TextView>(R.id.content_view)
    }

    private fun createTrustManager(): X509TrustManager {
        val keyStore = loadKeyStore(
            this,
            "truststore.bks",
//            "truststore_2.bks",
            "123456"
        )

        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(keyStore)
        for(tm in tmf.trustManagers){
            if (tm is X509TrustManager) {
                return tm as X509TrustManager
            }
        }
        throw IllegalStateException("Missed X509TrustManager in ${tmf.trustManagers}")
//        return MergeTrustManager(keyStore)
    }

    private fun loadKeyStore(context: Context, assetsName: String, keystorePassword: String): KeyStore? {
        val am: AssetManager = context.assets
        val trustStore = KeyStore.getInstance("BKS")
        am.open(assetsName).use {
            trustStore.load(it, keystorePassword.toCharArray())
            return trustStore
        }
    }

    private fun createSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        return sslContext.socketFactory
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.startReqBtn -> startReqOnce()
            R.id.startReqBatch -> startReqBatch()
        }
    }

    private fun startReqBatch() {
        contentText?.text = "批量做Https请求：\n"

        val urls = arrayOf(
//            "https://app.58.com/api/home/operate/rightop" // 1
            "https://pic7.58cdn.com.cn/m1/bigimage/n_v2a6078c18b0494ee9864f0864cac9c4d9.jpg"
//            "https://app.58.com/api/home/app/newindexinfoV2/?os=android&data=3E44B4D856CBC69DBBA2D00DBDBB840ADE30376E1929C36E2AE955CDE796380C9E180229D97F7E004F49664FD3636F0F76B85F213B73DAEB37B9941311BBBE2E&city=bj&cversion=10.10.0&key=455A91B20E6D42C66CE82049E844C607DDD92BE483948780018A53F818B780F3C2E113CCF05E83E52F2E128C2F9FDCA217F7771475A94C3C33115DEDE23A1D66C71EC08A2036BB7B0953525E9DA19112E215483EBC6E62ECA7FF2DD27859DDE0DE182BDCA9308050356B841463DEA181820F9A37E43925CDA2A1F9B6CC8FF04CEFD181EA85819F64CBC54B863F44D2E8BDCD5FC1084BCD2BF0B3AF67E755B785BA47C4308D638E2FBC598A2D24B0B28E7634088BAE95A7F2E0160034D55ACAA3318EC2ED3D4D70D2E8B15130E6096E8DFB5C808A355BDC3D87C31DF2BA697D523CE5271DA51F2EFE03E5B96FDC852CC766F3BF64D5DDD24AC3F9F1E289BD5AC4",
//            "https://app.58.com/api/home/operate/maindata?city=bj", // 3
//            "https://app.58.com/api/home/discovery/reddot/tab", // 4
//            "https://pic5.58cdn.com.cn/m1/bigimage/n_v25fe2e2d3d1df4c958f3f46281b9db8d3.jpg", // 5
//            "https://app.58.com/api/base/hotupdate/getresource?commver=9&ver=1742&bundleid=166&appversion=10.10.0&appshort=5857",
//            "https://pic3.58cdn.com.cn/m1/bigimage/n_v21939c04384204fa094c6098e41b6dbf1.jpg", // 8
//            "https://pic3.58cdn.com.cn/nowater/tribenowatermark/n_v2a81b841f50e44a0897998bcb433bb83e.png",
//            "https://app.58.com/api/home/inform/carousel", // 10
//            "https://pic7.58cdn.com.cn/m1/bigimage/n_v237dc685211444861b94d03f02e43a7e9.jpg",
//            "https://pic5.58cdn.com.cn/m1/bigimage/n_v22247aa05e4d34697a8845c0ca101b471.jpg",
//            "https://pic7.58cdn.com.cn/m1/bigimage/n_v2bfcb3fddad354fd388f9853d9a9823b7.jpg", // 13
//            "https://pic5.58cdn.com.cn/m1/bigimage/n_v246b69538ae19489e95786437815916df.jpg" // 14
//            "https://app.58.com/api/home/feed/recommend?pageNum=1",
//            "https://app.58.com/api/search/searchwords?history=",
//            "https://pic1.58cdn.com.cn/m1/bigimage/n_v2b30d4cafbd574108b2d2bd5826e78906.jpg",
//            "https://pic1.58cdn.com.cn/m1/bigimage/n_v2629e607eb656420ba7a9e9cfab6ac0a0.jpg",
//            "https://pic1.58cdn.com.cn/m1/bigimage/n_v28b5ddfb25b0e4bd4a4616a49691befcb.jpg",
//            "https://pic4.58cdn.com.cn/m1/bigimage/n_v2b05d4df4bed54a3c8b69b579160c4e03.jpg",
//            "https://pic4.58cdn.com.cn/m1/bigimage/n_v27f3a3502a510471c8413c7c6e4d6a292.jpg",
//            "https://pic6.58cdn.com.cn/nowater/tribenowatermark/n_v220d2bcfebd09423783b012c9537300b8.png",
//            "https://img.58cdn.com.cn/escstatic/fecar/pmuse/chexi_logo_320/409331.png",
//            "https://pic6.58cdn.com.cn/m1/bigimage/n_v289776abfff024b2592ba19b9f4ba2da6.jpg",
//            "https://pic6.58cdn.com.cn/m1/bigimage/n_v2f723b566bf7b4bcd8f87ad11abc6f91a.jpg",
//            "https://pic6.58cdn.com.cn/m1/bigimage/n_v2ae78366062ec4bb29766799a65fa1d35.jpg",
//            "https://pic2.58cdn.com.cn/m1/bigimage/n_v2316f0264915b4f1c8b1affb84870ec6c.jpg",
//            "https://pic2.58cdn.com.cn/m1/bigimage/n_v2855a287d6ecc4d0da08cada0ec3c0205.jpg",
//            "https://pic2.58cdn.com.cn/anjuke_58/5b7a4b8f886cac3aa152a85eceead1dc?w=342&h=258",
//            "https://pic1.58cdn.com.cn/anjuke_58/7ec21a963453b19ff0fdc1f887685226?w=342&h=258"
        )

        val headers = mapOf(
            Pair("coordinatetype", "0"),
            Pair("official", "true"),
            Pair("accept-encoding", "gzip,deflate"),
            Pair("ua", "Mi9 Pro 5G"),
            Pair("uuid", "1aa37514-c841-4b68-953e-69e18a2d11b5"),
            Pair("dirname", "bj"),
            Pair("productorid", "1"),
            Pair("nettype", "wifi"),
            Pair("currentcid", "1"),
            Pair("ltext", "%E5%A4%A7%E5%B1%B1%E5%AD%90"),
            Pair("push", "0"),
            Pair("version", "10.10.0"),
            Pair("rnsoerror", "0"),
            Pair("firstopentime", "1609240287582"),
            Pair("deny", "2.75"),
            Pair("lat", "39.993398"),
            Pair("apn", "WIFI"),
            Pair("brand", "Xiaomi"),
            Pair("xxaq_rid", "9db6ebcc12"),
            Pair("dlat", "39.9992158888"),
            Pair("osv", "10"),
            Pair("marketchannelid", "585858"),
            Pair("bangbangid", "1080866010669110857"),
            Pair("wbuversion", "10.10.0"),
            Pair("androidid", "a5d8f813dea4ae73"),
            Pair("cid", "1"),
            Pair("townlocalid", "110105026000"),
            Pair("xxzlsid", "5NQg1T-MCt-M9j-Rwf-1Sa7Usymh"),
            Pair("dlon", "116.4952218888"),
            Pair("scale", "1"),
            Pair("adnop", "46002"),
            Pair("lon", "116.511517"),
            Pair("nop", "4257128004483"),
            Pair("platform", "android"),
            Pair("manufacturer", "Xiaomi"),
            Pair("id58", "102991378364691"),
            Pair("sh", "2221"),
            Pair("osarch", "arm64-v8a"),
            Pair("xxzlcid", "e206b6d956cd461fac387841618eb6e4"),
            Pair("webua", "Mozilla/5.0 (Linux; Android 10; Mi9 Pro 5G Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.101 Mobile Safari/537.36"),
            Pair("bundle", "com.wuba"),
            Pair("uniqueid", "841bd63b3b026695597b5895078bfbf2"),
            Pair("totalsize", "224.7"),
            Pair("owner", "baidu"),
            Pair("product", "app58"),
            Pair("sw", "1080"),
            Pair("os", "android"),
            Pair("deviceid", "a5d8f813dea4ae73"),
            Pair("r", "2221_1080"),
            Pair("xxzl_deviceid", "k8nApe8uqZmU8uzfyx2tlYb3cMSoJ0Ujx698vcS2h5LapB8OzFTHU9C+mMszy9aS"),
            Pair("xxzl_smartid", "3cc9ffd763804fb481c8852aa7fb4870"),
            Pair("58ua", "58app"),
            Pair("xxzl_cid", "e206b6d956cd461fac387841618eb6e4"),
            Pair("maptype", "2"),
            Pair("imei", "a5d8f813dea4ae73"),
            Pair("location", "1,1142,7551"),
            Pair("tp", "Mi9 Pro 5G"),
            Pair("channelid", "1015"),
            Pair("locationstate", "1"),
            Pair("user-agent", "okhttp/3.11.0")
        )

        urls.forEachIndexed { index, url ->
            // 通过线程执行
            executor?.execute {
                val call = apiService?.reqBatch(url, headers)
                call?.enqueue(object : Callback<String> {
                    @SuppressLint("SetTextI18n")
                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        this@RequestActivity.runOnUiThread {
                            contentText?.text = "${contentText?.text}${index+1}. $url 请求结果：请求失败，" +
                                    "Failure=${t?.toString()} \n\n"
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<String>?, response: Response<String>?) {
                        this@RequestActivity.run {
                            contentText?.text = "${contentText?.text}${index+1}. $url 请求结果：请求成功，" +
                                    "code=${response?.code()} \n\n"
                        }
                    }
                })
            }
        }
    }

    private fun startReqOnce() {
        val httpUrl = "http://news.58.com/api/windex/getconsultdetail"
//        val httpsUrl = "https://wubacom.com:4450"

        val call = apiService?.reqOnce(httpUrl) ?: return

        contentText?.text = "http 请求中..."

        call.enqueue(object : Callback<String> {
                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    contentText?.text = "http 请求失败：\n" +
                            "Failure=${t?.toString()}"
                }
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    contentText?.text = "http 请求成功：\n" +
                            "message=${response?.message()}\n" +
                            "body=${response?.body()}"
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        executor?.shutdownNow()
    }
}

interface ApiService {
    @GET
    fun reqOnce(@Url url: String): Call<String>

    @GET
    fun reqBatch(@Url url: String, @HeaderMap headers:Map<String, String>): Call<String>
}