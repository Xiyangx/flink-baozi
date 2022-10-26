package com.sunac.utils;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/11 4:54 下午
 * @Version 1.0
 */
public class StringToIntegerUtils {
    public static Integer getInteger(String date){
        String replace = date.replace("-", "");
        return Integer.parseInt(replace);
    }
}
