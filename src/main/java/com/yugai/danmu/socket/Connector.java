package com.yugai.danmu.socket;

import com.yugai.danmu.codec.DanMuChannelInitializer;
import com.yugai.danmu.codec.MessageHandlerAdapter;
import com.yugai.danmu.message.Message;
import com.yugai.danmu.util.EmptyUtils;
import com.yugai.danmu.util.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by yugai on 2016-4-7.
 */
public class Connector {
    private EventLoopGroup mGroup;
    private Bootstrap mBootstrap;
    private ChannelFuture mFuture;

    private String mHeartText;
    private String[] mMessages;
    private CastSocketMessageHandler mHandler = new CastSocketMessageHandler(this);

    public Connector() {
        this(new NioEventLoopGroup());
    }

    public Connector(EventLoopGroup group) {
        mGroup = group;
        mBootstrap = new Bootstrap();
        mBootstrap.group(mGroup)
                  .channel(NioSocketChannel.class);
    }

    public Connector remoteAddress(SocketAddress address) {
        mBootstrap.remoteAddress(address);
        return this;
    }

    public Connector heartTxt(String heartTxt) {
        mHeartText = heartTxt;
        return this;
    }

    public Connector messages(String... messages) {
        mMessages = messages;
        return this;
    }

    public void connect() throws ConnectException,InterruptedException {
        if (mFuture != null && mFuture.isCancelled()) {
            return;
        }

        mBootstrap.handler(new DanMuChannelInitializer(mHandler, mHeartText, mMessages));
        mFuture = mBootstrap.connect().sync();
        mFuture.channel().closeFuture().sync();
    }

    public Connector write(final String msg) {
        ChannelFuture channelFuture = mFuture.channel().writeAndFlush(msg);
        channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LogUtils.printf(String.format("[%1$s] 输出失败，Socket 关闭。", msg));
                    future.channel().close();
                }
            }
        });
        return this;
    }

    public void close(boolean closeGroup) throws InterruptedException {
        mFuture.channel().close();
        if (closeGroup) {
            closeGroup();
        }
    }

    private void closeGroup() throws InterruptedException {
        mGroup.shutdownGracefully().sync();
    }

    public Connector addMessageHandler(MessageHandler handler) {
        mHandler.addMessageHandler(handler);
        return this;
    }

    public Connector removeMessageHandler(MessageHandler handler) {
        mHandler.removeMessageHandler(handler);
        return this;
    }

    public Connector removeMessageHandler(String type) {
        mHandler.removeMessageHandler(type);
        return this;
    }

    private static class CastSocketMessageHandler implements MessageHandlerAdapter.SocketMessageHandler {

        private TreeMap<String, List<MessageHandler>> group = new TreeMap<>();
        private List<MessageHandler> mListenerList = new LinkedList<>();
        private Connector mConnector;

        public CastSocketMessageHandler(Connector connector) {
            mConnector = connector;
        }

        @Override
        public void handleMessage(ChannelHandlerContext ctx, String msg) {
            Message message = new Message(msg);
            ctx.executor().execute(new HandleMessageRunnable(group, mListenerList, mConnector, message));
        }

        public void addMessageHandler(MessageHandler handler) {
            if (handler == null) {
                return;
            }
            String type = handler.getType();
            if (EmptyUtils.isEmpty(type)) {
                mListenerList.add(handler);
            } else {
                List<MessageHandler> list = group.get(type);
                if (list == null) {
                    list = new LinkedList<>();
                    group.put(type, list);
                }
                list.add(handler);
            }
        }

        public void removeMessageHandler(MessageHandler handler) {
            if (handler == null) {
                return;
            }
            String type = handler.getType();
            if (EmptyUtils.isEmpty(type)) {
                mListenerList.remove(handler);
            } else {
                List<MessageHandler> list = group.get(type);
                if (list != null) {
                    list.remove(handler);
                }
            }
        }

        public void removeMessageHandler(String type) {
            if (EmptyUtils.isEmpty(type)) {
                mListenerList.clear();
            } else {
                group.remove(type);
            }
        }

        private static class HandleMessageRunnable implements Runnable {
            private TreeMap<String, List<MessageHandler>> mGroup;
            private List<MessageHandler> mListenerList;
            private Connector mConnector;
            private Message mMessage;

            public HandleMessageRunnable(TreeMap<String, List<MessageHandler>> group,
                                         List<MessageHandler> listenerList,
                                         Connector connector, Message message) {
                mGroup = group;
                mListenerList = listenerList;
                mConnector = connector;
                mMessage = message;
            }

            @Override
            public void run() {
                if (!EmptyUtils.isEmpty(mListenerList)) {
                    for (MessageHandler listener : mListenerList) {
                        listener.handleMessage(mConnector, mMessage);
                    }
                }

                List<MessageHandler> list = mGroup.get(mMessage.getType());
                if (!EmptyUtils.isEmpty(list)) {
                    for (MessageHandler listener : list) {
                        listener.handleMessage(mConnector, mMessage);
                    }
                }
            }
        }
    }

}
