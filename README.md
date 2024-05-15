# 糖动SDK使用说明

### 集成步骤

* 请在build.gradle中设置仓库的访问凭证

```groovy

allprojects {
    repositories {
        maven {
            url 'https://maven.aliyun.com/repository/public'
        }
        maven {
            credentials {
                username '644743f5840f2501c4ff47d3'
                password 'iYSfTLX3ivhB'
            }
            url 'https://packages.aliyun.com/maven/repository/2369369-release-3RKhpV/'
        }
        maven {
            credentials {
                username '644743f5840f2501c4ff47d3'
                password 'iYSfTLX3ivhB'
            }
            url 'https://packages.aliyun.com/maven/repository/2369369-snapshot-xBNR34/'
        }
    }
}

```

* 在你的build.gradle文件中加入你要引用的文件信息。

```groovy

dependencies {
    implementation 'com.cgmcare.sdk:bloodSugar:2.1.1'
}

```

### 清单文档中需要添加的权限

```groovy

< !--NFC扫描权限-- >
        < uses - permission android: name = "android.permission.NFC" / >
        < !--ACCESS_COARSE_LOCATION 、 ACCESS_FINE_LOCATION这些权限允许您的应用程序访问设备的位置信息 ， 用于蓝牙扫描和定位蓝牙设备 。 -- >
        < uses - permission android: name = "android.permission.ACCESS_COARSE_LOCATION" / >
        < uses - permission android: name = "android.permission.ACCESS_FINE_LOCATION" / >
        < !--此权限允许您的应用程序连接到蓝牙设备以进行数据传输 。 -- >
        < uses - permissionandroid : name = "android.permission.BLUETOOTH" android: maxSdkVersion = "30" / >
        < !--此权限允许您的应用程序扫描周围的蓝牙设备 、 发现新设备和建立蓝牙连接等高级操作 。 -- >
        < uses - permissionandroid : name = "android.permission.BLUETOOTH_ADMIN" android: maxSdkVersion = "30" / >
        < !--为了保护用户隐私 ， 自 Android 10 起 ， 应用程序需要请求此权限才能在后台继续使用位置信息 。 -- >
        < uses - permission android: name = "android.permission.ACCESS_BACKGROUND_LOCATION" / >
        < !--安卓12新增权限 ， 蓝牙扫描周围设备的权限-- >
        < uses - permissionandroid : name = "android.permission.BLUETOOTH_SCAN" android: usesPermissionFlags = "neverForLocation" / >
        < !--安卓12新增权限 ， 仅当应用使设备可供蓝牙设备发现时才需要 。 -- >
        < uses - permission android: name = "android.permission.BLUETOOTH_ADVERTISE" / >
        < !--安卓12新增权限 ， 仅当应用与已配对的蓝牙设备通信时才需要-- >
        < uses - permission android: name = "android.permission.BLUETOOTH_CONNECT" / >
        < !--网络请求权限-- >
        < uses - permission android: name = "android.permission.INTERNET" / >
        < !--手机振动的权限-- >
        < uses - permission android: name = "android.permission.VIBRATE" / >


```

### 需要动态申请的权限

```groovy

安卓12以上的
android.permission.BLUETOOTH_SCAN 扫描蓝牙设备的权限
android.permission.BLUETOOTH_CONNECT 连接蓝牙设备的权限
android.permission.BLUETOOTH_ADVERTISE 方便其他设备发现

安卓10以上的
android.permission.ACCESS_BACKGROUND_LOCATION 后台定位权限用于SDK后台扫描时使用

安卓6以上
android:
name = "android.permission.ACCESS_FINE_LOCATION 定位权限，蓝牙扫描需要使用到定位
android:
name = "android.permission.ACCESS_COARSE_LOCATION 


```

### SDK初始化

```groovy

//初始化SDK
CgmCareManage.getInstance().initApplication(getApplication());
//SDK鉴权认证
String secret = "糖动官网申请";
CgmCareManage.getInstance().authentication(secret, new DeviceAuthenticationListener() {
    @Override
    public void onSuccess() {
        showToast("SDK鉴权成功");
    }

    @Override
    public void onFail(String s) {
        showToast("SDK鉴权失败：" + s);
    }
});


```

