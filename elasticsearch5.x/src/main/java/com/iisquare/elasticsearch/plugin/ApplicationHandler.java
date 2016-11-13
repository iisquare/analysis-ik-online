package com.iisquare.elasticsearch.plugin;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.rest.RestRequest;

import com.iisquare.elasticsearch.wltea.util.ServletUtil;
import com.iisquare.elasticsearch.wltea.web.ControllerBase;

public class ApplicationHandler implements RestHandler {
	
	final Logger logger = ESLoggerFactory.getLogger(getClass());
	private String appPath, classNamePath;
	public static String defaultControllerName = "index";
	public static String defaultActionName = "index";
	public static String defaultErrorController = "error";
	public static String defaultErrorAction = "index";
	public static String defaultControllerSuffix = "Controller";
	public static String defaultActionSuffix = "Action";

	@Inject
	public ApplicationHandler(Settings settings, RestController controller) {
		logger.debug("#trace@ApplicationHandler.construct");
		appPath = "/_plugin/" + IKAnalysisPlugin.pluginName + "/";
		controller.registerHandler(RestRequest.Method.GET, appPath + "{controllerName}/{actionName}", this);
		controller.registerHandler(RestRequest.Method.POST, appPath + "{controllerName}/{actionName}", this);
		String className = ControllerBase.class.getName();
		String classSimpleName = ControllerBase.class.getSimpleName();
		classNamePath = className.substring(0, className.length() - classSimpleName.length() - 1);
	}

	@Override
	public void handleRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
		logger.debug("#trace@ApplicationHandler.handleRequest");
		String controllerName = request.param("controllerName", defaultControllerName);
		String actionName = request.param("actionName", defaultActionName);
		Object retVal = invoke(request, channel, client, controllerName, actionName, null);
		if(null != retVal) retVal = invoke(request,
				channel, client, defaultErrorController, defaultErrorAction, retVal);
		if(null != retVal) throw new Exception(retVal.toString());
	}
	
	private Exception invoke(RestRequest request,
			RestChannel channel, NodeClient client, String controllerName, String actionName, Object arg) {
		Class<?> controller;
		try {
			controller = Class.forName(classNamePath + "."
					+ controllerName.substring(0, 1).toUpperCase()
					+ controllerName.substring(1) + defaultControllerSuffix);
			ControllerBase instance = (ControllerBase) controller.newInstance();
			instance.appPath = appPath;
			instance.controllerName = controllerName;
			instance.actionName = actionName;
			instance.request = request;
			instance.channel = channel;
			instance.client = client;
			instance.params = ServletUtil.parseParameterMap(request.params());
			instance.assign = new LinkedHashMap<>();
			Object initVal = instance.init();
			if(null != initVal) return new Exception("initError");
			Object actionVal;
			if(null == arg) {
				actionVal = controller.getMethod(actionName + defaultActionSuffix).invoke(instance);
			} else {
				actionVal = controller.getMethod(actionName + defaultActionSuffix, Exception.class).invoke(instance, arg);
			}
			Object destroyVal = instance.destroy(actionVal);
			if (null != destroyVal) return new Exception("destroyError");
		} catch (Exception e) {
			return new Exception("Route:controller[" + controllerName + "] - action[" + actionName + "]", e);
		}
		return null;
	}

}
