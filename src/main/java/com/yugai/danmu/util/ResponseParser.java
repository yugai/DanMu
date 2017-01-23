package com.yugai.danmu.util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Brucezz on 2016/01/03.
 * DouyuCrawler
 */
public class ResponseParser {

    private static final String REGEX_ROOM_ID = "\"room_id\":(\\d*),";
    private static final String REGEX_ROOM_STATUS = "\"show_status\":(\\d*),";
    private static final String REGEX_SERVER = "%7B%22ip%22%3A%22(.*?)%22%2C%22port%22%3A%22(.*?)%22%7D%2C";

    private static Matcher getMatcher(String content, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return pattern.matcher(content);
    }

    /**
     * 从房间页面解析出roomId
     */
    public static String parseRoomId(String content) {
        if (content == null) {
            return "";
        }

        Matcher matcher = getMatcher(content, REGEX_ROOM_ID);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * 解析当前直播状态
     *
     * @return 若room_status == 1 则正在直播
     */
    public static boolean parseOnline(String content) {
        if (content == null) {
            return false;
        }

        Matcher matcher = getMatcher(content, REGEX_ROOM_STATUS);
        return matcher.find() && "1".equals(matcher.group(1));
    }

    /**
     * 解析出服务器地址
     */
    public static List<InetSocketAddress> parseServerInfo(String content) {
        if (content == null) {
            return null;
        }

        Matcher matcher = getMatcher(content, REGEX_SERVER);
        List<InetSocketAddress> list = new ArrayList<>();

        while (matcher.find()) {
            int port;
            try {
                port = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                continue;
            }
            String host = matcher.group(1);
            LogUtils.printf(host+":"+port);
            InetSocketAddress info = new InetSocketAddress(host,port);
            list.add(info);
        }
        return list;
    }
}
