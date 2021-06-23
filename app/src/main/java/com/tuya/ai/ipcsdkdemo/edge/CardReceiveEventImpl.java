package com.tuya.ai.ipcsdkdemo.edge;


import android.util.Log;

import com.tuya.edge.client.api.user.CardReceiveEvent;
import com.tuya.edge.client.api.user.TenementReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.user.CardRequest;
import com.tuya.edge.client.model.user.EnableCardRequest;
import com.tuya.edge.client.model.user.EnableTenementRequest;
import com.tuya.edge.client.model.user.RemoveCardRequest;
import com.tuya.edge.client.model.user.RemoveTenementRequest;
import com.tuya.edge.client.model.user.TenementRequest;
import com.tuya.edge.enums.TuyaConstants;

public class CardReceiveEventImpl implements CardReceiveEvent {

    @Override
    public BaseResult addCard(CardRequest cardRequest, EventContext eventContext) {
        return null;
    }

    @Override
    public BaseResult modifyCard(CardRequest cardRequest, EventContext eventContext) {
        return null;
    }

    @Override
    public BaseResult removeCard(RemoveCardRequest removeCardRequest, EventContext eventContext) {
        return null;
    }

    @Override
    public BaseResult enableCard(EnableCardRequest enableCardRequest, EventContext eventContext) {
        Log.i(TuyaConstants.TAG, enableCardRequest + "执行成功");
        return new BaseResult(true, "succ", "执行成功");
    }
}

