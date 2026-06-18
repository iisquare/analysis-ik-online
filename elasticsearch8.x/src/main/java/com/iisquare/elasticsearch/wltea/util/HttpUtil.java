package com.iisquare.elasticsearch.wltea.util;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class HttpUtil {

    final static Logger logger = Loggers.getLogger(HttpUtil.class, HttpUtil.class.getSimpleName());

    public static RequestConfig requestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(60000);
        return builder.build();
    }

    public static String requestGet(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) return null;
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
            try {
                if (nvps instanceof String) {
                    entity = new StringEntity((String) nvps);
                } else if (nvps instanceof List) {
                    entity = new UrlEncodedFormEntity((List<? extends NameValuePair>) nvps);
                } else {
                    return null;
                }
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) return null;
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            FileUtil.close(response, httpClient);
        }
    }
}