### NFC扫描(NFC读取瞬感探头数据、NFC激活瞬感探头)

* 1、读卡器模式进行NFC读取

```groovy

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //设置NFC扫描模式  NFCReadType.onCardReader 读卡器NFC扫描模式，该方法必须在onCreate方法中执行
    CgmCareManage.getInstance().initNFC(this, NFCReadType.onCardReader);
    //设置NFC扫码手机震动
    CgmCareManage.getInstance().setNfcPhoneVibrate(true);
    //设置NFC读取数据的异常信息回调
    CgmCareManage.getInstance().addDeviceReadFailureMessageListener(deviceReadFailureMessageListener);
    //设置瞬感探头设备和血糖数据的回调
    CgmCareManage.getInstance().addDeviceBloodSugarMessageListener(deviceBloodSugarMessageListener);
   
}

@Override
protected void onResume() {
    message.delete(0, message.length());
    //恢复NFC扫描监听，该方法必须在onResume生命周期方法中执行
    CgmCareManage.getInstance().onResumeNFC();
    super.onResume();
}

@Override
protected void onPause() {
    //取消NFC扫描监听，该方法必须onPause生命周期方法中执行
    CgmCareManage.getInstance().onPauseNFC();
    super.onPause();
}
/**
 * 读取数据的异常信息回调
 */
private DeviceReadFailureMessageListener deviceReadFailureMessageListener = new DeviceReadFailureMessageListener() {
    @Override
    public void onNotifyFailure(String msg, FailureType type) {

    }
};

/**
 * 血糖和设备信息的回调接口
 */
private DeviceBloodSugarMessageListener deviceBloodSugarMessageListener = new DeviceBloodSugarMessageListener() {
    @Override
    public void onUpdateDeviceMessage(DeviceEntity deviceEntity) {

    }
};

```

* 2、前台调度模式进行NFC读取(前台调度模式的NFC扫描时，系统有铃声提示)

```groovy


private PendingIntent mPendingIntent;
protected NfcAdapter nfcAdapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
    } else {
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    //设置NFC扫描模式  NFCReadType.onIntent 前台调度模式
    CgmCareManage.getInstance().initNFC(this, NFCReadType.onIntent);
    CgmCareManage.getInstance().setPendingIntent(mPendingIntent);
    CgmCareManage.getInstance().setNfcAdapter(nfcAdapter);
    //设置NFC扫码手机震动
    CgmCareManage.getInstance().setNfcPhoneVibrate(true);
    //设置NFC扫描异常信息回调
    CgmCareManage.getInstance().addDeviceReadFailureMessageListener(deviceReadFailureMessageListener);
    //设置瞬感探头设备和血糖数据的回调
    CgmCareManage.getInstance().addDeviceBloodSugarMessageListener(deviceBloodSugarMessageListener);
}


@Override
protected void onResume() {
    super.onResume();
    //恢复NFC扫描监听，该方法必须在onResume生命周期方法中执行
    CgmCareManage.getInstance().onResumeNFC();
}


@Override
protected void onPause() {
    super.onPause();
    //取消NFC扫描监听，该方法必须在onPause生命周期方法中执行
    CgmCareManage.getInstance().onPauseNFC();
}

@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    //前台调度模式必须方法
    CgmCareManage.getInstance().nfcToOnNewIntent(intent);
}

/**
 * 读取数据的异常信息回调
 */
private DeviceReadFailureMessageListener deviceReadFailureMessageListener = new DeviceReadFailureMessageListener() {
    @Override
    public void onNotifyFailure(String msg, FailureType type) {

    }
};


/**
 * 血糖和设备信息的回调接口
 */
private DeviceBloodSugarMessageListener deviceBloodSugarMessageListener = new DeviceBloodSugarMessageListener() {
    @Override
    public void onUpdateDeviceMessage(DeviceEntity deviceEntity) {

    }
};

```

* 3、传递标签tag NFC的扫描逻辑APP自己处理

