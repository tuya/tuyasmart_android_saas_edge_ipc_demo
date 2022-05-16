package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.tuya.edge.client.api.user.PassPwdReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.user.PassPwdRequest;
import com.tuya.edge.enums.TuyaConstants;

public class PassPwdReceiveEventImpl implements PassPwdReceiveEvent {


    @Override
    public BaseResult addPassPwd(PassPwdRequest passPwdRequest, EventContext eventContext) {
        Log.i(TuyaConstants.TAG, passPwdRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }

    @Override
    public BaseResult modifyPassPwd(PassPwdRequest passPwdRequest, EventContext eventContext) {
        Log.i(TuyaConstants.TAG, passPwdRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }

    @Override
    public BaseResult removePassPwd(PassPwdRequest passPwdRequest, EventContext eventContext) {
        Log.i(TuyaConstants.TAG, passPwdRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }
}

