package com.yugai.danmu.message;

/**
 * Created by yugai on 2016-04-05.
 */
public class SetMsgGroup extends Message {
    private String rid;
    private String gid;

    public SetMsgGroup(Message message) {
        super(message);
        rid = list.get("rid");
        gid = list.get("gid");
    }

    public String getRid() {
        return rid;
    }

    public String getGid() {
        return gid;
    }

    @Override
    public String toString() {
        return "SetMsgGroup{" +
               "rid='" + rid + '\'' +
               ", gid='" + gid + '\'' +
               '}';
    }
}
