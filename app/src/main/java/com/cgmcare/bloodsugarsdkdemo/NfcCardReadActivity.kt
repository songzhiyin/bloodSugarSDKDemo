package com.cgmcare.bloodsugarsdkdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import com.cgmcare.bloodSugar.InterfaceShare.CgmCareManage
import com.cgmcare.bloodSugar.InterfaceShare.DeviceBloodSugarMessageListener
import com.cgmcare.bloodSugar.InterfaceShare.DeviceReadFailureMessageListener
import com.cgmcare.bloodSugar.enums.NFCReadType

/**
 * NFC——读卡器模式
 */
class NfcCardReadActivity : BaseActivity() {
    private var message: StringBuffer = StringBuffer()
    private lateinit var tvMessage: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_card_read)
        tvMessage = findViewById(R.id.tvMessage)
        //设置NFC扫描模式  NFCReadType.onCardReader 读卡器NFC扫描模式
        CgmCareManage.getInstance().initNFC(this, NFCReadType.onCardReader)
        //设置NFC扫码手机震动
        CgmCareManage.getInstance().setNfcPhoneVibrate(true)
        //设置NFC扫描异常信息回调
        CgmCareManage.getInstance()
            .addDeviceReadFailureMessageListener(deviceReadFailureMessageListener)
        //设置瞬感探头设备和血糖数据的回调
        CgmCareManage.getInstance()
            .addDeviceBloodSugarMessageListener(deviceBloodSugarMessageListener)
    }

    override fun onResume() {
        super.onResume()
        //开启扫描，必须在onResume生命周期方法中调用
        CgmCareManage.getInstance().onResumeNFC()
    }

    override fun onPause() {
        super.onPause()
        //结束NFC扫描，必须在onPause生命周期方法中调用
        CgmCareManage.getInstance().onPauseNFC()
    }

    /**
     * 读取数据的异常信息回调
     */
    private val deviceReadFailureMessageListener =
        DeviceReadFailureMessageListener { msg, type ->
            message.append("\n")
            message.append(msg)
            tvMessage.text = message
        }

    /**
     * 血糖和设备信息的回调接口
     */
    private val deviceBloodSugarMessageListener =
        DeviceBloodSugarMessageListener { deviceEntity ->
            message.append("\n")
            message.append("\n" + "" + deviceEntity.print)
            if (deviceEntity.bloodSugarEntities != null && deviceEntity.bloodSugarEntities.size > 0) {
                message.append("\n")
                message.append("\n" + "" + deviceEntity.bloodSugarEntities.get(deviceEntity.bloodSugarEntities.size - 1).print)
            }
            tvMessage.text = message.toString()
        }


}