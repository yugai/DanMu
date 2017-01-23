package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.Gift;

/**
 * Created by yugai on 2016-4-6.
 */
public class GiftHandler extends FormatMessageHandler<Gift> {
    public GiftHandler() {
        super("dgb", Gift.class);
    }
}
