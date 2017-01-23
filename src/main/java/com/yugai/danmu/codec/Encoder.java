package com.yugai.danmu.codec;

import com.yugai.danmu.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.UnsupportedEncodingException;

/**
 * Created by yugai on 2016-4-6.
 */
public class Encoder extends MessageToByteEncoder<String> {
    public static final byte[] CLIENT_FLAG = new byte[]{(byte) 0xb1, 0x02, 0x00, 0x00};
    private byte[] mByteArrayCache = new byte[1024];

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        // 一条斗鱼 Socket 消息包含 5 个部分：
        // 1. 数据长度，大小为后四部分的字节长度，占 4 个字节。
        // 2. 内容和第一部分一样，占 4 个字节。
        // 3. 斗鱼固定的请求码，占 4 个字节。
        //     本地 -> 服务器是 0xb1,0x02,0x00,0x00 。
        //     服务器 -> 本地是 0xb2,0x02,0x00,0x00 。
        // 4. 消息内容。
        // 5. 尾部一个空字节 0x00 ，占 1 个字节。
        byte[] messageBytes;
        try {
            messageBytes = msg.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // 需要发送的消息的实际字节长度。
        int messageLength = messageBytes.length + 13;
        if (messageLength > mByteArrayCache.length) {
            mByteArrayCache = new byte[messageLength];
        }

        // 1
        int length = messageBytes.length + 9;
        byte[] lengthBytes = ByteUtils.toDouYuBytes(length);
        System.arraycopy(lengthBytes, 0, mByteArrayCache, 0, lengthBytes.length);
        // 2
        System.arraycopy(lengthBytes, 0, mByteArrayCache, 4, lengthBytes.length);
        // 3
        System.arraycopy(CLIENT_FLAG, 0, mByteArrayCache, 8, CLIENT_FLAG.length);
        // 4
        System.arraycopy(messageBytes, 0, mByteArrayCache, 12, messageBytes.length);
        // 5
        mByteArrayCache[messageLength - 1] = (byte) 0x00;
        out.writeBytes(mByteArrayCache, 0, messageLength);
    }
}
