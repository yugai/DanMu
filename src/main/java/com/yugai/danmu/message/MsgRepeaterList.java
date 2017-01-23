package com.yugai.danmu.message;

import java.util.ArrayList;

/**
 * Created by yugai on 2016-04-05.
 */
public class MsgRepeaterList extends Message {
    private String rid;
    private ArrayList<MsgRepeater> mMsgRepeaterArrayList;

    public MsgRepeaterList(Message message) {
        super(message);
        rid = list.get("rid");
        String list = this.list.get("list");
        String[] split = list.split("@AS@S");
        mMsgRepeaterArrayList = new ArrayList<>();
        for (String s : split) {
            MsgRepeater repeater = new MsgRepeater(s);
            if (repeater.check()) {
                mMsgRepeaterArrayList.add(repeater);
            }
        }
    }

    public String getRid() {
        return rid;
    }

    public ArrayList<MsgRepeater> getMsgRepeaterArrayList() {
        return mMsgRepeaterArrayList;
    }

    @Override
    public String toString() {
        return "MsgRepeaterList{" +
               "rid='" + rid + '\'' +
               ", mMsgRepeaterArrayList=" + mMsgRepeaterArrayList +
               '}';
    }
}
