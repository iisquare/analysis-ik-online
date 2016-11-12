package com.iisquare.elasticsearch.plugin;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;

public class IKAnalysisPlugin extends Plugin implements ActionPlugin {

	// TODO:where to call Dictionary.initial() on startup and MongoUtil.close() on shutdown
	// TODO:dictionary reload with single or cloud model
	// TODO:1.gradle to properties 2.config from properties 3.quto deploy to plugin directory
	@Override
	public List<Class<? extends RestHandler>> getRestHandlers() {
		List<Class<? extends RestHandler>> list = new ArrayList<>();
		list.add(ApplicationHandler.class);
		return list;
	}

}
