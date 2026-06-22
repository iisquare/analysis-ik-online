package com.iisquare.elasticsearch.wltea.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    final static Logger logger = LogManager.getLogger();

    public static RequestConfig requestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectionRequestTimeout(3000, TimeUnit.MILLISECONDS)
                .setConnectTimeout(3000, TimeUnit.MILLISECONDS)
                .setResponseTimeout(60000, TimeUnit.MILLISECONDS);
        return builder.build();
    }

    public static String requestGet(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getCode() != HttpStatus.SC_OK) return null;
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            FileUtil.close(response, httpClient);
        }
    }

    public static String requestPost(String url, Object nvps) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig());
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        if (null != nvps) {
            StringEntity entity;
            if (nvps instanceof String) {
                entity = new StringEntity((String) nvps, ContentType.create("application/json", StandardCharsets.UTF_8));
            } else if (nvps instanceof List) {
                entity = new UrlEncodedFormEntity((List<? extends NameValuePair>) nvps);
            } else {
                return null;
            }
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getCode() != HttpStatus.SC_OK) return null;
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            FileUtil.close(response, httpClient);
        }
    }
}
