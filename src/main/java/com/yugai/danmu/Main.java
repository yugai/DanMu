package com.yugai.danmu;

import com.yugai.danmu.message.ChatMsg;
import com.yugai.danmu.message.handler.*;
import com.yugai.danmu.socket.Connector;
import com.yugai.danmu.util.LogUtils;

/**
 * Created by yugai on 2016-4-7.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        final DouYu douYu = new DouYu("woshiu");
        douYu.addMessageHandler(new ChatMsgHandler() {
                    @Override
                    protected void handleMessage0(Connector connector, ChatMsg chatMsg) {
                        super.handleMessage0(connector, chatMsg);
                        LogUtils.printf(chatMsg.getMessage());
                    }
                })
                .addMessageHandler(new GiftHandler())
                .addMessageHandler(new BlackResHandler())
                .addMessageHandler(new RichManInRoomHandler())
                .start();


    }
}
