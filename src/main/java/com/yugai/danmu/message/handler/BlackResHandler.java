package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.BlackRes;

/**
 * Created by yugai on 2016-4-6.
 */
public class BlackResHandler extends FormatMessageHandler<BlackRes> {
    public BlackResHandler() {
        super("blackres", BlackRes.class);
    }
}
