[English](README.md) | [中文版](README_zh.md)

The `tuyasmart_android_saas_edge_ipc_demo` provides the demo for integration with third-party access devices that have built-in Tuya IPC SDK. Device manufacturers can follow instructions in this demo to access the Tuya IoT Cloud based on saas-sdk.

# Architecture

![Process](https://airtake-public-data-1254153901.cos.ap-shanghai.myqcloud.com/content-platform/hestia/16363757485b94a44bbac.png)

This topic only supports integration with third-party access control devices that have built-in Tuya IPC SDK.

# DPs (Data Points)

| No.  | DP                                                           | Description                                                  |
| ---- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1    | Device pairing                                               | Third-party devices must be paired before they can receive commands from the cloud and interact with the cloud. |
| 2    | Video call                                                   | Initiate, receive, and hang up calls.                        |
| 3    | Manage residents                                             | Add, update, remove, enable, and disable residents.          |
| 4    | Manage biometric authentication                              | Add, update, and remove residents.                           |
| 5    | Manage access control cards.                                 | Add, update, and remove access control cards.                |
| 6    | Manage QR codes                                              | Add, update, and remove QR codes.                            |
| 7    | Remote unlocking                                             | Use the Smart Life app to send unlocking commands to the access control device. The device unlocks the door and reports the access control record after receiving the command. |
| 8    | Report events                                                | The access control device recognizes people who pass by and reports the access control record. |
| 9    | Grade biometric photos                                       | The server grades the captured biometric photos. This feature is used only for check-in at hotels. |
| 10   | Dynamically refresh the QR code for residents' access control | In addition to the access control QR code from the cloud, a dynamically refreshable access control QR code is also supported. |
| 11   | Manage access control passwords                              | Add, update, and remove access control passwords.            |

# Integration process

## Configure dependencies

```java
  implementation 'com.squareup.okhttp3:okhttp:3.14.0'
  implementation 'org.apache.commons:commons-lang3:3.3.2'
```

## Download Jizhi Community app

Download the Jizhi Community app in an app store to establish audio and video calls with the access control device through the app.

## Integrate third-party device with Tuya EDGE IPC SDK

Tuya EDGE IPC SDK and demo address:

* https://github.com/TuyaInc/tuyasmart_android_saas_edge_ipc_demo

* EDGE IPC SDK is developed based on Tuya IPC SDK. For more information, see:

* https://tuyainc.github.io/tuyasmart_android_device_sdk_doc

## Pair device and initialize configuration

### Request a private key

Request a private key from Tuya to decrypt pairing information.

### Scan the QR code on the device

Address of QR code: https://community.console.tuya.com/gateway/device. Request the testing account from Tuya.

The device scans the QR code on the construction app to pair the device. The QR code includes content in the following format:

```json
{
    "t":"AYRfQAw5nmpr_0",
    "a":"a1-cn.wgine.com",
    "key":"1168813909092601857"
}
```

After the QR code is generated, it will expire in 10 minutes.

The device calls API operations by using `a`, `key`, and `cid` to get and decrypt the QR code content. The decrypted result is converted into `qrcodeMap`.

```java
// Returns pairing information.
NetQrcodeVO netQrcodeVO = AtopFacade.getInstance().queryQrcodeInfo(a, key, cid);
// Decrypts pairing information.
String qrcodeInfo = AESUtils.decrypt(netQrcodeVO.getData(), secret);

// Creates qrcodeMap.
Map<String, String> qrcodeMap = JSON.parseObject(qrcodeInfo, new TypeReference<HashMap<String, String>>() {});
qrcodeMap.put(QrcodeEnum.TOKEN.getCode(), t);
```



### Initialize the SDK

```java
    /**
     * Scans the QR code for pairing and restarts the device
     *
     * @param ctx                     The system context.
     * @param cid                     The third-party device ID. This is a unique value for each device.
     * @param qrcodeMap              The map of the QR code information as described in Section 1.3.2.
     * @param basePath                The writable path that stores SDK configurations. The app storage directory is recommended.
     * @param recordPath              The writable path that stores video footage. The SD card is recommended.
     * @param properties              The settings of the implementation class.
     * @param paramConfigCallBack     The callback of audio and video parameters.
     */
    public void initSDK(Context ctx, String cid, Map<String,String> qrcodeMap,
          String basePath, String recordPath, Properties properties,
          MediaParamConfigCallback paramConfigCallBack)
```

For more information about the key of `qrcodeMap`, see the code of `QrcodeEnum`. `t`, `pId`, `uuid`, and `authKey` must be written to the system after pairing. This way, after the device is restarted, powered off, or restored with default settings, it can automatically reconnect to the Tuya IoT Cloud.

Check whether the device is paired:

```java
 SharedPreferences sp = ctx.getSharedPreferences("edge_config",Context.MODE_PRIVATE);
 boolean isBind = sp.getBoolean("bind_status",false);
```

If `isBind = true`, the device is paired.

### Initialize the implementation class of the access control API

The following example shows the key of `properties`. `value` is implemented by the third party in the API of Directory 6.

```java
Properties properties = new Properties();
// The resident data synchronization class.
properties.put("dc_userInfo","com.tuya.ai.ipcsdkdemo.edge.TenementReceiveEventImpl");
// Synchronizes biometric data.
properties.put("dc_faceInfo","com.tuya.ai.ipcsdkdemo.edge.FaceImageReceiveEventImpl");
// Synchronizes card data.
properties.put("dn_cardInfo","com.tuya.ai.ipcsdkdemo.edge.CardReceiveEventImpl");
// Synchronizes QR code data.
properties.put("dc_qrCodeInfo","com.tuya.ai.ipcsdkdemo.edge.QcCodeReceiveEventImpl");
// Unlocks the door.
properties.put("dc_door","com.tuya.ai.ipcsdkdemo.edge.DoorReceiveEventImpl");
```

## Implement APIs

The IPC SDK has encapsulated the commands sent by the Tuya IoT Cloud into APIs. The third party only needs to implement specific APIs. For more information about the APIs, see Directory 6.
