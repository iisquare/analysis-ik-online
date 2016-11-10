package com.iisquare.solr.wltea.web;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iisquare.solr.wltea.util.DPUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class ControllerBase {

	public String appPath, rootPath;
	public String controllerName, actionName;

	public HttpServletRequest request;
	public HttpServletResponse response;

	public Map<String, Object> params; // 请求参数
	public Map<String, Object> assign; // 视图数据Map对象

	public Object init() {
		return null;
	}

	public Object destroy(Object actionVal) {
		return actionVal;
	}

	/**
	 * 设置视图中需要的参数
	 */
	protected void assign(String key, Object value) {
		assign.put(key, value);
	}

	public String url() {
		return url(actionName);
	}

	public String url(String action) {
		return url(controllerName, action);
	}

	/**
	 * 获取URL地址
	 * 
	 * @param controller
	 *            控制器名称
	 * @param action
	 *            方法名称
	 */
	public String url(String controller, String action) {
		return appPath + "/" + controller + "/" + action;
	}

	/**
	 * 输出文本信息
	 */
	protected Object displayText(String text) throws Exception {
		PrintWriter out = response.getWriter();
		out.print(text);
		out.flush();
		return null;
	}

	/**
	 * 将assign中的数据输出为JSON格式
	 */
	protected Object displayJSON() throws Exception {
		return displayJSON(assign);
	}

	/**
	 * 输出JSON信息
	 * 
	 * @param object
	 *            对输出对象
	 */
	protected Object displayJSON(Object object) throws Exception {
		String result;
		if (object instanceof Map) {
			result = JSONObject.fromObject(object).toString();
		} else {
			result = JSONArray.fromObject(object).toString();
		}
		return displayText(result);
	}

	/**
	 * 重定向自定义URL地址
	 */
	protected Object redirect(String url) throws Exception {
		response.sendRedirect(url);
		return null;
	}

	/**
	 * 获取请求参数
	 * 
	 * @param key
	 *            参数名称
	 */
	protected String get(String key) {
		return DPUtil.parseString(params.get(key));
	}

	/**
	 * 获取请求参数数组
	 * 
	 * @param key
	 *            参数名称
	 */
	@SuppressWarnings("unchecked")
	protected String[] getArray(String key) {
		Object value = params.get(key);
		if (null == value)
			return new String[] {};
		if (value.getClass().isArray()) {
			return (String[]) value;
		}
		if (value instanceof Map) {
			return DPUtil.collectionToStringArray(((Map<String, Object>) value)
					.values());
		}
		return new String[] {};
	}

	/**
	 * 获取请求参数Map
	 * 
	 * @param key
	 *            参数名称
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getMap(String key) {
		Object value = params.get(key);
		if (null == value || !(value instanceof Map))
			return new LinkedHashMap<>();
		return (Map<String, Object>) value;
	}
}
