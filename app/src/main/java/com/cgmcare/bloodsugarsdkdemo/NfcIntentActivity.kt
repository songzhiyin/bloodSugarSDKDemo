package com.cgmcare.bloodsugarsdkdemo

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import com.cgmcare.bloodSugar.InterfaceShare.CgmCareManage
import com.cgmcare.bloodSugar.InterfaceShare.DeviceBloodSugarMessageListener
import com.cgmcare.bloodSugar.InterfaceShare.DeviceReadFailureMessageListener
import com.cgmcare.bloodSugar.enums.NFCReadType


/**
 * NFC——前台调度模式
 */
class NfcIntentActivity : BaseActivity() {
    private var message: StringBuffer = StringBuffer()
    private lateinit var tvMessage: TextView
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var nfcAdapter: NfcAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_intent)
        tvMessage = findViewById(R.id.tvMessage)
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        mPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        //设置NFC扫描模式  NFCReadType.onIntent 前台调度模式
        CgmCareManage.getInstance().initNFC(this, NFCReadType.onIntent)
        CgmCareManage.getInstance().setPendingIntent(mPendingIntent)
        CgmCareManage.getInstance().setNfcAdapter(nfcAdapter)
        //设置NFC扫描时的手机振动
        CgmCareManage.getInstance().setNfcPhoneVibrate(true)
        //设置NFC扫描异常信息回调
        CgmCareManage.getInstance()
            .setDeviceFailureMessageListeners(deviceReadFailureMessageListener)
        //设置瞬感探头设备和血糖数据的回调
        CgmCareManage.getInstance()
            .setDeviceBloodSugarMessageListeners(deviceBloodSugarMessageListener)

    }

    override fun onResume() {
        super.onResume()
        //开启扫描，必须在onResume生命周期方法中调用
        CgmCareManage.getInstance().onEnableNFC()
    }

    override fun onPause() {
        super.onPause()
        //结束扫描，必须在onPause生命周期方法中调用
        CgmCareManage.getInstance().onDisableNFC()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //前台调度模式必须方法
        CgmCareManage.getInstance().nfcToOnNewIntent(intent)
    }


    /**
     * 读取数据的异常信息回调
     */
    private val deviceReadFailureMessageListener = DeviceReadFailureMessageListener { msg, type ->
        message.append("\n")
        message.append("\n" + msg)
        tvMessage.text = message.toString()
    }

    /**
     * 血糖和设备信息的回调接口
     */
    private val deviceBloodSugarMessageListener = DeviceBloodSugarMessageListener { deviceEntity ->
        message.append("\n")
        message.append("\n" + "" + deviceEntity.print)
        if (deviceEntity.bloodSugarEntities != null && deviceEntity.bloodSugarEntities.size > 0) {
            message.append("\n")
            message.append("\n" + "" + deviceEntity.bloodSugarEntities.get(deviceEntity.bloodSugarEntities.size - 1).print)
        }
        tvMessage.text = message.toString()
    }
}