```groovy

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CgmCareManage.getInstance().addDeviceReadFailureMessageListener(deviceReadFailureMessageListener);
    CgmCareManage.getInstance().addDeviceBloodSugarMessageListener(deviceBloodSugarMessageListener);
}

private void doReadNFC(Tag tag) {
    //SDK直接读取tag标签
    CgmCareManage.getInstance().nfcReadTag(tag);
}

/**
 * 读取数据的异常信息回调
 */
private DeviceReadFailureMessageListener deviceReadFailureMessageListener = new DeviceReadFailureMessageListener() {
    @Override
    public void onNotifyFailure(String msg, FailureType type) {

    }
};


/**
 * 血糖和设备信息的回调接口
 */
private DeviceBloodSugarMessageListener deviceBloodSugarMessageListener = new DeviceBloodSugarMessageListener() {
    @Override
    public void onUpdateDeviceMessage(DeviceEntity deviceEntity) {

    }
};

```

### 激活瞬感探头

```groovy

//开启NFC激活功能
CgmCareManage.getInstance().enableAbbottActivation();
//禁止NFC激活功能
CgmCareManage.getInstance().disableAbbottActivation();

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initNFC();
    //开启NFC激活功能
    CgmCareManage.getInstance().enableAbbottActivation();
    //激活瞬感探头的回调
    CgmCareManage.getInstance().addDeviceSensorActivateListeners(deviceSensorActivateListener);
}
/**
 * 瞬感探头激活的回调
 */
private DeviceSensorActivateListener deviceSensorActivateListener = new DeviceSensorActivateListener() {
    @Override
    public void onActivateStatus(boolean isActivated) {
        message.append("\n" + "探头激活结果：" + isActivated);
        tvMessage.setText(message.toString());
    }

    @Override
    public void onFailure(String msg) {
        message.append("\n" + "异常信息：" + msg);
        tvMessage.setText(message.toString());
    }
};

```

### 蓝牙连接

* 1、基本回调方法

```groovy

//设备读取数据的异常信息回调
CgmCareManage.getInstance().addDeviceReadFailureMessageListener(deviceReadFailureMessageListener);
CgmCareManage.getInstance().removeDeviceReadFailureMessageListener(deviceReadFailureMessageListener);
CgmCareManage.getInstance().removeDeviceReadFailureMessageListenerAll();

//设置蓝牙扫描设备的回调，蓝牙设备扫描到就会回调没有排重，同一个设备回调的时间间隔是5秒
CgmCareManage.getInstance().addDeviceBluetoothScanAlwaysListener(deviceBluetoothScanAlwaysListener);
CgmCareManage.getInstance().removeDeviceBluetoothScanAlwaysListener(deviceBluetoothScanAlwaysListener);
CgmCareManage.getInstance().removeDeviceBluetoothScanAlwaysListenerAll();

//血糖接收数据的回调
CgmCareManage.getInstance().addDeviceBloodSugarMessageListener(deviceBloodSugarMessageListener);
CgmCareManage.getInstance().removeDeviceBloodSugarMessageListener(deviceBloodSugarMessageListener);
CgmCareManage.getInstance().removeDeviceBloodSugarMessageListenerAll();

//蓝牙连接状态的回调
CgmCareManage.getInstance().addDeviceBluetoothConnectListener(deviceBluetoothConnectListener);
CgmCareManage.getInstance().removeDeviceBluetoothConnectListener(deviceBluetoothConnectListener);
CgmCareManage.getInstance().removeDeviceBluetoothConnectListenerAll();

//设置NFC瞬感探头激活的回调
CgmCareManage.getInstance().addDeviceSensorActivateListener(deviceSensorActivateListener);
CgmCareManage.getInstance().removeDeviceSensorActivateListener(deviceSensorActivateListener);
CgmCareManage.getInstance().removeDeviceSensorActivateListenerAll();



```

* 2、蓝牙扫描，扫描到的蓝牙在deviceBluetoothScanListener回调方法中返回

