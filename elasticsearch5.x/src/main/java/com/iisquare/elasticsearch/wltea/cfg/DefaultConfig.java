/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 * 
 */
package com.iisquare.elasticsearch.wltea.cfg;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.iisquare.elasticsearch.plugin.IKAnalysisPlugin;
import com.iisquare.elasticsearch.wltea.util.DPUtil;

/**
 * Configuration 默认实现 2012-5-8
 *
 */
public class DefaultConfig implements Configuration {
	/*
	 * 分词器配置文件路径
	 */
	private static final String FILE_NAME = "IKAnalyzer.cfg.xml";

	private static DefaultConfig instance;

	private Properties props;

	private String host, authDatabase, userName, password;
	private String dictDatabase, dictWord, dictStopword, dictQuantifier, dictSynonym;
	private int port = 27017;

	private String[] initDictSerials; // 预初始化的词典

	/**
	 * 返回单例
	 * 
	 * @return Configuration单例
	 */
	public static synchronized Configuration getInstance() {
		if (null == instance) {
			instance = new DefaultConfig();
		}
		return instance;
	}

	/*
	 * 初始化配置文件
	 */
	private DefaultConfig() {
		props = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(IKAnalysisPlugin.pluginLoadPath + FILE_NAME);
			props.loadFromXML(input);
			parseConfig();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != input) input.close();
			} catch (IOException e) {}
		}
	}

	private void parseConfig() {
		host = props.getProperty("host", "127.0.0.1").trim();
		userName = props.getProperty("userName", "").trim();
		authDatabase = props.getProperty("authDatabase", "admin").trim();
		password = props.getProperty("password", "").trim();
		dictDatabase = props.getProperty("dictDatabase", "lucene").trim();
		dictWord = props.getProperty("dictWord", "dict.word").trim();
		dictStopword = props.getProperty("dictStopword", "dict.stopword").trim();
		dictQuantifier = props.getProperty("dictQuantifier", "dict.quantifier").trim();
		dictSynonym = props.getProperty("dictSynonym", "dict.synonym").trim();
		port = Integer.valueOf(props.getProperty("port", "27017").trim());
		initDictSerials = DPUtil.explode(props.getProperty("initDictSerials", "").trim(), ",", " ", true);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAuthDatabase() {
		return authDatabase;
	}

	public void setAuthDatabase(String authDatabase) {
		this.authDatabase = authDatabase;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDictDatabase() {
		return dictDatabase;
	}

	public void setDictDatabase(String dictDatabase) {
		this.dictDatabase = dictDatabase;
	}

	public String getDictWord() {
		return dictWord;
	}

	public void setDictWord(String dictWord) {
		this.dictWord = dictWord;
	}

	public String getDictStopword() {
		return dictStopword;
	}

	public void setDictStopword(String dictStopword) {
		this.dictStopword = dictStopword;
	}

	public String getDictQuantifier() {
		return dictQuantifier;
	}

	public void setDictQuantifier(String dictQuantifier) {
		this.dictQuantifier = dictQuantifier;
	}

	public String getDictSynonym() {
		return dictSynonym;
	}

	public void setDictSynonym(String dictSynonym) {
		this.dictSynonym = dictSynonym;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String[] getInitDictSerials() {
		return initDictSerials;
	}

	public void setInitDictSerials(String[] initDictSerials) {
		this.initDictSerials = initDictSerials;
	}

}
