package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.Message;
import com.yugai.danmu.socket.Connector;

/**
 * Created by yugai on 2016-4-8.
 */
public class ErrorMessageHandler extends TargetTypeMessageHandler {
    private boolean mCloseGroup;

    public ErrorMessageHandler(boolean closeGroup) {
        super("error");
        mCloseGroup = closeGroup;
    }

    @Override
    public void handleMessage(Connector connector, Message message) {
        try {
            connector.close(mCloseGroup);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
