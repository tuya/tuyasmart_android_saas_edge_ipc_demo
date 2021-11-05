package com.tuya.ai.ipcsdkdemo.video;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tuya.smart.aiipc.ipc_sdk.api.Common;
import com.tuya.smart.aiipc.ipc_sdk.api.IMediaTransManager;
import com.tuya.smart.aiipc.ipc_sdk.api.IParamConfigManager;
import com.tuya.smart.aiipc.ipc_sdk.service.IPCServiceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.media.MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
import static android.media.MediaCodec.BUFFER_FLAG_KEY_FRAME;

public class VideoCodec {

    private MediaCodec mCodec;
    private MediaFormat mediaFormat;
    private static final int MAX_BUF_SIZE = 25;

    private Handler handler;

    private long startTime = System.nanoTime();

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private byte[] SPS;

    private byte[] PPS;

    IMediaTransManager transManager;
    IParamConfigManager configManager;

    private boolean isRunning = false;

    private byte[] lock = new byte[0];

    private Executor executor = Executors.newSingleThreadExecutor();
    
    private int mChannel;

    private FPSCounter fpsCounter = new FPSCounter("VideoCodec");

    @TargetApi(21)
    public VideoCodec(int channel) {
        
        mChannel = channel;

        transManager = IPCServiceManager.getInstance().getService(IPCServiceManager.IPCService.MEDIA_TRANS_SERVICE);
        configManager = IPCServiceManager.getInstance().getService(IPCServiceManager.IPCService.MEDIA_PARAM_SERVICE);

        if (Build.VERSION_CODES.KITKAT_WATCH < Build.VERSION.SDK_INT) {

            callback = new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    byte[] data = cameraDatas.poll();
                    if (data == null) {
                        handler.post(() -> callback.onInputBufferAvailable(codec, index));
                        return;
                    }
                    ConvertToNV12(data);
                    ByteBuffer buffer = codec.getInputBuffer(index);
                    if (buffer == null) {
                        return;
                    }
                    buffer.put(data);
                    buffer.clear();
                    codec.queueInputBuffer(index, 0, data.length, computePresentationTime(), 0);

                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    ByteBuffer buffer = codec.getOutputBuffer(index);
                    if (buffer == null) {
                        return;
                    }
                    byte[] bytes = new byte[info.size];
                    buffer.get(bytes);
                    int type = Common.NAL_TYPE.NAL_TYPE_PB;
                    codec.releaseOutputBuffer(index, false);
                    if ((info.flags & BUFFER_FLAG_KEY_FRAME) != 0) {
                        outputStream.reset();
                        try {
                            outputStream.write(SPS);
                            outputStream.write(PPS);
                            outputStream.write(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = outputStream.toByteArray();
                        type = Common.NAL_TYPE.NAL_TYPE_IDR;
                    }
                    transManager.pushMediaStream(mChannel, type, bytes);

//                    fpsCounter.logFrame();
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                    MediaFormat outputFormat = codec.getOutputFormat();
                    SPS = outputFormat.getByteBuffer("csd-0").array();
                    PPS = outputFormat.getByteBuffer("csd-1").array();
                }
            };
        }
        init();

    }

