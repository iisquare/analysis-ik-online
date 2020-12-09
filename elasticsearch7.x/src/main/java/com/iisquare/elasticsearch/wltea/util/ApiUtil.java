package com.iisquare.elasticsearch.wltea.util;

import java.util.LinkedHashMap;

public class ApiUtil {
    public static String echoMessage(int code, String message, Object data) {
        LinkedHashMap<String, Object> object = new LinkedHashMap<>();
        object.put("code", code);
        object.put("message", message);
        object.put("data", data);
        return DPUtil.buildJSON(object);
    }
}
