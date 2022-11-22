package com.sunac.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/11/18 6:13 下午
 * @Version 1.0
 */
public class MakeMd5Utils {
    public static String makeMd5(String... s) {
        StringBuilder sb = new StringBuilder();
        if (null == s) {
            return MakeMd5Utils.makeMd5("");
        }
        for (String e : s) {
            if (StringUtils.isBlank(e)) {
                e = "";
            }
            sb.append(e);
        }
        return DigestUtils.md5Hex(sb.toString());
    }
}
