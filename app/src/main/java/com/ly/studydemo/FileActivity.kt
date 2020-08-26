package com.ly.studydemo

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.EnvironmentCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class FileActivity : Activity(), View.OnClickListener {

    private var resultTextView: TextView? = null

    private var initData: String? = null

    private var spData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_file)

        findViewById<Button>(R.id.sp_btn).setOnClickListener(this@FileActivity)
        findViewById<Button>(R.id.sdcard_btn).setOnClickListener(this@FileActivity)

        resultTextView = findViewById(R.id.result)

        initData = "内部目录：${this@FileActivity.filesDir}; 外部目录：${this@FileActivity.externalCacheDir}"

        updateResult()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.sp_btn -> spHandle()
            R.id.sdcard_btn -> sdcardHandle()
        }
    }

    private fun sdcardHandle() {
        val file: File = File(this@FileActivity.externalCacheDir, "test.txt")

        val fos = FileOutputStream(file)
        val writer: OutputStreamWriter = OutputStreamWriter(fos, "UTF-8")

        writer.append("你好")

        writer.close()
        fos.close()

        Toast.makeText(this, "文件添加成功", Toast.LENGTH_LONG).show()
    }

    private fun spHandle() {
        val sharedPreferences = this.getSharedPreferences("liuyang", Context.MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putString("name", "liuyang")
            commit()
        }

        spData = sharedPreferences.getString("name", "")

        updateResult()
    }

    private fun updateResult() {
        resultTextView?.text = "${initData} \n sp=${spData} \n"
    }
}