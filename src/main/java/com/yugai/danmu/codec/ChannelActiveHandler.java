package com.yugai.danmu.codec;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by yugai on 2016-4-7.
 */
public class ChannelActiveHandler extends ChannelInboundHandlerAdapter {
    private final String[] mMessages;

    public ChannelActiveHandler(String... messages) {
        mMessages = messages;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        for (String message : mMessages) {
            ChannelFuture channelFuture = ctx.writeAndFlush(message);
            channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }
}
