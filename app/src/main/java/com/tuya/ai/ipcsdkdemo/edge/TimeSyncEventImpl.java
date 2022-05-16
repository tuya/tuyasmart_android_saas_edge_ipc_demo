package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.tuya.edge.client.api.device.DataClearEvent;
import com.tuya.edge.client.api.device.TimeSyncEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.device.door.DoorDataClearRequest;
import com.tuya.edge.client.model.device.door.DoorTimeSyncRequest;
import com.tuya.edge.enums.TuyaConstants;

public class TimeSyncEventImpl implements TimeSyncEvent {


    @Override
    public BaseResult syncTime(DoorTimeSyncRequest doorTimeSyncRequest, EventContext eventContext) {
        Log.i(TuyaConstants.TAG, doorTimeSyncRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }
}

