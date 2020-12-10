package com.ly.studydemo.tcpip

import java.io.File

/**
 * 证书的信息数据类
 * @property keystoreFile Keystore文件目录
 * @property alias 别名
 * @property password keystore的密码
 * @property CN 你的姓名
 * @property OU 你的组织单位名称
 * @property O 你的组织名称
 * @property L 你所在的城市名称
 * @property S 你所在的省份名称
 * @property C 你的国家名称
 * @property dnsList 此证书所支持的域名列表
 */
data class Authority(
    val keyStoreDir: File,
    val alias: String,
    val password: String,
    val CN: String,
    val OU: String,
    val O: String,
    val L: String,
    val S: String,
    val C: String,
    val dnsList: List<String>
) {
    fun aliasFile(): File {
        return File(keyStoreDir, alias + CertificateHelper.KEY_STORE_FILE_EXTENSION)
    }
    fun pemFile(): File {
        return File(keyStoreDir, alias + ".pem")
    }
}