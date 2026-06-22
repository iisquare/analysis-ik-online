package com.iisquare.elasticsearch.wltea.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    final static Logger logger = LogManager.getLogger();

    private static CloseableHttpClient secureHttpClient;

    /**
     * 获取支持 HTTPS（含自签名证书）的 HttpClient 单例
     * ES 8.x 开启 security 后默认使用自签名 TLS 证书，需要跳过证书校验
     */
    private static synchronized CloseableHttpClient getHttpClient() {
        if (null != secureHttpClient) return secureHttpClient;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(sslContext)
                            .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                            .build())
                    .build();
            secureHttpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();
            return secureHttpClient;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("failed to create SSL HttpClient", e);
            return HttpClients.createDefault();
        }
    }

    public static RequestConfig requestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectionRequestTimeout(3000, TimeUnit.MILLISECONDS)
                .setConnectTimeout(3000, TimeUnit.MILLISECONDS)
                .setResponseTimeout(60000, TimeUnit.MILLISECONDS);
        return builder.build();
    }

    public static String requestGet(String url) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getCode() != HttpStatus.SC_OK) return null;
            return EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            FileUtil.close(response);
        }
    }

    public static String requestPost(String url, Object nvps) {
        return requestPost(url, nvps, null);
    }

    /**
     * @param url     请求地址
     * @param nvps    请求体（String 或 List&lt;NameValuePair&gt;）
     * @param headers 自定义请求头，用于传递 Authorization 等认证信息
     */
    public static String requestPost(String url, Object nvps, Map<String, String> headers) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig());
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        if (null != headers) {
            headers.forEach(httpPost::setHeader);
        }
        if (null != nvps) {
            StringEntity entity;
            if (nvps instanceof String) {
                entity = new StringEntity((String) nvps, ContentType.create("application/json", StandardCharsets.UTF_8));
            } else if (nvps instanceof List) {
                entity = new UrlEncodedFormEntity((List<? extends NameValuePair>) nvps);
            } else {
                return null;
            }
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getCode() != HttpStatus.SC_OK) return null;
            return EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            FileUtil.close(response);
        }
    }

    /**
     * 关闭共享的 HttpClient，通常在插件卸载或 ES 节点关闭时调用
     */
    public static synchronized void close() {
        if (null != secureHttpClient) {
            FileUtil.close(secureHttpClient);
            secureHttpClient = null;
        }
    }
}
