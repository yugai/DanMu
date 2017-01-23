package com.yugai.danmu.socket;

import com.yugai.danmu.message.Message;

/**
 * Created by yugai on 2016-4-7.
 */
public interface MessageHandler {
    String getType();

    void handleMessage(Connector connector, Message message);
}
