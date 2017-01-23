package com.yugai.danmu.message;

import com.yugai.danmu.message.handler.FormatMessageHandler;
import com.yugai.danmu.util.EmptyUtils;

/**
 * Created by yugai on 2016-4-8.
 */
public class Gift extends Message implements FormatMessageHandler.FormatMessage {
    private String hit;
    private String nn;
    private String gs;

    public Gift(Message message) {
        super(message);

        nn = list.get("nn");
        hit = list.get("hits");
        gs = list.get("gs");
    }

    public String getSend() {
        return nn;
    }

    public String getGift() {
        switch (gs) {
            case "1":
                return "100 鱼丸";
            case "3":
                return "赞";
            case "4":
                return "666";
            case "2":
                return "520 鱼丸";
            case "5":
                return "飞机";
            case "6":
                return "火箭";
        }
        return "未知礼物";
    }

    public String getHit() {
        if (EmptyUtils.isEmpty(hit)) {
            return "";
        } else {
            return "x " + hit + " ";
        }
    }

    @Override
    public String toString() {
        return "Gift{" +
               "hit='" + hit + '\'' +
               ", nn='" + nn + '\'' +
               ", gs='" + gs + '\'' +
               '}';
    }

    @Override
    public String getMessage() {
        return String.format("[礼物][%1$s] 赠送主播 [%2$s] %3$s。", getSend(), getGift(), getHit());
    }
}
