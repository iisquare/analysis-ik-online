package com.iisquare.elasticsearch.plugin;

import com.iisquare.elasticsearch.wltea.util.ApiUtil;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class HandlerBase implements RestHandler {

    protected final Logger logger = Loggers.getLogger(getClass(), getClass().getSimpleName());

    public String uri() {
        return "/_plugin/" + IKAnalysisPlugin.pluginName + "/";
    }

    public String uri(String subPath) {
        return uri() + subPath;
    }

    public void text(RestChannel channel, String text) {
        if (null == text) text = "";
        channel.sendResponse(new BytesRestResponse(RestStatus.OK, BytesRestResponse.TEXT_CONTENT_TYPE, text));
    }

    public void json(RestChannel channel, Map<String, ?> map) {
        text(channel, DPUtil.buildJSON(map));
    }

    public void message(RestChannel channel, int code, String message, Object data) {
        text(channel, ApiUtil.echoResult(code, message, data));
    }

    public Map<String, Object> param(RestRequest request) {
        if (request.hasContent()) {
            Tuple<XContentType, Map<String, Object>> tuple = XContentHelper
                    .convertToMap(request.content(), true, request.getXContentType());
            if(XContentType.JSON == tuple.v1()) return tuple.v2();
        }
        return new LinkedHashMap<>();
    }

    protected String[] array(Object value) {
        if (null == value) return new String[] {};
        if (value.getClass().isArray()) {
            return (String[]) value;
        }
        if (value instanceof List) {
            return DPUtil.collectionToStringArray((List<?>) value);
        }
        if (value instanceof Map) {
            return DPUtil.collectionToStringArray(((Map<String, Object>) value).values());
        }
        return new String[] {};
    }

    protected Map<String, Object> map(Object value) {
        if (null == value || !(value instanceof Map)) return new LinkedHashMap<>();
        return (Map<String, Object>) value;
    }

}
