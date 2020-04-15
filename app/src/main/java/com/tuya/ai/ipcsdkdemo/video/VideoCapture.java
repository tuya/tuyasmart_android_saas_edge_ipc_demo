package com.tuya.ai.ipcsdkdemo.video;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

public class VideoCapture {

    private Camera mCamera;
    private byte[] pixelBuffer;
    VideoCodec mCodec;
    private SurfaceTexture mSurfaceTexture;

    public VideoCapture(int channel) {

        pixelBuffer = new byte[1280 * 720 * 3 / 2];
        mCodec = new VideoCodec(channel);

    }
    public void startVideoCapture() {
        startPreview();
        mCodec.startCodec();
    }

    private void  openCamera() {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    private void startPreview() {
//        openCamera();
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        Camera.Parameters p = mCamera.getParameters();
        //根据自己的设置更改
        p.setPreviewFormat(ImageFormat.NV21);
//        p.setPreviewFormat(ImageFormat.YV12);
        p.setPreviewFpsRange(30000, 30000);
        p.setPreviewSize(1280, 720);

        mCamera.setParameters(p);
        mSurfaceTexture = new SurfaceTexture(0);

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

            mCamera.addCallbackBuffer(pixelBuffer);
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Log.d("Video", "onPreviewFrame: ");
                //编码
                byte[] pixelData = new byte[1280 * 720 * 3 / 2];
                System.arraycopy(data, 0, pixelData, 0, data.length);
                mCodec.encodeH264(pixelData);
                Log.d("Video", "onPreviewFrame: done");
                camera.addCallbackBuffer(pixelBuffer);
            }
        });
        mCamera.startPreview();
    }
}
