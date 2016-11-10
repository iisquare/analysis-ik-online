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
 */
package com.iisquare.elasticsearch.wltea.cfg;

/**
 * 
 * 配置管理类接口
 * 
 */
public interface Configuration {

	public String getHost();

	public void setHost(String host);

	public String getAuthDatabase();

	public void setAuthDatabase(String authDatabase);
	
	public String getUserName();

	public void setUserName(String userName);

	public String getPassword();

	public void setPassword(String password);

	public String getDictDatabase();

	public void setDictDatabase(String dictDatabase);

	public String getDictWord();

	public void setDictWord(String dictWord);

	public String getDictStopword();

	public void setDictStopword(String dictStopword);

	public String getDictQuantifier();

	public void setDictQuantifier(String dictQuantifier);

	public String getDictSynonym();
	
	public int getPort();

	public void setPort(int port);

	public String[] getInitDictSerials();

	public void setInitDictSerials(String[] initDictSerials);

}
