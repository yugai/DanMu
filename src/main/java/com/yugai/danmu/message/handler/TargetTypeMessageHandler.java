package com.yugai.danmu.message.handler;

import com.yugai.danmu.socket.MessageHandler;

/**
 * Created by yugai on 2016-4-8.
 */
public abstract class TargetTypeMessageHandler implements MessageHandler {
    private String mType;

    public TargetTypeMessageHandler(String type) {
        mType = type;
    }

    @Override
    public String getType() {
        return mType;
    }
}
