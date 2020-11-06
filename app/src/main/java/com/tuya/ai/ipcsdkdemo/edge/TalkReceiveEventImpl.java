package com.tuya.ai.ipcsdkdemo.edge;

import android.util.Log;

import com.tuya.edge.client.api.device.TalkReceiveEvent;
import com.tuya.edge.client.model.BaseResult;
import com.tuya.edge.client.model.EventContext;
import com.tuya.edge.client.model.device.talk.TalkRequest;
import com.tuya.edge.enums.TuyaConstants;

public class TalkReceiveEventImpl implements TalkReceiveEvent {

    @Override
    public BaseResult talkByAudio(TalkRequest talkRequest, EventContext context) {
        Log.i(TuyaConstants.TAG,talkRequest.getUid() + "执行成功");
        return new BaseResult(true,"succ","执行成功");
    }
}
