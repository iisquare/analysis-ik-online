package com.iisquare.elasticsearch.plugin;

import com.iisquare.elasticsearch.handler.DemoHandler;
import com.iisquare.elasticsearch.handler.IndexHandler;
import com.iisquare.elasticsearch.handler.ReloadHandler;
import com.iisquare.elasticsearch.handler.StateHandler;
import com.iisquare.elasticsearch.wltea.lucene.IKAnalyzerProvider;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.FileUtil;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public class IKAnalysisPlugin extends Plugin implements AnalysisPlugin, ActionPlugin {

    private static final String pluginDescriptorFileName = "plugin-descriptor.properties";
    public static String pluginName = null; // 插件名称
    public static String pluginPath = null; // 插件运行路径
    final Logger logger = Loggers.getLogger(getClass(), getClass().getSimpleName());

    public IKAnalysisPlugin() throws IOException {
        logger.debug("#trace@IKAnalysisPlugin.construct");
        pluginPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath(); // JAR路径
        File file = new File(pluginPath);
        if (file.isFile()) pluginPath = file.getParent(); // 获取JAR运行目录
        String pathEndChar = DPUtil.subString(pluginPath, -1);
        if (!"/".equals(pathEndChar) && !"\\".equals(pathEndChar)) pluginPath += "/";
        logger.info("pluginPath is " + pluginPath);
        InputStream input;
        if (FileUtil.isExists(pluginPath + pluginDescriptorFileName)) {
            // 通过本地文件加载，生产环境部署
            input = new FileInputStream(pluginPath + pluginDescriptorFileName);
        } else {
            // 通过JAR资源加载，本地开发调试
            input = getClass().getClassLoader().getResourceAsStream(pluginDescriptorFileName);
        }
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

    @Override
    public List<RestHandler> getRestHandlers(Settings settings, RestController restController, ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings, SettingsFilter settingsFilter, IndexNameExpressionResolver indexNameExpressionResolver, Supplier<DiscoveryNodes> nodesInCluster) {
        logger.debug("#trace@IKAnalysisPlugin.getRestHandlers");
        return Arrays.asList(
                new DemoHandler(),
                new IndexHandler(),
                new ReloadHandler(),
                new StateHandler()
        );
    }

    @Override
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        logger.debug("#trace@IKAnalysisPlugin.getGuiceServiceClasses");
        return Arrays.asList(IKAnalysisLifecycle.class);
    }
}
