package com.iisquare.elasticsearch.wltea.web;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iisquare.solr.wltea.dic.Dictionary;
import com.iisquare.solr.wltea.util.MongoUtil;
import com.iisquare.solr.wltea.util.ServletUtil;

public class FrameServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String appPath, rootPath, classNamePath;
	public static String defaultControllerName = "index";
	public static String defaultActionName = "index";
	public static String defaultErrorController = "error";
	public static String defaultErrorAction = "index";
	public static String defaultControllerSuffix = "Controller";
	public static String defaultActionSuffix = "Action";

	@Override
	public void destroy() {
		super.destroy();
		MongoUtil.close();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		appPath = getInitParameter("appPath");
		if (!appPath.endsWith("/"))
			appPath += "/";
		String className = ControllerBase.class.getName();
		String classSimpleName = ControllerBase.class.getSimpleName();
		classNamePath = className.substring(0, className.length()
				- classSimpleName.length() - 1);
		rootPath = getServletContext().getRealPath("/");
		Dictionary.initial(); // 初始化词典
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.service(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.service(request, response);
	}

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		Object[] route = parse(request);
		try {
			if (null == route)
				throw new Exception("uriError");
			processInvoke(request, response, route, null, 0);
		} catch (Exception e) {
			route[0] = defaultErrorController;
			route[1] = defaultErrorAction;
			try {
				processInvoke(request, response, route, e, 0);
			} catch (Exception e1) {
				proessError(request, response, route, e1, 1);
			}
		}
	}

	private void processInvoke(HttpServletRequest request,
			HttpServletResponse response, Object[] route, Exception e, int count)
			throws Exception {
		String controllerName = route[0].toString();
		String actionName = route[1].toString();
		Class<?> controller = Class.forName(classNamePath + "."
				+ controllerName.substring(0, 1).toUpperCase()
				+ controllerName.substring(1) + defaultControllerSuffix);
		ControllerBase instance = (ControllerBase) controller.newInstance();
		instance.appPath = appPath;
		instance.rootPath = rootPath;
		instance.controllerName = controllerName;
		instance.actionName = actionName;
		instance.request = request;
		instance.response = response;
		instance.params = ServletUtil.parseParameterMap(request.getParameterMap());
		instance.assign = new LinkedHashMap<>();
		Object initVal = instance.init();
		if (null != initVal) {
			proessError(request, response, route, new Exception("initError"),
					count);
			return;
		}
		Object actionVal;
		if (null == e) {
			actionVal = controller.getMethod(actionName + defaultActionSuffix)
					.invoke(instance);
		} else {
			actionVal = controller.getMethod(actionName + defaultActionSuffix,
					Exception.class).invoke(instance, e);
		}
		Object destroyVal = instance.destroy(actionVal);
		if (null != destroyVal) {
			proessError(request, response, route,
					new Exception("destroyError"), count);
			return;
		}
	}

	private void proessError(HttpServletRequest request,
			HttpServletResponse response, Object[] route, Exception e, int count) {
		if (count > 0) {
			System.out.println("+++++++++++++proessError+++++++++++++++++");
			System.out.println(route.toString());
			e.printStackTrace();
			return;
		}
		route[0] = defaultErrorController;
		route[1] = defaultErrorAction;
		try {
			processInvoke(request, response, route, e, count++);
		} catch (Exception e1) {
			proessError(request, response, route, new Exception("proessError"),
					count++);
		}
	}

	private Object[] parse(HttpServletRequest request) {
		Object[] route = new Object[3];
		route[0] = defaultControllerName; // controllerName
		route[1] = defaultActionName; // actionName
		route[2] = ""; // paramString
		String uri = request.getRequestURI().trim();
		if (!uri.matches("^[\\/_a-zA-Z\\d\\-]*$"))
			return null;
		if (!uri.endsWith("/"))
			uri += "/";
		if (null != appPath && uri.startsWith(appPath))
			uri = uri.replaceFirst(appPath, "");
		if (uri.startsWith("/"))
			uri = uri.substring(1);
		if (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);
		if ("".equals(uri))
			return route;
		String[] uris = uri.split("/");
		route[0] = uris[0];
		if (1 == uris.length)
			return route;
		route[1] = uris[1];
		if (2 == uris.length)
			return route;
		StringBuilder stringBuilder = new StringBuilder();
		int i, length = uris.length - 1;
		for (i = 2; i < length; i++) {
			stringBuilder.append(uris[i]).append("/");
		}
		stringBuilder.append(uris[i]);
		route[2] = stringBuilder.toString();
		return route;
	}
}
