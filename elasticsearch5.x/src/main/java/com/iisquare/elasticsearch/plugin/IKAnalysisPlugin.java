package com.iisquare.elasticsearch.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;

public class IKAnalysisPlugin extends Plugin implements ActionPlugin {
	
	final Logger logger = ESLoggerFactory.getLogger(getClass());

	// TODO:dictionary reload with single or cloud model
	// TODO:1.gradle to properties 2.config from properties 3.quto deploy to plugin directory
	@Override
	public List<Class<? extends RestHandler>> getRestHandlers() {
		logger.debug("#trace@IKAnalysisPlugin.getRestHandlers");
		List<Class<? extends RestHandler>> list = new ArrayList<>();
		list.add(ApplicationHandler.class);
		return list;
	}

	@Override
	public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
		logger.debug("#trace@IKAnalysisPlugin.getGuiceServiceClasses");
		Collection<Class<? extends LifecycleComponent>> list = new ArrayList<>();
		list.add(ApplicationLifecycle.class);
		return list;
	}
	
}
