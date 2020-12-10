package com.ly.studydemo.tcpip

import org.bouncycastle.asn1.ASN1EncodableVector
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.DERSequence
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.bc.BcX509ExtensionUtils
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.OperatorCreationException
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*

object CertificateHelper {

    /**
     * Android 不支持jks格式，p12格式支持所有的平台
     */
    const val KEY_STORE_TYPE = "PKCS12"
    const val KEY_STORE_FILE_EXTENSION = ".p12"

    private const val ROOT_KEYSIZE = 2048
    private const val FAKE_KEYSIZE = 1024
    private const val KEYGEN_ALGORITHM = "RSA"
    private const val SECURE_RANDOM_ALGORITHM = "SHA1PRNG"


    /** The milliseconds of a day  */
    private const val ONE_DAY = 86400000L
    /**
     * Current time minus 1 year, just in case software clock goes back due to
     * time synchronization
     */
    private val NOT_BEFORE: Date = Date(System.currentTimeMillis() - ONE_DAY * 365)

    /**
     * The maximum possible value in X.509 specification: 9999-12-31 23:59:59,
     * new Date(253402300799000L), but Apple iOS 8 fails with a certificate
     * expiration date grater than Mon, 24 Jan 6084 02:07:59 GMT (issue #6).
     *
     * Hundred years in the future from starting the proxy should be enough.
     */
    private val NOT_AFTER = Date(System.currentTimeMillis() + ONE_DAY * 365 * 100)

    private val SIGNATURE_ALGORITHM = (if (is32BitJvm()) "SHA256" else "SHA512") + "WithRSAEncryption"

    /**
     * 创建KeyStore，自签名证书，且是根证书，即CA证书
     */
    fun createRootCertificate(authority: Authority): KeyStore {
        // 生成RSA的密钥对
        val keyPair = generateKeyPair(ROOT_KEYSIZE);

        // 证书的颁发者信息
        val nameBuilder = X500NameBuilder(BCStyle.INSTANCE)
        nameBuilder.addRDN(BCStyle.CN, authority.CN)
        nameBuilder.addRDN(BCStyle.O, authority.O)
        nameBuilder.addRDN(BCStyle.OU, authority.OU)
        val issuer = nameBuilder.build()
        // 证书的拥有者信息，因为是自签名证书，所有颁发者与拥有者是一样的
        val subject = issuer

        // 生成序列号
        val serial = BigInteger.valueOf(initRandomSerial())

        // 公钥
        val publicKey = keyPair.public

        // 生成证书
        val generator = JcaX509v3CertificateBuilder(issuer, serial, NOT_BEFORE, NOT_AFTER, subject, publicKey)
        // 添加扩展信息：公钥的标识，非关键
        generator.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(publicKey))
        // 添加扩展信息：标识为根证书，即CA=true，关键
        generator.addExtension(Extension.basicConstraints, true, BasicConstraints(true))
        // 添加扩展信息：使用信息，非关键
        val usage = KeyUsage(KeyUsage.keyCertSign
                or KeyUsage.digitalSignature or KeyUsage.keyEncipherment
                or KeyUsage.dataEncipherment or KeyUsage.cRLSign)
        generator.addExtension(Extension.keyUsage, false, usage)
        // 添加扩展信息：扩展使用信息
        val purposes = ASN1EncodableVector()
        purposes.add(KeyPurposeId.id_kp_serverAuth)
        purposes.add(KeyPurposeId.id_kp_clientAuth)
        purposes.add(KeyPurposeId.anyExtendedKeyUsage)
        generator.addExtension(Extension.extendedKeyUsage, false, DERSequence(purposes))

        // 先自签名（使用本身的私钥给证书生成数字签名），再生成证书文件
        val cert = signCertificate(generator, keyPair.private)

