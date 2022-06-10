package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tuya.edge.client.api.device.CallRefuseEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.device.door.DoorCallRefuseRequest;
import com.tuya.edge.enums.TuyaConstants;

public class CallRefuseEventImpl implements CallRefuseEvent {


    @Override
    public BaseResult callRefuse(DoorCallRefuseRequest callRefuseRequest, EventContext context) {
        Log.i(TuyaConstants.TAG, JSON.toJSONString(callRefuseRequest) + "挂断成功");
        return new BaseResult(true, "succ", "执行成功");
    }
}

