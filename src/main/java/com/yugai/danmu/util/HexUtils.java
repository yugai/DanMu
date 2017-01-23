package com.yugai.danmu.util;

/**
 * Created by yugai on 2016-4-5.
 */
public class HexUtils {
    public static String toHex(byte b) {
        char[] arrays = "0123456789abcdef".toCharArray();
        int n1 = (b & 0xf0) >>> 4;
        int n2 = b & 0xf;
        return arrays[Math.abs(n1)] + "" + arrays[Math.abs(n2)];
    }

    public static String toHex(int i) {
        String value = "";
        int a = 0xff000000;
        int b = 24;
        for (int j = 0; j < 4; j++) {
            value += toHex((byte) ((i & a) >>> b));
            a >>>= 8;
            b -= 8;
        }
        return value;
    }

    public static String toHex(long l) {
        String value = "";
        long a = 0xff00000000000000L;
        int b = 64;
        for (int j = 0; j < 8; j++) {
            value += toHex((byte) ((l & a) >>> b));
            a >>>= 8;
            b -= 8;
        }
        return value;
    }

    public static void println(byte[] array) {
        System.out.print('[');
        for (byte b : array) {
            System.out.print(toHex(b));
            System.out.print(',');
        }
        System.out.println(']');
    }
}
