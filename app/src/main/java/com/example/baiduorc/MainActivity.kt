package com.example.baiduorc

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.ui.camera.CameraActivity
import com.tbruyelle.rxpermissions3.RxPermissions

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RxPermissions(this)
            .request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            .subscribe { granted ->
                if (granted) {
                    // All requested permissions are granted
                } else {
                    finish()
                    // At least one permission is denied
                }
            }

        findViewById<TextView>(R.id.textView).setOnClickListener {
            if (!hasGotToken) return@setOnClickListener

            val intent = Intent(
                this@MainActivity,
                CameraActivity::class.java
            )
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, mRequestCode)
        }

        initAccessTokenWithAkSk()
    }

    private var hasGotToken = false

    /**
     * 用明文ak，sk初始化
     */
    private fun initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                val token = result.accessToken
                hasGotToken = true
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()

            }
        }, applicationContext, "YEeXLdYGnGwIITxV1XYq0MUQ", "996qGSKyQajM32Xc8w6LDTYvtDzqgASh")
    }

    private val mRequestCode = 2333

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        // 识别成功回调，通用文字识别（高精度版）
        if (requestCode == mRequestCode && resultCode == RESULT_OK) {
            RecognizeService.recGeneralBasic(this,
                FileUtil.getSaveFile(applicationContext).absolutePath
            ) {
                copeData()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun copeData(){

    }

}