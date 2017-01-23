package com.yugai.danmu.codec;

import com.yugai.danmu.util.EmptyUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by yugai on 2016-04-05.
 */
public class DanMuChannelInitializer extends ChannelInitializer<Channel> {
    private String mHeartTxt;
    private String[] mMessages;
    private MessageHandlerAdapter.SocketMessageHandler mHandler;

    public DanMuChannelInitializer(MessageHandlerAdapter.SocketMessageHandler handler,
                                   String heartTxt,
                                   String... messages) {
        mHeartTxt = heartTxt;
        mMessages = messages;
        mHandler = handler;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 以 16 进制输出接收内容，用于测试。
        // pipeline.addLast(new PrintReadHandler());

        // in
        // byte -> String
        pipeline.addLast(new Decoder());
        // Handle Message
        if (mHandler != null) {
            MessageHandlerAdapter messageHandlerAdapter = new MessageHandlerAdapter();
            messageHandlerAdapter.addMessageReadListener(mHandler);
            pipeline.addLast(messageHandlerAdapter);
        }

        // out
        // String -> byte
        pipeline.addLast(new Encoder());
        // heart
        if (!EmptyUtils.isEmpty(mHeartTxt)) {
            pipeline.addLast(new IdleStateHandler(45, 45, 45));
            pipeline.addLast(new HeartHandler(mHeartTxt));
        }
        // send message
        if (!EmptyUtils.isEmpty(mMessages)) {
            pipeline.addLast(new ChannelActiveHandler(mMessages));
        }
    }

}
