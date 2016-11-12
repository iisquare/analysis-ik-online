package com.iisquare.elasticsearch.plugin;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;

public class IKAnalysisPlugin extends Plugin implements ActionPlugin {

	// TODO:where to call Dictionary.initial() on startup and MongoUtil.close() on shutdown
	@Override
	public List<Class<? extends RestHandler>> getRestHandlers() {
		System.out.println("@IKAnalysisPlugin.getRestHandlers");
		List<Class<? extends RestHandler>> list = new ArrayList<>();
		list.add(ApplicationHandler.class);
		return list;
	}

}