```groovy

/**
 * 蓝牙扫描搜索糖动设备 (该方法一直扫描不会自动停止，后台扫描需要注意权限的申请)
 */
private void scanDevice() {
    //判断蓝牙权限和定位权限是否动态授权
    if (!XXPermissions.isGranted(mContext, Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_CONNECT,
            Permission.BLUETOOTH_ADVERTISE)) {
        XXPermissions.with(mContext)
                .permission(Permission.BLUETOOTH_SCAN)
                .permission(Permission.BLUETOOTH_CONNECT)
                .permission(Permission.BLUETOOTH_ADVERTISE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (allGranted) {
                            //开启糖动扫描
                            CgmCareManage.getInstance().scanBloodSugarBluetoothDeviceScanAlways();
                        }
                    }
                });
    } else {
        //开启糖动扫描
        CgmCareManage.getInstance().scanBloodSugarBluetoothDeviceScanAlways();
    }

}


/**
 * 设备持续扫描的回调
 */
private DeviceBluetoothScanAlwaysListener deviceBluetoothScanAlwaysListener = new DeviceBluetoothScanAlwaysListener() {
    @Override
    public void onSuccess(BluetoothDevice device) {
        String deviceName = device.getName();
        if (!TextUtils.isEmpty(deviceName) && deviceName.equals(name)) {
            //能扫描到则证明该设备已经断联了，直接连接就行，重复调用该方法需要间隔最少1秒
            CgmCareManage.getInstance().connectBloodSugarBluetoothDevice(deviceName, device.getAddress());
        }
    }
};


//关闭蓝牙扫描
CgmCareManage.getInstance().stopBloodSugarBluetoothDeviceScan();
//关闭蓝牙连接
CgmCareManage.getInstance().dismissBloodSugarBluetoothDevice();


```

* 3、蓝牙连接，并且自动读取瞬感设备信息和血糖数据

```groovy

//连接蓝牙设备(蓝牙不会自动连接，断联之后会有状态回调，需要APP自己处理重连)
CgmCareManage.getInstance().connectBloodSugarBluetoothDevice(deviceName, device.getAddress());

/**
 * 血糖数据获取的回调
 */
private DeviceBloodSugarMessageListener deviceBloodSugarMessageListener = new DeviceBloodSugarMessageListener() {
    @Override
    public void onUpdateDeviceMessage(DeviceEntity deviceEntity) {
        message.append("\n" + "设备信息：" + deviceEntity.getPrint());

    }
};

//断开蓝牙连接
CgmCareManage.getInstance().dismissBloodSugarBluetoothDevice();

//蓝牙连接状态的回调
private DeviceBluetoothConnectListener deviceBluetoothConnectListener = new DeviceBluetoothConnectListener() {
    @Override
    public void onConnectStatus(boolean connected) {
        message.append("\n" + "蓝牙是否连接正常：" + connected);
        tvMessage.setText(message);
    }
};

//读取数据的异常信息回调
private DeviceReadFailureMessageListener deviceReadFailureMessageListener = new DeviceReadFailureMessageListener() {
    @Override
    public void onNotifyFailure(String msg, FailureType failureType) {
        message.append("\n" + "异常信息：" + msg);
        tvMessage.setText(message);
    }
};

```

### 基本方法和参数说明

* CgmCareManage 糖动SDK管理类

