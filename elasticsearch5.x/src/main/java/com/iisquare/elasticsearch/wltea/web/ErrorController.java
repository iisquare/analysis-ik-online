package com.iisquare.elasticsearch.wltea.web;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.iisquare.elasticsearch.wltea.util.ApiUtil;
import com.iisquare.elasticsearch.wltea.util.DPUtil;


public class ErrorController extends ControllerBase {

	public Object indexAction(Exception e) throws Exception {
		if (!DPUtil.empty(get("debug"))) {
			OutputStream out = new ByteArrayOutputStream();
			PrintStream print = new PrintStream(out);
			e.printStackTrace(print);
			return displayText(out.toString());
		} else {
			e.printStackTrace();
		}
		if (e instanceof NoSuchMethodException || e instanceof ClassNotFoundException) {
			return displayText(ApiUtil.echoMessage(404, "地址未识别", e.getMessage()));
		}
		return displayText(ApiUtil.echoMessage(500, "内部服务错误", e.getMessage()));
	}

}
