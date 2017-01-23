package com.yugai.danmu.message;

import com.yugai.danmu.util.EmptyUtils;

/**
 * Created by yugai on 2016-04-05.
 */
public class MsgRepeater extends Message {
    private String mId;
    private String mIp;
    private int mPort;

    public MsgRepeater(String message) {
        super(message, "@AS", "@AA=");

        // id@AA=75701@ASnr@AA=1@ASml@AA=10000@ASip@AA=danmu.douyutv.com@ASport@AA=12601
        mId = list.get("id");
        mIp = list.get("ip");
        String port = list.get("port");
        try {
            mPort = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            mPort = -1;
        }
    }

    public String getId() {
        return mId;
    }

    public String getIp() {
        return mIp;
    }

    public int getPort() {
        return mPort;
    }

    public boolean check() {
        return !EmptyUtils.isEmpty(mIp) && mPort > 0;
    }

    @Override
    public String toString() {
        return "MsgRepeater{" +
               "mId='" + mId + '\'' +
               ", mIp='" + mIp + '\'' +
               ", mPort='" + mPort + '\'' +
               '}';
    }
}