```groovy

/**
 * 初始化SDK
 */
public void initApplication(Application application) 
/**
 * SDK鉴权认证(每次启动APP都需要进行鉴权认证)
 */
public void authentication(String secret, DeviceAuthenticationListener deviceAuthenticationListener)

/**
 * SDK是否鉴权
 */
public boolean isAuthentication() 

/**
 * 设置是否打印debug日志
 */
public void setPrintDebugLog(boolean print)


/**
* 添加设备蓝牙扫描的回调
*/
public void addDeviceBluetoothScanAlwaysListener(DeviceBluetoothScanAlwaysListener listener)

/**
* 删除设备蓝牙扫描的回调
*/
public void removeDeviceBluetoothScanAlwaysListener(DeviceBluetoothScanAlwaysListener listener) 
/**
* 删除设备蓝牙扫描的回调
*/
public void removeDeviceBluetoothScanAlwaysListenerAll()

/**
 * 读取数据的异常信息回调
 */
public void setDeviceFailureMessageListeners(DeviceReadFailureMessageListener listener) 

/**
 * 瞬感设备信息和血糖数据
 */
public void setDeviceBloodSugarMessageListeners(DeviceBloodSugarMessageListener listener) 

/**
* 添加糖动发射器蓝牙连接状态的回调
*/
public void addDeviceBluetoothConnectListener(DeviceBluetoothConnectListener listener)

/**
* 删除糖动发射器蓝牙连接状态的回调
*/
public void removeDeviceBluetoothConnectListener(DeviceBluetoothConnectListener listener)

/**
* 删除糖动发射器蓝牙连接状态的回调
*/
public void removeDeviceBluetoothConnectListenerAll()
    

/**
 * 添加瞬感探头激活的回调
 */
public void addDeviceSensorActivateListener(DeviceSensorActivateListener listener)
/**
 * 删除瞬感探头激活的回调
 */
public void removeDeviceSensorActivateListener(DeviceSensorActivateListener listener)
/**
 * 清除所有的瞬感探头激活的回调
 */
public void removeDeviceSensorActivateListenerAll()



/**
 * 开启NFC激活功能(默认是关闭的)
 * 注：开启NFC激活功能，NFC读取血糖数据功能就无法使用，请及时关闭激活功能
 */
public void enableAbbottActivation()

/**
 * 禁止NFC激活功能
 */
public void disableAbbottActivation()
/**
 * 是否允许探头激活
 */
public boolean checkAllowAbbottActivation()



/**
 * 开启NFC读取数据的功能(默认是开启的)
 */
public void enableAbbottNFCReadData()

/**
 * 禁止NFC读取数据的功能
 */
public void disableAbbottNFCReadData()

/**
 * 是否允许NFC读取数据
 */
public void checkAllowAbbottNFCReadData()




/**
 * 糖动发射器扫描服务UUID
 */
public String getDeviceScanUUID()

/**
 * 蓝牙持续扫描(扫描的蓝牙设备就会返回，没做排重处理)
 */
public void scanBloodSugarBluetoothDeviceScanAlways()

/**
 * 蓝牙设备扫描搜索的回调(扫描的设备做了排重，每个设备最多扫描返回一次，多次扫描到也无效)
 */
public void scanBloodSugarBluetoothDeviceScanSearch(DeviceBluetoothScanSearchListener listener)


/**
 * 蓝牙停止扫描
 */
public void stopBloodSugarBluetoothDeviceScan()

/**
 * 连接蓝牙设备
 * 注意：最少间隔一秒调用该方法，否则该方法失效
 */
public void connectBloodSugarBluetoothDevice(String deviceName, String deviceAddress)

/**
 * 设置设备连接的时间间隔(秒)
 */
public void setBluetoothConnectTimeInterval(int interval)

/**
 * 检验设备是否连接着
 */
public boolean checkDeviceConnect(String deviceAddress)

/**
 * 断开蓝牙设备
 */
public void dismissBloodSugarBluetoothDevice()


/**
 * NFC标签读取
 */
public void nfcReadTag(Tag tag) 

/**
 * NFC扫描完毕是否振动
 */
public void setNfcPhoneVibrate(boolean vibrate)

/**
 * 初始化NFC扫描
 *
 * @param activity
 * @param type NFC扫描模式
 *                 NFCReadType.onCardReader 读卡器模式
 *                 NFCReadType.onIntent 前台调度模式
 */
public void initNFC(final AppCompatActivity activity, NFCReadType type) 

/**
 * NFC前台调度模式必要方法
 * 重写Activity的OnNewIntent方法，在OnNewIntent方法调用nfcToOnNewIntent方法
 * 如果不使用前台调度模式，该方法可以不写
 */
public void nfcToOnNewIntent(Intent intent)

/**
 * 恢复NFC的监听(建议在生命周期OnResume方法调用)
 */
public void onResumeNFC()

/**
 * 取消NFC扫描的监听(建议在生命周期OnPause方法调用)
 */
public void onPauseNFC()

public void setPendingIntent(PendingIntent pendingIntent)

public void setNfcAdapter(NfcAdapter nfcAdapter)


```

* DeviceBloodSugarMessageListener 瞬感设备信息和血糖信息回调

