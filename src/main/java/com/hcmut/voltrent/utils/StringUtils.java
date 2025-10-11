package com.hcmut.voltrent.utils;

public class StringUtils {

    public static boolean isNullOrEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof String) {
            String s1 = o.toString();
            return "".equals(s1);
        } else {
            return false;
        }
    }
}
