package com.yugai.danmu.message;

import com.yugai.danmu.message.handler.FormatMessageHandler;

/**
 * Created by yugai on 2016-04-08.
 */
public class RichManInRoom extends Message implements FormatMessageHandler.FormatMessage {
    private String nn;

    public RichManInRoom(Message message) {
        super(message);
        nn = list.get("nn");
    }

    @Override
    public String toString() {
        return "RichManInRoom{" +
               "nn='" + nn + '\'' +
               '}';
    }

    @Override
    public String getMessage() {
        return String.format("欢迎土豪 [%1$s] 进入房间。", nn);
    }
}
