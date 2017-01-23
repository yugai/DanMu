package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.Message;
import com.yugai.danmu.socket.Connector;
import com.yugai.danmu.util.LogUtils;

/**
 * Created by yugai on 2016-04-09.
 */
public class FormatMessageHandler<T extends Message> extends CastMessageHandler<T> {
    public FormatMessageHandler(String type, Class<T> clz) {
        super(type, clz);
    }

    @Override
    protected void handleMessage0(Connector connector, T t) {
        if (t instanceof FormatMessage) {
            FormatMessage message = (FormatMessage) t;
//            LogUtils.printf(message.getMessage());
        } else {
            LogUtils.printf(t.toString());
        }
    }

    public  interface FormatMessage {
        String getMessage();
    }
}
