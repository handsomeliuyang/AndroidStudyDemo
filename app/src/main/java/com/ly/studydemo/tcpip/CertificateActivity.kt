package com.ly.studydemo.tcpip

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ly.studydemo.R
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import java.io.*
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class CertificateActivity : AppCompatActivity(), View.OnClickListener {

    private var mTextView: TextView? = null
    private var mCAAuthority: Authority? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate)

        findViewById<Button>(R.id.generate_ca_certi).setOnClickListener(this)
        findViewById<Button>(R.id.generate_keypair).setOnClickListener(this)

        mTextView = findViewById(R.id.content_view)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.generate_ca_certi -> generateCACenti()
            R.id.generate_keypair -> generateKeypair()
        }
    }

    private fun generateKeypair() {
        val keyStoreDir = this.getExternalFilesDir("keystore")
        if(keyStoreDir?.exists() != true) {
            keyStoreDir?.mkdirs()
        }

        if(mCAAuthority == null) {

            val caAuthority = Authority(
                keyStoreDir?:return,
                "ca",
                "123456",
                "LIU CA",
                "CA",
                "LIUSign",
                "Beijing",
                "Beijing",
                "CN",
                emptyList()
            )

            mCAAuthority = caAuthority;

        }

        val authority = Authority(
            keyStoreDir?:return,
            "wuba1",
            "123456",
            "58tongcheng",
            "58",
            "Beijing 58 Information",
            "Beijing",
            "Beijing",
            "CN",
            arrayListOf(
                "*.58.com",
                "xueche.com",
                "*.5858.com",
                "jxedt.com",
                "zhuanzhuan.com",
                "58xueche.com",
                "apiwmda.58.com.cn",
                "zhuanspirit.com",
                "zhuancorp.com",
                "report.xcydt.com",
                "*.xueche.com",
                "*.m.58.com",
                "*.vip.58.com",
                "*.5858.com",
                "*.jxedt.com",
                "*.zhuanzhuan.com",
                "*.58xueche.com",
                "*.zhuanspirit.com",
                "*.zhuancorp.com",
                "m.zhuanzhuan.58.com",
                "auth.finance.58.com",
                "58.com"
            )
        )

        val serverKeyStore = CertificateHelper.createServerKeypair(authority, mCAAuthority?:return)
        saveKeystore(authority, serverKeyStore)

        // 从Keystore中导出证书，格式转换为.pem，单独保存
        val cert = serverKeyStore.getCertificate(authority.alias)
        saveCert(authority, cert)

        mTextView?.text = "${mTextView?.text}\n" +
                "Server证书生成成功！！！！！\n" +
                "Keystore的路径：${authority.aliasFile().absolutePath}\n" +
                "证书的路径：${authority.pemFile().absolutePath}"
    }

    private fun generateCACenti() {
        mTextView?.text = "生成失败，请查看日志"

        val keyStoreDir = this.getExternalFilesDir("keystore")
        if(keyStoreDir?.exists() != true) {
            keyStoreDir?.mkdirs()
        }

        val caAuthority = Authority(
            keyStoreDir?:return,
            "ca",
            "123456",
            "LIU CA",
            "CA",
            "LIUSign",
            "Beijing",
            "Beijing",
            "CN",
            emptyList()
        )

        // 创建根证书，即CA证书
        val keystore = CertificateHelper.createRootCertificate(caAuthority)
        saveKeystore(caAuthority, keystore)

        // 从Keystore中导出证书，格式转换为.pem，单独保存
        val cert = keystore.getCertificate(caAuthority.alias)
        saveCert(caAuthority, cert)

        mCAAuthority = caAuthority

        mTextView?.text = "CA根证书生成成功！！！！！\n" +
                "根证书的路径：${caAuthority.aliasFile().absolutePath}\n" +
                "证书的路径：${caAuthority.pemFile().absolutePath}"
    }

    private fun saveCert(authority: Authority, cert: Certificate) {
        var sw: Writer? = null
        var pw: JcaPEMWriter? = null
        try {
            val pemFile = authority.pemFile()
            if(pemFile.exists()) {
                pemFile.delete()
            }
            sw = FileWriter(pemFile)
            pw = JcaPEMWriter(sw)

            pw.writeObject(cert)
            pw.flush()
        } finally {
            pw?.close()
            sw?.close()
        }
    }

    private fun saveKeystore(authority: Authority, keystore: KeyStore) {
        // 保存密钥仓库(keystore)文件
        var os: OutputStream? = null
        try {
            val file =  authority.aliasFile()
            if(file.exists()) {
                file.delete()
            }
            os = FileOutputStream(file)
            keystore.store(os, authority.password.toCharArray())
        } finally {
            os?.close()
        }
    }


}