package com.iisquare.elasticsearch.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;

import com.iisquare.elasticsearch.wltea.lucene.IKAnalyzerProvider;
import com.iisquare.elasticsearch.wltea.util.DPUtil;

public class IKAnalysisPlugin extends Plugin implements AnalysisPlugin, ActionPlugin {
	
	final Logger logger = ESLoggerFactory.getLogger(getClass());
	private static final String pluginDescriptorFileName = "plugin-descriptor.properties";
	public static String pluginName = null; // 插件名称
	public static String pluginPath = null; // 插件运行路径
	public static String pluginLoadPath = null; // 配置加载路径
	
	public IKAnalysisPlugin() throws IOException {
		logger.debug("#trace@IKAnalysisPlugin.construct");
		pluginPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(pluginPath);
		if(file.isFile()) pluginPath = file.getParent();
		String pathEndChar = DPUtil.subString(pluginPath, -1);
		if(!"/".equals(pathEndChar) && !"\\".equals(pathEndChar))  pluginPath += "/";
		InputStream input = getClass().getClassLoader().getResourceAsStream(pluginDescriptorFileName);
		if(null == input) { // 当前运行路径不存在，从插件运行路径获取
			pluginLoadPath = pluginPath;
			input = new FileInputStream(pluginPath + pluginDescriptorFileName);
		}
		logger.info("pluginPath is " + pluginPath);
		logger.info("pluginLoadPath is " + pluginLoadPath);
		Properties props = new Properties();
		props.load(input);
		pluginName = props.getProperty("name");
		input.close();
	}

	/**
	 * 默认推荐分析器，可根据自身业务调整或在启动后采用_settings设置
	 */
	@Override
	public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
		logger.debug("#trace@IKAnalysisPlugin.getAnalyzers");
		Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> map = new HashMap<>();
		map.put("text_ik_i_index", IKAnalyzerProvider::enhanceIndexerForIndex);
		map.put("text_ik_i_query", IKAnalyzerProvider::enhanceIndexerForQuery);
		map.put("text_ik_q_index", IKAnalyzerProvider::enhanceQuerierForIndex);
		map.put("text_ik_q_query", IKAnalyzerProvider::enhanceQuerierForQuery);
		return map;
	}
	
	/**
	 * Rest API用于词典管理和检索示例
	 */
	@Override
	public List<Class<? extends RestHandler>> getRestHandlers() {
		logger.debug("#trace@IKAnalysisPlugin.getRestHandlers");
		List<Class<? extends RestHandler>> list = new ArrayList<>();
		list.add(ApplicationHandler.class);
		return list;
	}

	/**
	 * 插件生命周期管理，用于申请和释放资源
	 */
	@Override
	public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
		logger.debug("#trace@IKAnalysisPlugin.getGuiceServiceClasses");
		Collection<Class<? extends LifecycleComponent>> list = new ArrayList<>();
		list.add(ApplicationLifecycle.class);
		return list;
	}

}
