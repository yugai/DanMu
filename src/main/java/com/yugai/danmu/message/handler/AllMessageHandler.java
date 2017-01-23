package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.Message;
import com.yugai.danmu.socket.Connector;
import com.yugai.danmu.util.LogUtils;

/**
 * Created by yugai on 2016-4-8.
 */
public class AllMessageHandler extends TargetTypeMessageHandler {
    public AllMessageHandler() {
        super(null);
    }

    @Override
    public void handleMessage(Connector connector, Message message) {
        LogUtils.printf(message.getMsg());
    }
}
