package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.ChatMsg;

/**
 * Created by yugai on 2016-4-6.
 */
public class ChatMsgHandler extends FormatMessageHandler<ChatMsg> {
    public ChatMsgHandler() {
        super("chatmsg", ChatMsg.class);
    }
}
