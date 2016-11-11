package com.iisquare.elasticsearch.wltea.web;

import com.iisquare.solr.wltea.util.ApiUtil;
import com.iisquare.solr.wltea.util.DPUtil;

public class ErrorController extends ControllerBase {

	public Object indexAction(Exception e) throws Exception {
		if (!DPUtil.empty(get("debug"))) {
			e.printStackTrace(response.getWriter());
		} else {
			e.printStackTrace();
		}
		if (e instanceof NoSuchMethodException
				|| e instanceof ClassNotFoundException) {
			return displayText(ApiUtil.echoMessage(request, 404, "地址未识别",
					e.getMessage()));
		}
		return displayText(ApiUtil.echoMessage(request, 500, "内部服务错误",
				e.getMessage()));
	}

}
