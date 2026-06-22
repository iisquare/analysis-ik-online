package com.iisquare.elasticsearch.plugin;

import com.iisquare.elasticsearch.wltea.dic.Dictionary;
import com.iisquare.elasticsearch.wltea.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.component.AbstractLifecycleComponent;

import java.io.IOException;

public class IKAnalysisLifecycle extends AbstractLifecycleComponent {

    final Logger logger = LogManager.getLogger();

    @Override
    protected void doStart() {
        logger.debug("#trace@ApplicationLifecycle.doStart");
        Dictionary.initial();
    }

    @Override
    protected void doStop() {
        logger.debug("#trace@ApplicationLifecycle.doStart");
    }

    @Override
    protected void doClose() throws IOException {
        logger.debug("#trace@ApplicationLifecycle.doClose");
        HttpUtil.close();
    }
}