    private void init() {
        loadConfig();
        HandlerThread handlerThread = new HandlerThread("VideoCodec");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private void loadConfig() {
        mediaFormat = MediaFormat.createVideoFormat("video/avc",  configManager.getInt(mChannel, Common.ParamKey.KEY_VIDEO_WIDTH), configManager.getInt(mChannel, Common.ParamKey.KEY_VIDEO_HEIGHT));
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, configManager.getInt(mChannel, Common.ParamKey.KEY_VIDEO_I_FRAME_INTERVAL));
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, configManager.getInt(mChannel, Common.ParamKey.KEY_VIDEO_FRAME_RATE));
        //根据自己的设备来选择需要的格式
        if (Build.VERSION_CODES.KITKAT_WATCH < Build.VERSION.SDK_INT){
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        }else {
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        }
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, configManager.getInt(mChannel, Common.ParamKey.KEY_VIDEO_BIT_RATE));
    }

    private ArrayBlockingQueue<byte[]> cameraDatas = new ArrayBlockingQueue<>(MAX_BUF_SIZE);

    /**
     * H264编码
     */
    @TargetApi(21)
    private MediaCodec.Callback callback;

    private void check(byte[] sps, byte[] pps) {
        if (!Arrays.equals(sps, SPS)) {
            SPS = sps;
        }
        if (!Arrays.equals(pps, PPS)) {
            PPS = pps;
        }
    }

    @TargetApi(19)
    private void startSyncEncode() {
        isRunning = true;
        executor.execute(() -> {
            while (isRunning) {
                byte[] data = cameraDatas.poll();
                if (data == null) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    continue;
                }
                ConvertToNV12(data);
                ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
                ByteBuffer[] outputBuffers = mCodec.getOutputBuffers();
                int inputBufferIndex = mCodec.dequeueInputBuffer(-1);
                Log.d("Decoder", "inputBufferIndex:  " + inputBufferIndex);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                    inputBuffer.clear();
                    inputBuffer.put(data, 0, data.length);
                    mCodec.queueInputBuffer(inputBufferIndex, 0, data.length, computePresentationTime(), 0);
                }
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int outputBufferIndex = mCodec.dequeueOutputBuffer(info, 0);
                while (outputBufferIndex >= 0) {
                    byte[] bytes = new byte[info.size];
                    outputBuffers[outputBufferIndex].get(bytes, 0, bytes.length);
                    int type = Common.NAL_TYPE.NAL_TYPE_PB;
                    mCodec.releaseOutputBuffer(outputBufferIndex, false);
                    if ((info.flags & BUFFER_FLAG_KEY_FRAME) != 0) {
                        type = Common.NAL_TYPE.NAL_TYPE_IDR;
                        outputStream.reset();
                        try {
                            if (SPS != null) {
                                outputStream.write(SPS);
                            }
                            if (PPS != null) {
                                outputStream.write(PPS);
                            }
                            outputStream.write(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = outputStream.toByteArray();
                    }
                    if ((info.flags & BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        MediaFormat outputFormat = mCodec.getOutputFormat();
                        byte[] sps = outputFormat.getByteBuffer("csd-0").array();
                        byte[] pps = outputFormat.getByteBuffer("csd-1").array();
                        check(sps, pps);
                    } else {
                        transManager.pushMediaStream(mChannel, type, bytes);
                    }
                    outputBufferIndex = mCodec.dequeueOutputBuffer(info, 0);
                }
            }
        });
    }

    public void startCodec() {
        handler.post(() -> {
            try {
                mCodec = MediaCodec.createEncoderByType("video/avc");
                mCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    mCodec.setCallback(callback);
                    mCodec.start();
                } else {
                    mCodec.start();
                    startSyncEncode();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private long computePresentationTime() {
        return (System.nanoTime() - startTime) / 1000;
    }

    public void encodeH264(byte[] pixelData) {
        if (cameraDatas.size() >= MAX_BUF_SIZE - 1) {
            cameraDatas.poll();
//            Log.d("Codec", "drop frame: ");
        }
        Log.d("Codec", "encodeH264: ");
        this.cameraDatas.add(pixelData);
        Log.d("Codec", "encodeH264: " + this.cameraDatas.size());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && this.cameraDatas.size() == 1) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    private void ConvertToNV12(byte[] data) {
        //NV21 to NV12
        byte tmp;
        int offset = 1280 * 720;
        for (int i = offset; i < offset * 3 / 2; i += 2) {
            tmp = data[i];
            data[i] = data[i + 1];
            data[i + 1] = tmp;
        }
//
//         YV12 to NV12
//        int nLenY = 1280 * 720;
//        int nLenU = nLenY / 4;
//        byte[] tmp = new byte[nLenU*2];
//        for (int i = 0; i < nLenU; i++) {
//            tmp[2 * i] = data[nLenY + nLenU + i];
//            tmp[2 * i + 1] = data[nLenY + i];
//        }
//        System.arraycopy(tmp, 0, data, nLenY, nLenU*2);
    }
}
