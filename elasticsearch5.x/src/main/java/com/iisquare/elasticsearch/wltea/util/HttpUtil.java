package com.iisquare.elasticsearch.wltea.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	public static String requestGet(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return null;
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
		} finally {
			try {
				if (null != response)
					response.close();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
		return null;
	}

	public static String requestPost(String url, List<NameValuePair> nvps) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		if (null != nvps) {
			StringEntity entity;
			try {
				entity = new UrlEncodedFormEntity(nvps);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			entity.setContentEncoding("UTF-8");
			httpPost.setEntity(entity);
		}
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return null;
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
		} finally {
			try {
				if (null != response)
					response.close();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
		return null;
	}
}
