package com.yugai.danmu.util;

import java.util.List;

/**
 * Created by yugai on 2016-4-6.
 */
public class EmptyUtils {
    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }

    public static boolean isEmpty(List list) {
        return null == list || list.isEmpty();
    }

    public static <T> boolean isEmpty(T[] array) {
        return null == array || array.length == 0;
    }
}
