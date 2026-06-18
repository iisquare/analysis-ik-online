package com.iisquare.elasticsearch.wltea.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiUtil {

    public static Map<String, Object> result(int code, String message, Object data) {
        Map<String, Object> object = new LinkedHashMap<>();
        object.put("code", code);
        object.put("message", message);
        object.put("data", data);
        return object;
    }

    public static String echoResult(int code, String message, Object data) {
        return DPUtil.buildJSON(result(code, message, data));
    }
}
