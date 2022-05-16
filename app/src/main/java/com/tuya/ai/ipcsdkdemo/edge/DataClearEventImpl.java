package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.tuya.edge.client.api.device.DataClearEvent;
import com.tuya.edge.client.api.user.CardReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.device.door.DoorDataClearRequest;
import com.tuya.edge.client.model.user.CardRequest;
import com.tuya.edge.client.model.user.EnableCardRequest;
import com.tuya.edge.client.model.user.RemoveCardRequest;
import com.tuya.edge.enums.TuyaConstants;

public class DataClearEventImpl implements DataClearEvent {

    @Override
    public BaseResult clearData(DoorDataClearRequest doorDataClearRequest, EventContext eventContext) {
        Log.i(TuyaConstants.TAG, doorDataClearRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }
}

