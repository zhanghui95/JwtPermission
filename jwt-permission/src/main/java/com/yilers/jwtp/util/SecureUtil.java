package com.yilers.jwtp.util;

import java.security.MessageDigest;

/**
 * md5
 * @author: hui.zhang
 * @date: 2021/1/14 4:13 下午
 **/
public class SecureUtil {

    public static String md5(String s) {
        try {
            // 获取MD5实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 此处传入要加密的byte类型值
            md.update(s.getBytes("UTF-8"));
            byte[] digest = md.digest();
            int i;
            StringBuilder sb = new StringBuilder();
            for (int offset = 0; offset < digest.length; offset++) {
                i = digest[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    sb.append(0);
                }
                // 通过Integer.toHexString方法把值变为16进制
                sb.append(Integer.toHexString(i));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("md5出错");
        }
    }
}
