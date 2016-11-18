package com.iisquare.elasticsearch.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iisquare.elasticsearch.wltea.util.DPUtil;

public class JSONTester {

	/**
	 * JSON对照表
	 * JSON Type			->	Java Type
	 * object				->	LinkedHashMap<String,Object>
	 * array				->	ArrayList<Object>
	 * string				->	String
	 * number (no fraction)	->	Integer, Long or BigInteger (smallest applicable)
	 * number (fraction)	->	Double (configurable to use BigDecimal)
	 * true|false			->	Boolean
	 * null					->	null
	 */
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", "2");
		List<Object> list = new ArrayList<>();
		map.put("c", list);
		map.put("d", new HashMap<String, Object>());
		String json = DPUtil.buildJSON(map);
		System.out.println(json);
		Object object = DPUtil.parseJSON(json);
		System.out.println(object);
	}
	
}
