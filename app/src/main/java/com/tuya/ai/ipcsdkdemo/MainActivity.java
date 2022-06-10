package com.tuya.ai.ipcsdkdemo;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tuya.ai.ipcsdkdemo.audio.FileAudioCapture;
import com.tuya.ai.ipcsdkdemo.video.VideoCapture;
import com.tuya.edge.atop.AtopFacade;
import com.tuya.edge.enums.QrcodeEnum;
import com.tuya.edge.init.EdgeNetConfigManager;
import com.tuya.edge.init.MediaParamConfigCallback;
import com.tuya.edge.model.vo.NetQrcodeVO;
import com.tuya.edge.utils.AESUtils;
import com.tuya.edge.utils.TOTPUtils;
import com.tuya.smart.aiipc.base.permission.PermissionUtil;
import com.tuya.smart.aiipc.ipc_sdk.api.Common;
import com.tuya.smart.aiipc.ipc_sdk.api.IDeviceManager;
import com.tuya.smart.aiipc.ipc_sdk.api.IMediaTransManager;
import com.tuya.smart.aiipc.ipc_sdk.api.IParamConfigManager;
import com.tuya.smart.aiipc.ipc_sdk.service.IPCServiceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IPC_DEMO";

    private SurfaceView surfaceView;

    private VideoCapture videoCapture;

    private FileAudioCapture fileAudioCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.call).setOnClickListener(v -> {
            IMediaTransManager mediaTransManager = IPCServiceManager.getInstance().getService(IPCServiceManager.IPCService.MEDIA_TRANS_SERVICE);

            try {
                InputStream fileStream = getAssets().open("donghua.jpg");

                byte[] buffer = new byte[2048];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                byte[] file = output.toByteArray();

                mediaTransManager.sendDoorBellCallForPress(file, Common.NOTIFICATION_CONTENT_TYPE_E.NOTIFICATION_CONTENT_JPEG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //上线前需要向涂鸦申请
                    String secret = "";

                    //三方设备id,根据实际设备id配置
                    String cid = "device164114432";
                    String basePath = getFilesDir().getPath() + "/";
                    String recordPath = getFilesDir().getPath() + "/";

                    //二维码信息Map
                    String t = "";
                    String a = "a1-cn.wgine.com";
                    String key = "";

                    //查询配网信息
                    NetQrcodeVO netQrcodeVO = AtopFacade.getInstance().queryQrcodeInfo(a, key, cid);
                    //对配网信息进行解密
                    String qrcodeInfo = AESUtils.decrypt(netQrcodeVO.getData(), secret);

                    //组装qrcodeMap;
                    Map<String, String> qrcodeMap = JSON.parseObject(qrcodeInfo, new TypeReference<HashMap<String, String>>() {
                    });
                    qrcodeMap.put(QrcodeEnum.TOKEN.getCode(), t);

                    Map<String, String> extendMap = new HashMap<>();
                    extendMap.put("ip", "127.0.0.1");
                    extendMap.put("install", "安装地址");
                    //设置扩展信息
                    qrcodeMap.put(QrcodeEnum.EXTEND_DATA.getCode(), JSON.toJSONString(extendMap));

                    //实现类的配置
                    Properties properties = new Properties();


                    //人员事件处理
                    properties.put("dc_userInfo", "com.tuya.ai.ipcsdkdemo.edge.TenementReceiveEventImpl");
                    //人脸事件处理
                    properties.put("dc_faceInfo", "com.tuya.ai.ipcsdkdemo.edge.FaceReceiveEventImpl");
                    //人卡事件处理
                    properties.put("dn_cardInfo","com.tuya.ai.ipcsdkdemo.edge.CardReceiveEventImpl");
                    //二维码事件处理
                    properties.put("dc_qrCodeInfo","com.tuya.ai.ipcsdkdemo.edge.QrCodeReceiveEventImpl");
                    //远程开门事件处理
                    properties.put("dc_door", "com.tuya.ai.ipcsdkdemo.edge.DoorReceiveEventImpl");
                    //密码通行事件处理
                    properties.put("dc_passPwd","com.tuya.ai.ipcsdkdemo.edge.PassPwdReceiveEventImpl");
                    //数据清理事件处理
                    properties.put("dc_dataClear", "com.tuya.ai.ipcsdkdemo.edge.DataClearEventImpl");
                    //时间同步事件处理
                    properties.put("dc_timeSync", "com.tuya.ai.ipcsdkdemo.edge.TimeSyncEventImpl");
                    //可视对讲业务方挂断事件处理
                    properties.put("dc_callRefuse", "com.tuya.ai.ipcsdkdemo.edge.CallRefuseEventImpl");

                    PermissionUtil.check(MainActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA
                    }, () -> initSDK(MainActivity.this, cid, qrcodeMap, basePath, recordPath, properties, "1", new MediaParamConfigCallback() {
                        public void initMediaParamConfig() {
                            LoadParamConfig();
                        }
                    }));
                } catch (Exception ex) {

                }
            }
        }).start();
    }

    /**
     * 扫二维码配网及设备重启时调用
     *
     * @param ctx                 上下文
     * @param cid                 三方设备id
     * @param qrcodeMap           二维码信息Map
     * @param basePath            可写的一个路径，用于存储SDK相关的配置
     * @param recordPath          可写的一个路径，用于存储录像
     * @param properties          实现类的配置
     * @param version             versionnum
     * @param paramConfigCallBack
     */
    private void initSDK(Context ctx, String cid, Map<String, String> qrcodeMap, String basePath, String recordPath,
                         Properties properties, String version, MediaParamConfigCallback paramConfigCallBack) {

        EdgeNetConfigManager.getInstance().initSDK(ctx, cid, qrcodeMap, basePath, recordPath, properties, version, paramConfigCallBack);

        runOnUiThread(() -> findViewById(R.id.call).setEnabled(true));

        //获取涂鸦设备id
        IDeviceManager deviceManager = (IDeviceManager) IPCServiceManager.getInstance().getService(IPCServiceManager.IPCService.DEVICE_SERVICE);
        String deviceId = deviceManager.getDeviceId();

        //视频流（相机）
        videoCapture = new VideoCapture(Common.ChannelIndex.E_CHANNEL_VIDEO_MAIN);
        videoCapture.startVideoCapture();

        //音频流（本地文件）
        fileAudioCapture = new FileAudioCapture(ctx);
        fileAudioCapture.startFileCapture();

        IMediaTransManager mediaTransManager = IPCServiceManager.getInstance().getService(IPCServiceManager.IPCService.MEDIA_TRANS_SERVICE);
        mediaTransManager.setDoorBellCallStatusCallback(status -> {
            /**
             * 门铃呼叫报警接听状态
             * status = -1 未知状态
             * status = 0 接听
             * status = 1 挂断
             * status = 2 通话中心跳
             * {@link Common.DoorBellCallStatus}
             * */
            Log.d(TAG, "doorbell back: " + status);

        });

    }

    public void edgeClick(View view)//按钮按下,执行atop请求
    {
        //二维码刷新
        //私钥
        String secretKey = "fa26477e064d884c2fc13120e9daa272";
        //二维码刷新时间
        Long refreshTime = 500000L;
        //二维码内容的加密部分
        String decryptData = "e/ARSVkAEddzvT6CwVNy1zFUAcCQW4yhvzOiVLzaHne5nAO6rhZnpsLsY+QsW/5KeVO1eoqbCCCQGM5z9JQf1w==";
        String qrcode = AESUtils.decryptForBase64(decryptData, secretKey);
        String[] qrcodeArray = qrcode.split("\\|");

        //验证二维码信息是否匹配
        boolean flag = TOTPUtils.verifyTOTPFlexibility(qrcodeArray[0], secretKey, qrcodeArray[1], refreshTime);
        System.out.println(flag);
    }

    /**
     * 音视频参数配置
     */
    private void LoadParamConfig() {
        IParamConfigManager configManager = IPCServiceManager.getInstance().getService(IPCServiceManager.IPCService.MEDIA_PARAM_SERVICE);

        configManager.setInt(Common.ChannelIndex.E_CHANNEL_VIDEO_MAIN, Common.ParamKey.KEY_VIDEO_WIDTH, 1280);
        configManager.setInt(Common.ChannelIndex.E_CHANNEL_VIDEO_MAIN, Common.ParamKey.KEY_VIDEO_HEIGHT, 720);
        configManager.setInt(Common.ChannelIndex.E_CHANNEL_VIDEO_MAIN, Common.ParamKey.KEY_VIDEO_FRAME_RATE, 24);
        configManager.setInt(Common.ChannelIndex.E_CHANNEL_VIDEO_MAIN, Common.ParamKey.KEY_VIDEO_I_FRAME_INTERVAL, 2);
        configManager.setInt(Common.ChannelIndex.E_CHANNEL_VIDEO_MAIN, Common.ParamKey.KEY_VIDEO_BIT_RATE, 1024000);

        configManager.setInt(Common.ChannelIndex.E_CHANNEL_AUDIO, Common.ParamKey.KEY_AUDIO_CHANNEL_NUM, 1);
        configManager.setInt(Common.ChannelIndex.E_CHANNEL_AUDIO, Common.ParamKey.KEY_AUDIO_SAMPLE_RATE, 8000);
        configManager.setInt(Common.ChannelIndex.E_CHANNEL_AUDIO, Common.ParamKey.KEY_AUDIO_SAMPLE_BIT, 16);
        configManager.setInt(Common.ChannelIndex.E_CHANNEL_AUDIO, Common.ParamKey.KEY_AUDIO_FRAME_RATE, 25);
    }
}
