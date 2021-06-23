package com.tuya.ai.ipcsdkdemo.edge;

import android.util.Log;

import com.tuya.edge.client.api.device.CustomReceiveEvent;
import com.tuya.edge.client.api.device.SecretReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.device.custom.CustomRequest;
import com.tuya.edge.client.model.device.secret.SecretRequest;
import com.tuya.edge.enums.TuyaConstants;


public class SecretReceiveEventImpl implements SecretReceiveEvent {

    @Override
    public BaseResult acceptSecret(SecretRequest secretRequest, EventContext context) {
        Log.i(TuyaConstants.TAG, secretRequest.getSecret() + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }
}
