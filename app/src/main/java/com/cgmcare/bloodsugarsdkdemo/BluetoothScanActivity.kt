package com.cgmcare.bloodsugarsdkdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.cgmcare.bloodSugar.InterfaceShare.CgmCareManage
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * 蓝牙设备扫描搜索
 */
class BluetoothScanActivity : BaseActivity() {
    private lateinit var tvMessage: TextView
    private lateinit var mContext: Context

    @SuppressLint("MissingInflatedId", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_scan)
        mContext = this
        tvMessage = findViewById(R.id.tvMessage)
        findViewById<Button>(R.id.tvScan).setOnClickListener {
            if (!XXPermissions.isGranted(
                    mContext, Permission.BLUETOOTH_SCAN,
                    Permission.BLUETOOTH_CONNECT,
                    Permission.BLUETOOTH_ADVERTISE
                )
            ) {
                XXPermissions.with(mContext)
                    .permission(Permission.BLUETOOTH_SCAN)
                    .permission(Permission.BLUETOOTH_CONNECT)
                    .permission(Permission.BLUETOOTH_ADVERTISE)
                    .request { permissions, allGranted ->
                        if (allGranted) {
                            searchDevice()
                        }
                    }
            } else {
                //开启蓝牙持续扫描
                searchDevice()
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun searchDevice() {
        //扫描搜索糖动发射器蓝牙设备，该扫描做了排重处理，每个设备只回调一次
        CgmCareManage.getInstance().scanBloodSugarBluetoothDeviceScanSearch { bluetoothDevices ->
            if (bluetoothDevices != null) {
                val message = StringBuffer()
                message.append("蓝牙扫描中，请稍后...")
                for (bluetoothDevice in bluetoothDevices) {
                    message.append("\n")
                    message.append("\n ${bluetoothDevice.name}")
                }
                tvMessage.text = message
            }
        }
    }


    override fun onDestroy() {
        //停止蓝牙扫描
        CgmCareManage.getInstance().stopBloodSugarBluetoothDeviceScan()
        super.onDestroy()
    }
}