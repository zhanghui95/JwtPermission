package com.yilers.jwtp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 处理Json工具类
 */
public class JacksonUtil {

    /**
     * Object转JSON字符串
     */
    public static String toJSONString(Object object) {
        if (object != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.writeValueAsString(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * JSON字符串转List
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                List<T> list = mapper.readValue(json, mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz));
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * JSON字符串转Object
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                T object = mapper.readValue(json, clazz);
                return object;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * objectListToArray
     */
    public static Object[] objectListToArray(List<Object> list) {
        if (list != null) {
            return list.toArray(new Object[list.size()]);
        }
        return null;
    }

    /**
     * stringListToArray
     */
    public static String[] stringListToArray(List<String> list) {
        if (list != null) {
            return list.toArray(new String[list.size()]);
        }
        return null;
    }

    /**
     * stringSetToArray
     */
    public static String[] stringSetToArray(Set<String> set) {
        if (set != null) {
            return set.toArray(new String[set.size()]);
        }
        return null;
    }

}
