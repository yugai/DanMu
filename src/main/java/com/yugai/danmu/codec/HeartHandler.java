package com.yugai.danmu.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by yugai on 2016-4-6.
 */
public class HeartHandler extends ChannelInboundHandlerAdapter {
    private final String mHeartStr;

    public HeartHandler(String heartStr) {
        mHeartStr = heartStr;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            if (state == IdleState.WRITER_IDLE || state == IdleState.ALL_IDLE) {
                ctx.writeAndFlush(mHeartStr);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
