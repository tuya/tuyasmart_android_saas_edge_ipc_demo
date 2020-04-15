package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.tuya.edge.client.api.user.TenementReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.user.EnableTenementRequest;
import com.tuya.edge.client.model.user.RemoveTenementRequest;
import com.tuya.edge.client.model.user.TenementRequest;
import com.tuya.edge.enums.TuyaConstants;

public class TenementReceiveEventImpl implements TenementReceiveEvent {

    @Override
    public BaseResult addTenement(TenementRequest tenementRequest, EventContext context) {
        return null;
    }

    @Override
    public BaseResult modifyTenement(TenementRequest tenementRequest, EventContext context) {
        Log.i(TuyaConstants.TAG,tenementRequest.getUid() + "执行成功");
        return new BaseResult(true,"succ","执行成功");
    }

    @Override
    public BaseResult removeTenement(RemoveTenementRequest removeTenementRequest, EventContext context) {
        return null;
    }

    @Override
    public BaseResult enableTenement(EnableTenementRequest enableTenementRequest, EventContext context) {
        return null;
    }
}

