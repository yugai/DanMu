package com.yugai.danmu.message;

import java.util.LinkedHashMap;

/**
 * Created by yugai on 2016-4-7.
 */
public class Message {
    protected String msg;
    protected String type;
    protected LinkedHashMap<String, String> list;

    public Message() {}

    public Message(String message) {
        this(message, "/", "@=");
    }

    public Message(String message, String split1, String split2) {
        msg = message;
        list = parser(message, split1, split2);
        type = list.get("type");
    }

    public Message(Message message) {
        this.msg = message.msg;
        this.type = message.type;
        this.list = new LinkedHashMap<>(message.list);
    }

    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public LinkedHashMap<String, String> getList() {
        return list;
    }

    public static LinkedHashMap<String, String> parser(String message, String split1, String split2) {
        LinkedHashMap<String, String> list = new LinkedHashMap<>();
        if (message != null) {
            message = message.trim();

            String[] splits1 = message.split(split1);
            for (String s : splits1) {
                String[] splits2 = s.split(split2);
                if (splits2.length == 2) {
                    list.put(splits2[0], splits2[1]);
                }
            }
        }
        return list;
    }
}
