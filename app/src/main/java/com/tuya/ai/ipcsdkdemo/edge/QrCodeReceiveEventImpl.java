package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.tuya.edge.client.api.user.QcCodeReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.user.QrCodeRequest;
import com.tuya.edge.enums.TuyaConstants;

public class QrCodeReceiveEventImpl implements QcCodeReceiveEvent {



    @Override
    public BaseResult addQcCode(QrCodeRequest qrCodeRequest, EventContext eventContext) throws Exception {
        Log.i(TuyaConstants.TAG, qrCodeRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");    }

    @Override
    public BaseResult modifyQcCode(QrCodeRequest qrCodeRequest, EventContext eventContext) throws Exception {
        Log.i(TuyaConstants.TAG, qrCodeRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");    }

    @Override
    public BaseResult removeQcCode(QrCodeRequest qrCodeRequest, EventContext eventContext) throws Exception {
        Log.i(TuyaConstants.TAG, qrCodeRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");    }
}