```groovy

//瞬感设备信息和血糖信息回调
public interface DeviceBloodSugarMessageListener {
    void onUpdateDeviceMessage(DeviceEntity deviceEntity);
}

//瞬感探头设备信息
public class DeviceEntity implements Serializable {
    private long sensorStartTime;//启动时间(单位：秒)
    private int battery;//电量
    private long sensorTimeLength;//探头使用时长(单位：分钟)
    private String sensorSerialNumber;//探头编号
    private String BleName;// 蓝牙名字
    private LibreSensorType sensorType;//探头类型
    private LibreSensorState sensorState;//探头状态
    private List<BloodSugarEntity> bloodSugarEntities;//血糖数据
}
/**
 * 瞬感探头类型
 */
public enum LibreSensorType {
    /**
     * 零售1代
     */
    libre1,
    /**
     * 零售2代
     */
    libre2,
    /**
     * 医疗版1代
     */
    libre1ProH,
    /**
     * 未知
     */
    unknown
}

/**
 * 瞬感探头状态
 */
public enum LibreSensorState {
    /**
     * 未激活
     */
    sensorStateNotActivated,
    /**
     * 预热
     */
    sensorStateWarmingUp,
    /**
     * 已激活
     */
    sensorStateActive,
    /**
     * 过期
     */
    sensorStateExpired,
    /**
     * 关机
     */
    sensorStateShutdown,
    /**
     * 故障
     */
    sensorStateFailure,
    /**
     * 未知
     */
    sensorStateUnknown,

}

/**
 * 血糖
 */
public class BloodSugarEntity implements Serializable {
    /**
     * 血糖值
     */
    private float bloodSugar;
    /**
     * 血糖时间(秒)
     */
    private long timeCreate;
    /**
     * 瞬感探头编号
     */
    private String sensorSerialNumber;
    /**
     * 趋势箭头  1为平稳 5为斜升 10为斜降 15为直升 20为为直降
     */
    private int trend;

}


```

* DeviceBluetoothConnectListener 糖动发射器连接状态回调

```groovy

/**
 * 蓝牙连接状态的更新
 */
public interface DeviceBluetoothConnectListener {
    void onConnectStatus(boolean connected);
}

```

* DeviceAuthenticationListener SDK鉴权认证的回调

```groovy


/**
 * SDK鉴权认证回调
 */
public interface DeviceAuthenticationListener {
    void onSuccess();

    void onFail(String message);
}


```

* DeviceBluetoothScanListener 蓝牙设备扫描的回调

```groovy

/**
 * 蓝牙设备持续扫描的回调（蓝牙设备扫描到就会走回调方法，同一个设备间隔5秒）
 */
public interface DeviceBluetoothScanAlwaysListener {
    void onSuccess(BluetoothDevice device);
}

/**
 * 蓝牙设备扫描搜索的回调（蓝牙设备做了排重，同一个设备只会走一次回调方法）
 */
public interface DeviceBluetoothScanSearchListener {
    void onSuccess(List<BluetoothDevice> bluetoothDevices);
}

```

* DeviceReadFailureMessageListener 血糖数据读取异常信息回调

```groovy

/**
 * 血糖读取异常信息提示
 */
public interface DeviceReadFailureMessageListener {
    void onNotifyFailure(String msg, FailureType type);
}

/**
 * 数据读取的异常信息
 */
public enum FailureType {
    /**
     * 无法读取数据(糖动发射器无法读取瞬感探头)
     * 可能情况：1、糖动发射器损坏  2、糖动发射器没有佩戴好 3、瞬感探头损坏
     */
    errorUnableToReadData,
    /**
     * 瞬感探头类型暂不支持
     */
    errorSensorType,
    /**
     * 血糖数据的CRC校验无法通过
     */
    errorDataCRC,
    /**
     * 没有蓝牙权限
     */
    errorBluetoothPermissions,
    /**
     * SDK尚未鉴权认证
     */
    errorUnAllowUse
    /**
     * 未知
     */
    errorUnknown
}

```

* DeviceSensorActivateListener 瞬感探头激活回调

```groovy

/**
 * 瞬感探头激活回调
 */
public interface DeviceSensorActivateListener {
    void onActivateStatus(boolean isActivated);

    void onFailure(String msg);
}

```
