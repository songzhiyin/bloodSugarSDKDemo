package com.cgmcare.bloodsugarsdkdemo

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import com.cgmcare.bloodSugar.InterfaceShare.CgmCareManage
import com.cgmcare.bloodSugar.InterfaceShare.DeviceSensorActivateListener
import com.cgmcare.bloodSugar.enums.NFCReadType

/**
 * NFC——瞬感探头激活
 */
class NfcActivationActivity : BaseActivity() {
    private var message: StringBuffer = StringBuffer()
    private lateinit var tvMessage: TextView
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var nfcAdapter: NfcAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_activation)
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
        //设置NFC扫码手机震动
        CgmCareManage.getInstance().setNfcPhoneVibrate(true)
        //设置允许NFC扫描激活瞬感探头
        CgmCareManage.getInstance().setAllowAbbottActivation(true)
        //检查是否允许探头激活
        CgmCareManage.getInstance().checkAllowAbbottActivation()
        //瞬感探头回调
        CgmCareManage.getInstance()
            .setDeviceSensorActivateListeners(object : DeviceSensorActivateListener {
                override fun onActivateStatus(isActivated: Boolean) {
                    message.append("\n")
                    message.append("\n是否激活成功：$isActivated")
                    tvMessage.text = message
                }

                override fun onFailure(msg: String) {
                    message.append("\n")
                    message.append("\n异常信息：$msg")
                    tvMessage.text = message
                }
            })

    }

    override fun onResume() {
        super.onResume()
        //开启扫描
        CgmCareManage.getInstance().onEnableNFC()
    }

    override fun onPause() {
        super.onPause()
        //结束扫描
        CgmCareManage.getInstance().onDisableNFC()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //前台调度模式必须方法
        CgmCareManage.getInstance().nfcToOnNewIntent(intent)
    }

    override fun onDestroy() {
        //离开激活界面之前，需要将参数重置，否则会影响瞬感的读取
        CgmCareManage.getInstance().setAllowAbbottActivation(false)
        super.onDestroy()
    }
}