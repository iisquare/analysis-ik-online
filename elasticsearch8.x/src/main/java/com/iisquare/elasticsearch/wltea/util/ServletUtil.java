package com.iisquare.elasticsearch.wltea.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet操作类
 */
public class ServletUtil {

    public static final String regexParameterMapKey = "((?<!\\[)[^\\[\\]]+(?!\\])|(?<=\\[)[^\\[\\]]*(?=\\]))";

    /**
     * JavaWeb中：解析ParameterMap，将中括号[]中的字符串转换为下标 下标支持非中括号[]的任意字符，包括空格等
     * 若存在多个相同的下标（以中括号[]标识的数组除外），默认取最后一个下标对应的值
     * ElasticsearchNetty中：若存在多个相同的下标（包括以中括号[]标识的数组），默认取最后一个下标对应的值
     * 所以，以中括号[]标识的数组，即使是自然下标也不能为空值
     *
     * @param parameterMap 参数Map
     */
    public static Map<String, Object> parseParameterMap(Map<String, String> parameterMap) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            List<String> keys = DPUtil.getMatcher(regexParameterMapKey, entry.getKey(), true);
            generateParameterMap(map, keys, new String[]{entry.getValue()}, 0, keys.size());
        }
        return map;
    }

    /**
     * 按照KV形式，递归生成ParameterMap
     *
     * @param map        当前层级的LinkedHashMap<String, Object>
     * @param keyList    下标列表
     * @param valueArray 下标对应值
     * @param index      下标当前位置
     * @param length     处理深度
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> generateParameterMap(
            Map<String, Object> map, List<String> keyList, String[] valueArray, int index, int length) {
        int indexNext = index + 1;
        String key = keyList.get(index);
        if (indexNext >= length) { // 当前为最终位置，不存在下级元素
            map.put(key, valueArray.length > 0 ? valueArray[valueArray.length - 1] : ""); // 默认取最后一个值
            return map;
        }
        String keyNext = keyList.get(indexNext); // 存在下级元素
        if (0 == keyNext.length()) { // 下级元素为[]数组形式，应为最终位置
            map.put(key, valueArray);
            return map;
        }
        /* 下级元素为KV形式，继续递归处理 */
        Map<String, Object> subMap = (Map<String, Object>) map.get(key);
        if (null == subMap) subMap = new LinkedHashMap<String, Object>(); // 初始化下级Map
        map.put(key, generateParameterMap(subMap, keyList, valueArray, indexNext, length));
        return map;
    }

}