        // 生成Keystore(密钥仓库)文件
        val result = KeyStore.getInstance(KEY_STORE_TYPE)
        result.load(null, null)
        result.setKeyEntry(
            authority.alias, // 别名
            keyPair.private, // 私钥
            authority.password.toCharArray(), // keystore的密码
            arrayOf(cert) // 证书
        )
        return result
    }

    fun createServerKeypair(authority: Authority, caAuthority: Authority): KeyStore {
        // 生成密钥对
        val keyPair = generateKeyPair(FAKE_KEYSIZE)

        // 加载ca.pk12, 获取根证书和私钥
        val caKeyStore: KeyStore = loadKeyStore(caAuthority.aliasFile(), caAuthority.password)
        val caCert = caKeyStore.getCertificate(caAuthority.alias)
        val caPrivKey = caKeyStore.getKey(caAuthority.alias, caAuthority.password.toCharArray()) as PrivateKey

        // 获取ca根下书的所有者信息，当作Server证书的颁发者
        val issuer: X500Name = X509CertificateHolder(caCert.encoded).subject

        // 证书的序列号
        val serial = BigInteger.valueOf(initRandomSerial())

        // 证书所有者信息
        val name = X500NameBuilder(BCStyle.INSTANCE)
        name.addRDN(BCStyle.CN, authority.CN)
        name.addRDN(BCStyle.O, authority.O)
        name.addRDN(BCStyle.OU, authority.OU)
        val subject = name.build()

        // 创建证书
        val builder = JcaX509v3CertificateBuilder(issuer, serial, NOT_BEFORE,
            Date(System.currentTimeMillis() + ONE_DAY * 1), subject, keyPair.public)

        builder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(keyPair.public))
        builder.addExtension(Extension.basicConstraints, false, BasicConstraints(false))
        // 添加dns信息
        builder.addExtension(Extension.subjectAlternativeName, false, parseSubjectAlternativeName(authority.dnsList))

        // CA私钥对证书做数字签名
        val cert = signCertificate(builder, caPrivKey)
        cert.checkValidity(Date())
        cert.verify(caCert.publicKey)

        // 生成Keystore(密钥仓库)文件
        val result = KeyStore.getInstance(KeyStore.getDefaultType())
        result.load(null, null)

        result.setKeyEntry(
            authority.alias,
            keyPair.private,
            authority.password.toCharArray(),
            arrayOf<Certificate>(cert, caCert)
        )
        return result
    }

    private fun parseSubjectAlternativeName(dnsList: List<String>): GeneralNames {
        val encodedSANs: MutableList<GeneralName> = ArrayList(dnsList.size)
        for (dns in dnsList) {
            encodedSANs.add(GeneralName(GeneralName.dNSName, dns))
        }
        return GeneralNames(encodedSANs.toTypedArray())
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    private fun loadKeyStore(file: File, password: String): KeyStore {
        val ks = KeyStore.getInstance(KEY_STORE_TYPE)
        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(file)
            ks.load(inputStream, password.toCharArray())
        } finally {
            inputStream?.close()
        }
        return ks
    }

    private fun is32BitJvm(): Boolean {
        val bits = Integer.getInteger("sun.arch.data.model")
        return bits != null && bits == 32
    }

    @Throws(OperatorCreationException::class, CertificateException::class)
    private fun signCertificate(certificateBuilder: X509v3CertificateBuilder, signedWithPrivateKey: PrivateKey): X509Certificate {
        val signer: ContentSigner = JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
            .build(signedWithPrivateKey)
        return JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
            .getCertificate(certificateBuilder.build(signer))
    }

    @Throws(IOException::class)
    private fun createSubjectKeyIdentifier(key: Key): SubjectKeyIdentifier {
        val bIn = ByteArrayInputStream(key.encoded)

        var inputStream: ASN1InputStream? = null
        try {

            inputStream =  ASN1InputStream(bIn)
            val seq = inputStream?.readObject() as ASN1Sequence
            val info = SubjectPublicKeyInfo(seq)
            return BcX509ExtensionUtils().createSubjectKeyIdentifier(info)

        } finally {
            inputStream?.close()
        }
    }

    private fun initRandomSerial(): Long {
        val rnd = Random()
        rnd.setSeed(System.currentTimeMillis())
        // prevent browser certificate caches, cause of doubled serial numbers
        // using 48bit random number
        // prevent browser certificate caches, cause of doubled serial numbers
        // using 48bit random number
        var sl = rnd.nextInt().toLong() shl 32 or (rnd.nextInt().toLong() and 0xFFFFFFFFL)
        // let reserve of 16 bit for increasing, serials have to be positive
        sl = sl and 0x0000FFFFFFFFFFFFL
        return sl
    }

    private fun generateKeyPair(keySize: Int): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(KEYGEN_ALGORITHM)
        val secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM)

        keyPairGenerator.initialize(keySize, secureRandom)
        return keyPairGenerator.generateKeyPair()
    }



}