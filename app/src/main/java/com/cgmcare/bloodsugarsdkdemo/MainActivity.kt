package com.cgmcare.bloodsugarsdkdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cgmcare.bloodSugar.InterfaceShare.CgmCareManage
import com.cgmcare.bloodSugar.InterfaceShare.DeviceAuthenticationListener

class MainActivity : AppCompatActivity() {
    private var mContext: Context? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        initListener()
        //初始化SDK
        CgmCareManage.getInstance().initApplication(application)
        //打印debug日志
        CgmCareManage.getInstance().setPrintDebugLog(true)
        authentication()//SDK鉴权

    }

    private fun initListener() {
        findViewById<Button>(R.id.tvAuthentication).setOnClickListener(onClickListener)
        findViewById<Button>(R.id.tvNFCCardReader).setOnClickListener(onClickListener)
        findViewById<Button>(R.id.tvNFCIntent).setOnClickListener(onClickListener)
        findViewById<Button>(R.id.tvNFCActivation).setOnClickListener(onClickListener)
        findViewById<Button>(R.id.tvScan).setOnClickListener(onClickListener)
        findViewById<Button>(R.id.tvReconnect).setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.tvAuthentication -> {
                authentication()
            }

            R.id.tvNFCCardReader -> {//NFC-读卡器模式
                startActivity(Intent(mContext, NfcCardReadActivity::class.java))
            }

            R.id.tvNFCIntent -> {//NFC-前台调度模式
                startActivity(Intent(mContext, NfcIntentActivity::class.java))
            }

            R.id.tvNFCActivation -> {//NFC-瞬感探头激活
                startActivity(Intent(mContext, NfcActivationActivity::class.java))
            }

            R.id.tvScan -> {//糖动发射器蓝牙扫描
                startActivity(Intent(mContext, BluetoothScanActivity::class.java))
            }

            R.id.tvReconnect -> {//糖动发射器蓝牙重连
                startActivity(Intent(mContext, BluetoothReconnectActivity::class.java))
            }

        }
    }

    /**
     * SDK鉴权认证，
     */
    private fun authentication() {
        val secret = "sQSyK6KTEuV1EoKZ34s6Q4222K6440S4"
        CgmCareManage.getInstance().authentication(secret, object : DeviceAuthenticationListener {
            override fun onSuccess() {
                showToast("SDK鉴权成功")
            }

            override fun onFail(s: String) {
                showToast("SDK鉴权失败：$s")
            }
        })
    }

    fun showToast(msg: String?) {
        Toast.makeText(
            applicationContext, msg,
            Toast.LENGTH_SHORT
        ).show()
    }

}