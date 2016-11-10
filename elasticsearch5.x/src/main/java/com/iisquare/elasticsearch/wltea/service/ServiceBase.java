package com.iisquare.elasticsearch.wltea.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class ServiceBase {
	public static LinkedHashMap<String, Object> getListTemplate() {
		LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
		returnMap.put("page", 0);
		returnMap.put("total", 0);
		returnMap.put("pageSize", 0);
		returnMap.put("totalPage", 0);
		returnMap.put("rows", new ArrayList<>());
		return returnMap;
	}
}
