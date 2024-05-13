package com.cgmcare.bloodsugarsdkdemo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.cgmcare.bloodSugar.InterfaceShare.*
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * 糖动发射器扫描和连接
 */
class BluetoothReconnectActivity : BaseActivity() {
    private lateinit var edtMessage: EditText
    private lateinit var tvMessage: TextView
    private val message = StringBuffer()
    private var name: String? = null
    private lateinit var mContext: Context

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_scan_connect)
        edtMessage = findViewById(R.id.edtMessage)
        tvMessage = findViewById(R.id.tvMessage)
        mContext = this
        //设置异常信息回调
        CgmCareManage.getInstance().addDeviceReadFailureMessageListener(deviceReadFailureMessageListener)
        //设置蓝牙扫描设备的回调，蓝牙设备扫描到就会回调没有排重，同一个设备回调的时间间隔是5秒
        CgmCareManage.getInstance().addDeviceBluetoothScanAlwaysListener(deviceBluetoothScanAlwaysListener)
        //血糖接收数据的回调
        CgmCareManage.getInstance().addDeviceBloodSugarMessageListener(deviceBloodSugarMessageListener)
        //蓝牙连接状态的回调
        CgmCareManage.getInstance().addDeviceBluetoothConnectListener(deviceBluetoothConnectListener)

        findViewById<Button>(R.id.tvScan).setOnClickListener(View.OnClickListener {
            name = edtMessage.text.toString().trim { it <= ' ' }
            message.delete(0, message.length)
            message.append("开始蓝牙扫描...")
            tvMessage.text = message
            scanDevice()
        })
    }

    private fun scanDevice() {
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
                        // 开启蓝牙持续扫描
                        CgmCareManage.getInstance().scanBloodSugarBluetoothDeviceScanAlways()
                    }
                }
        } else {
            //开启蓝牙持续扫描
            CgmCareManage.getInstance().scanBloodSugarBluetoothDeviceScanAlways()
        }
    }

    override fun onDestroy() {
        //停止蓝牙扫描
        CgmCareManage.getInstance().stopBloodSugarBluetoothDeviceScan()
        //断开蓝牙连接
        CgmCareManage.getInstance().dismissBloodSugarBluetoothDevice()
        super.onDestroy()
    }

    /**
     * 异常信息的回调
     */
    private val deviceReadFailureMessageListener =
        DeviceReadFailureMessageListener { msg, type ->
            message.append("\n")
            message.append("\n异常：$msg")
            tvMessage.text = message
        }

    /**
     * 设备持续扫描的回调
     */
    @SuppressLint("MissingPermission")
    private val deviceBluetoothScanAlwaysListener =
        DeviceBluetoothScanAlwaysListener { device ->
            var deviceName = device.name
            if (!TextUtils.isEmpty(deviceName) && deviceName == name) {
                message.append("\n")
                message.append("\n扫描到设备，开始连接设备")
                tvMessage.text = message
                //能扫描到则证明该设备已经断联了，直接连接就行
                CgmCareManage.getInstance()
                    .connectBloodSugarBluetoothDevice(deviceName, device.address)
            }
        }

    /**
     * 血糖数据获取的回调
     */
    private val deviceBloodSugarMessageListener =
        DeviceBloodSugarMessageListener { deviceEntity ->
            message.append("\n")
            message.append("\n" + "" + deviceEntity.print)
            if (deviceEntity.bloodSugarEntities != null && deviceEntity.bloodSugarEntities.size > 0) {
                message.append("\n")
                message.append("\n" + deviceEntity.bloodSugarEntities.get(deviceEntity.bloodSugarEntities.size - 1).print)
            }
            tvMessage.text = message.toString()
        }

    /**
     * 蓝牙连接状态的回调
     */
    private val deviceBluetoothConnectListener =
        DeviceBluetoothConnectListener { connected ->
            message.append("\n")
            message.append("\n蓝牙连接状态：$connected")
            tvMessage.text = message
        }
}