/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 */
package com.iisquare.elasticsearch.wltea.cfg;

import com.iisquare.elasticsearch.plugin.IKAnalysisPlugin;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.FileUtil;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration 默认实现 2012-5-8
 *
 */
public class Configuration {
    /*
     * 分词器配置文件路径
     */
    private static final String FILE_NAME = "IKAnalyzer.cfg.xml";
    private static Configuration instance;
    final Logger logger = Loggers.getLogger(getClass(), getClass().getSimpleName());
    private String url, word = "word", stopword = "stopword", quantifier = "quantifier", synonym = "synonym";
    private Properties props;
    private String[] initSerials; // 预初始化的词典

    /*
     * 初始化配置文件
     */
    private Configuration() {
        props = new Properties();
        InputStream input = null;
        try {
            if (FileUtil.isExists(IKAnalysisPlugin.pluginPath + FILE_NAME)) {
                // 通过本地文件加载
                input = new FileInputStream(IKAnalysisPlugin.pluginPath + FILE_NAME);
            } else {
                // 通过JAR资源加载
                input = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
            }
            props.loadFromXML(input);
            parseConfig();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FileUtil.close(input);
        }
    }

    /**
     * 返回单例
     *
     * @return Configuration单例
     */
    public static synchronized Configuration getInstance() {
        if (null == instance) {
            instance = new Configuration();
        }
        return instance;
    }

    private void parseConfig() {
        url = props.getProperty("url", IKAnalysisPlugin.pluginPath).trim();
        word = props.getProperty("word", "word").trim();
        stopword = props.getProperty("stopword", "stopword").trim();
        quantifier = props.getProperty("quantifier", "quantifier").trim();
        synonym = props.getProperty("synonym", "synonym").trim();
        initSerials = DPUtil.explode(props.getProperty("initSerials", "").trim(), ",", " ", true);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getStopword() {
        return stopword;
    }

    public void setStopword(String stopword) {
        this.stopword = stopword;
    }

    public String getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(String quantifier) {
        this.quantifier = quantifier;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String[] getInitSerials() {
        return initSerials;
    }

    public void setInitSerials(String[] initSerials) {
        this.initSerials = initSerials;
    }
}
