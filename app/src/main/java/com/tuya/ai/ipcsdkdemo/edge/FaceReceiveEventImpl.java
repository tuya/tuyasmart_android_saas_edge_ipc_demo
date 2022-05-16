package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.tuya.edge.client.api.user.FaceImageReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.user.FaceImageRequest;
import com.tuya.edge.client.model.user.RemoveFaceImageRequest;
import com.tuya.edge.enums.TuyaConstants;

public class FaceReceiveEventImpl implements FaceImageReceiveEvent {


    @Override
    public BaseResult addFaceImage(FaceImageRequest faceImageRequest, EventContext eventContext) throws Exception {
        Log.i(TuyaConstants.TAG, faceImageRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");

    }

    @Override
    public BaseResult modifyFaceImage(FaceImageRequest faceImageRequest, EventContext eventContext) throws Exception {
        Log.i(TuyaConstants.TAG, faceImageRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }

    @Override
    public BaseResult removeFaceImage(RemoveFaceImageRequest removeFaceImageRequest, EventContext eventContext) throws Exception {
        Log.i(TuyaConstants.TAG, removeFaceImageRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }
}

