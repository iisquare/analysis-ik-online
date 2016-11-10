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
package com.iisquare.solr.wltea.dic;

import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymMap;

import com.iisquare.solr.wltea.cfg.Configuration;
import com.iisquare.solr.wltea.cfg.DefaultConfig;
import com.iisquare.solr.wltea.lucene.IKSynonymParser;
import com.iisquare.solr.wltea.util.MongoUtil;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * 词典管理类,单子模式
 */
public class Dictionary {

	private static HashMap<String, Dictionary> singletonMap = new HashMap<String, Dictionary>(); // 词典单子实例
	private DictSegment _MainDict;// 主词典对象
	private DictSegment _StopWordDict; // 停止词词典
	private DictSegment _QuantifierDict; // 量词词典
	private SynonymMap _SynonymDict; // 同义词词典
	private String dictSerial; // 词典编号（多词典支持）
	private Configuration configuration; // 配置参数

	private Dictionary(String dictSerial) {
		this.dictSerial = dictSerial;
		configuration = DefaultConfig.getInstance();
		LinkedHashMap<String, Boolean> map = reload(new String[] { "stopword", "quantifier", "word", "synonym" });
		if (!map.get("status")) {
			throw new RuntimeException("init dictionary error");
		}
	}

	public synchronized LinkedHashMap<String, Boolean> reload(String[] dicts) {
		LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();
		boolean status = true;
		try {
			MongoClient mongoClient = MongoUtil.connect(configuration.getHost(), configuration.getPort(),
					configuration.getUserName(), configuration.getAuthDatabase(), configuration.getPassword());
			DB db = mongoClient.getDB(configuration.getDictDatabase());
			Boolean bool;
			for (int i = 0; i < dicts.length; i++) {
				String dict = dicts[i];
				if (status) {
					switch (dict) {
					case "quantifier":
						bool = loadQuantifierDict(db);
						break;
					case "stopword":
						bool = loadStopWordDict(db);
						break;
					case "synonym":
						bool = loadSynonymDict(db);
						// bool &= IKSynonymFilter.updateDict(_SynonymDict,
						// true);
						break;
					case "word":
						bool = loadMainDict(db);
						break;
					default:
						map.put(dict, null);
						continue;
					}
					status &= bool;
				} else {
					bool = null;
				}
				map.put(dict, bool);
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		} finally {
			// MongoUtil.close();
		}
		map.put("status", status);
		return map;
	}

	/**
	 * 词典初始化 由于IK Analyzer的词典采用Dictionary类的静态方法进行词典初始化
	 * 只有当Dictionary类被实际调用时，才会开始载入词典， 这将延长首次分词操作的时间 该方法提供了一个在应用加载阶段就初始化字典的手段
	 */
	public static void initial() {
		String[] dictSerials = DefaultConfig.getInstance().getInitDictSerials();
		getSingleton(null);
		for (int i = 0; i < dictSerials.length; i++) {
			getSingleton(dictSerials[i]);
		}
	}

	/**
	 * 获取词典单子实例
	 * 
	 * @return Dictionary 单例对象
	 */
	public static Dictionary getSingleton(String dictSerial) {
		if (singletonMap.get(dictSerial) == null) {
			synchronized (Dictionary.class) {
				if (singletonMap.get(dictSerial) == null) {
					singletonMap.put(dictSerial, new Dictionary(dictSerial));
					return singletonMap.get(dictSerial);
				}
			}
		}
		return singletonMap.get(dictSerial);
	}

	/**
	 * 批量加载新词条
	 * 
	 * @param words
	 *            Collection<String>词条列表
	 */
	public void addWords(Collection<String> words) {
		if (words == null)
			return;
		for (String word : words) {
			if (word == null)
				continue;
			// 批量加载词条到主内存词典中
			_MainDict.fillSegment(word.trim().toLowerCase().toCharArray());
		}
	}

	/**
	 * 批量移除（屏蔽）词条
	 */
	public void disableWords(Collection<String> words) {
		if (words == null)
			return;
		for (String word : words) {
			if (word == null)
				continue;
			// 批量屏蔽词条
			_MainDict.disableSegment(word.trim().toLowerCase().toCharArray());
		}
	}

	/**
	 * 检索匹配主词典
	 * 
	 * @return Hit 匹配结果描述
	 */
	public Hit matchInMainDict(char[] charArray) {
		return _MainDict.match(charArray);
	}

	/**
	 * 检索匹配主词典
	 * 
	 * @return Hit 匹配结果描述
	 */
	public Hit matchInMainDict(char[] charArray, int begin, int length) {
		return _MainDict.match(charArray, begin, length);
	}

	/**
	 * 检索匹配量词词典
	 * 
	 * @return Hit 匹配结果描述
	 */
	public Hit matchInQuantifierDict(char[] charArray, int begin, int length) {
		return _QuantifierDict.match(charArray, begin, length);
	}

	/**
	 * 从已匹配的Hit中直接取出DictSegment，继续向下匹配
	 */
	public Hit matchWithHit(char[] charArray, int currentIndex, Hit matchedHit) {
		DictSegment ds = matchedHit.getMatchedDictSegment();
		return ds.match(charArray, currentIndex, 1, matchedHit);
	}

	/**
	 * 判断是否是停止词
	 */
	public boolean isStopWord(char[] charArray, int begin, int length) {
		return _StopWordDict.match(charArray, begin, length).isMatch();
	}

	public SynonymMap getSynonymDict() {
		return _SynonymDict;
	}

	public static String buildCollectionName(String collectionName,
			String dictSerial) {
		if (null == dictSerial || "".equals(dictSerial))
			return collectionName;
		return collectionName + "." + dictSerial;
	}

	/**
	 * 加载主词典及扩展词典
	 */
	private boolean loadMainDict(DB db) {
		_MainDict = this.loadDictSegment(db, buildCollectionName(configuration.getDictWord(), dictSerial));
		return null != _MainDict;
	}

	/**
	 * 加载用户扩展的停止词词典
	 */
	private boolean loadStopWordDict(DB db) {
		_StopWordDict = this.loadDictSegment(db, buildCollectionName(configuration.getDictStopword(), dictSerial));
		return null != _StopWordDict;
	}

	/**
	 * 加载量词词典
	 */
	private boolean loadQuantifierDict(DB db) {
		_QuantifierDict = this.loadDictSegment(db, buildCollectionName(configuration.getDictQuantifier(), dictSerial));
		return null != _QuantifierDict;
	}

	private DictSegment loadDictSegment(DB db, String collectionName) {
		DictSegment dictSegment = new DictSegment((char) 0);
		DBCollection coll = db.getCollection(collectionName);
		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()) {
				DBObject doc = cursor.next();
				Object text = doc.get("text");
				if (null == text || "".equals(text.toString().trim()))
					continue;
				dictSegment.fillSegment(text.toString().trim().toLowerCase()
						.toCharArray());
			}
		} catch (Exception e) {
			return null;
		} finally {
			cursor.close();
		}
		return dictSegment;
	}

	private boolean loadSynonymDict(DB db) throws Exception {
		Analyzer analyzer = new Analyzer() {
			@Override
			protected TokenStreamComponents createComponents(String fieldName,
					Reader reader) {
				Tokenizer tokenizer = new WhitespaceTokenizer(reader);
				// TokenStream stream = ignoreCase ? new
				// LowerCaseFilter(tokenizer) : tokenizer;
				TokenStream stream = new LowerCaseFilter(tokenizer);
				return new TokenStreamComponents(tokenizer, stream);
			}
		};
		IKSynonymParser parser = new IKSynonymParser(true, false, analyzer);
		DBCollection coll = db.getCollection(buildCollectionName(configuration.getDictSynonym(), dictSerial));
		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()) {
				DBObject doc = cursor.next();
				Object text = doc.get("text");
				if (null == text)
					continue;
				parser.addInternal(text.toString());
			}
			_SynonymDict = parser.build();
			if (_SynonymDict.maxHorizontalContext < 1) {
				parser.addInternal("同义词单元=>同义词单元"); // 至少包含一个同义词词典才能正常加载
				_SynonymDict = parser.build();
			}
		} catch (Exception e) {
			return false;
		} finally {
			cursor.close();
		}
		return true;
	}
}
