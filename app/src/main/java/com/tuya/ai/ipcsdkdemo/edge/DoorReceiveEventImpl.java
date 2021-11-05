package com.tuya.ai.ipcsdkdemo.edge;

import android.util.Log;

import com.tuya.edge.client.api.device.DoorReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.device.door.DoorRequest;
import com.tuya.edge.enums.TuyaConstants;

public class DoorReceiveEventImpl implements DoorReceiveEvent {

    @Override
    public BaseResult openDoor(DoorRequest doorRequest, EventContext context) {
        Log.i(TuyaConstants.TAG,doorRequest.getUid() + "执行成功");

        return new BaseResult(true,"succ","执行成功");
    }
}
