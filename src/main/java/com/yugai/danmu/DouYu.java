package com.yugai.danmu;

import com.yugai.danmu.message.*;
import com.yugai.danmu.message.handler.CastMessageHandler;
import com.yugai.danmu.message.handler.ErrorMessageHandler;
import com.yugai.danmu.socket.Connector;
import com.yugai.danmu.socket.MessageHandler;
import com.yugai.danmu.util.*;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yugai on 2016-4-8.
 */
public class DouYu {
    private static final int SEA_DANMAKU_GID = -9999;
    private EventLoopGroup mGroup = new NioEventLoopGroup();
    private Connector mDanMuConnector;
    private Connector mConnector;
    private String mUrl;
    private String mRoomId;
    private String mDanMuHost;
    private int mDanMuPort;
    /** 弹幕组。如果设置为 -9999 ，则说明开启海量弹幕模式。 */
    private String mGId;
    private String mUserName;
    /** 标记是否进入海量弹幕模式。 */
    private boolean mSea;

    private List<MessageHandler> mHandlers = new LinkedList<>();

    public DouYu addMessageHandler(MessageHandler handler) {

        mHandlers.add(handler);
        return this;
    }
    public DouYu(String roomId){
        mUrl="https://www.douyu.com/"+roomId;
    }

    public DouYu sea(boolean enable) {
        mSea = enable;
        return this;
    }

    public void start() {
        LogUtils.printf("获取房间页面 ...");
        String pageHtml = HttpUtil.get(mUrl);
        //获取roomId
        LogUtils.printf("获取直播房间 ID ...");
        mRoomId = ResponseParser.parseRoomId(pageHtml);
        LogUtils.printf("Room ID = " + mRoomId);

        //检查是否在线
        boolean online = ResponseParser.parseOnline(pageHtml);
        if (!online) {
            LogUtils.printf("该房间还没有直播！" + mUrl);
            return;
        }

        //获取服务器IP列表
        LogUtils.printf("获取服务器列表 ...");
        List<InetSocketAddress> list = ResponseParser.parseServerInfo(pageHtml);
        if (EmptyUtils.isEmpty(list)) {
            LogUtils.printf("获取服务器列表失败！");
            return;
        }

        InetSocketAddress address = list.get(0);
        String host = address.getHostString();
        int port = address.getPort();
        LogUtils.printf(String.format("使用服务器 [%1$s:%2$d] 。", host, port));
        start(address, mRoomId);
        startDanMu();
    }

    private void start(InetSocketAddress address, final String roomId) {
        mConnector = new Connector(mGroup);

        String rt = String.valueOf(System.currentTimeMillis() / 1000);
        String devId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String vk = MD5Utils.MD5(rt + "7oE9nPEG9xXV69phU31FYCLUagKeYtsF" + devId);
        String loginMessage = String.format("type@=loginreq/username@=/ct@=0/password@=/roomid@=%1$s/devid@=%2$s/rt@=%3$s/vk@=%4$s/ver@=20150929/ltkid@=/biz@=/stk@=/",
                                            roomId,
                                            devId,
                                            rt,
                                            vk);

        mConnector.messages(loginMessage)
                  .remoteAddress(address)
                  .addMessageHandler(new CastMessageHandler<LoginRes1>("loginres", LoginRes1.class) {
                      @Override
                      protected void handleMessage0(Connector connector, LoginRes1 loginRes1) {
                          mUserName = loginRes1.getUserName();
                          LogUtils.printf("username = " + mUserName);
                          String gidMessage = String.format("type@=qrl/rid@=%1$s/et@=0/", roomId);
                          connector.write(gidMessage);
                          connector.removeMessageHandler(this);
                      }
                  })
                  .addMessageHandler(new CastMessageHandler<SetMsgGroup>("setmsggroup", SetMsgGroup.class) {
                      @Override
                      protected void handleMessage0(Connector connector, SetMsgGroup setMsgGroup) {
                          mGId = setMsgGroup.getGid();
                          LogUtils.printf("gid = " + mGId);
                          connector.removeMessageHandler(this);
                          closeConnector();
                      }
                  })
                  .addMessageHandler(new CastMessageHandler<MsgRepeaterList>("msgrepeaterlist", MsgRepeaterList.class) {
                      @Override
                      protected void handleMessage0(Connector connector, MsgRepeaterList msgRepeaterList) {
                          ArrayList<MsgRepeater> msgRepeaterArrayList = msgRepeaterList.getMsgRepeaterArrayList();
                          if (!EmptyUtils.isEmpty(msgRepeaterArrayList)) {
                              MsgRepeater repeater = msgRepeaterArrayList.get(0);
                              LogUtils.printf(repeater);
                              mDanMuPort = repeater.getPort();
                              mDanMuHost = repeater.getIp();
                              connector.removeMessageHandler(this);
                              closeConnector();
                          }
                      }
                  })
                  .addMessageHandler(new ErrorMessageHandler(false));
        try {
            mConnector.connect();
        } catch (ConnectException e) {
            LogUtils.printf(String.format("连接服务器 [%1$s:%2$d] 失败。", address.getHostString(), address.getPort()));
            closeConnector(mConnector, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startDanMu() {
        if (check()) {
            return;
        }

        LogUtils.printf(String.format("连接弹幕服务器 [%1$s:%2$d] 。", mDanMuHost, mDanMuPort));

        mDanMuConnector = new Connector(mGroup);
        String loginMessage = String.format("type@=loginreq/username@=%1$s/password@=1234567890123456/roomid@=%2$s/",
                                            mUserName,
                                            mRoomId);
        mDanMuConnector.heartTxt("type@=mrkl/")
                       .messages(loginMessage)
                       .remoteAddress(new InetSocketAddress(mDanMuHost, mDanMuPort))
                       .addMessageHandler(new MessageHandler() {
                           @Override
                           public String getType() {
                               return "loginres";
                           }

                           @Override
                           public void handleMessage(Connector connector, Message message) {
                               String joinMessage = String.format("type@=joingroup/rid@=%1$s/gid@=%2$s/",
                                                                  mRoomId,
                                                                  mSea ? SEA_DANMAKU_GID : mGId);
                               connector.write(joinMessage);
                               connector.removeMessageHandler(this);
                           }
                       })
                       .addMessageHandler(new ErrorMessageHandler(true));

        for (MessageHandler handler : mHandlers) {
            mDanMuConnector.addMessageHandler(handler);
        }

        try {
            mDanMuConnector.connect();
        } catch (ConnectException e) {
            LogUtils.printf(String.format("连接弹幕服务器 [%1$s:%2$d] 失败。", mDanMuHost, mDanMuPort));
            closeConnector(mDanMuConnector, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        try {
            mConnector.close(true);
            mDanMuConnector.close(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeConnector() {
        if (check()) {
            return;
        }
        closeConnector(mConnector, false);
    }

    private boolean check() {
        if (EmptyUtils.isEmpty(mRoomId)) {
            return true;
        }
        if (EmptyUtils.isEmpty(mGId)) {
            return true;
        }
        if (EmptyUtils.isEmpty(mUserName)) {
            return true;
        }
        if (EmptyUtils.isEmpty(mDanMuHost)) {
            return true;
        }
        if (mDanMuPort == 0) {
            return true;
        }
        return false;
    }

    private void closeConnector(Connector connector, boolean closeGroup) {
        try {
            connector.close(closeGroup);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
