package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.RichManInRoom;

/**
 * Created by yugai on 2016-4-6.
 */
public class RichManInRoomHandler extends FormatMessageHandler<RichManInRoom> {
    public RichManInRoomHandler() {
        super("uenter", RichManInRoom.class);
    }
}
