package com.iisquare.elasticsearch.plugin;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;

import com.iisquare.elasticsearch.wltea.dic.Dictionary;
import com.iisquare.elasticsearch.wltea.util.MongoUtil;

public class ApplicationLifecycle extends AbstractLifecycleComponent {
	
	final Logger logger = ESLoggerFactory.getLogger(getClass());

	@Inject
	public ApplicationLifecycle(Settings settings) {
		super(settings);
		logger.debug("#trace@ApplicationLifecycle.construct");
	}

	@Override
	protected void doStart() {
		logger.debug("#trace@ApplicationLifecycle.doStart");
		Dictionary.initial();
	}

	@Override
	protected void doStop() {
		logger.debug("#trace@ApplicationLifecycle.doStop");
	}

	@Override
	protected void doClose() {
		logger.debug("#trace@ApplicationLifecycle.doClose");
		MongoUtil.close();
	}

}
