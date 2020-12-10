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
package com.iisquare.elasticsearch.wltea.dic;

import com.iisquare.elasticsearch.wltea.cfg.Configuration;
import com.iisquare.elasticsearch.wltea.lucene.IKSynonymParser;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.FileUtil;
import com.iisquare.elasticsearch.wltea.util.HttpUtil;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.elasticsearch.common.logging.Loggers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 词典管理类,单子模式
 */
public class Dictionary {
    private static HashMap<String, Dictionary> singletonMap = new HashMap<String, Dictionary>(); // 词典单子实例
    final Logger logger = Loggers.getLogger(getClass(), getClass().getSimpleName());
    private DictSegment _MainDict;// 主词典对象
    private DictSegment _StopWordDict; // 停止词词典
    private DictSegment _QuantifierDict; // 量词词典
    private SynonymMap _SynonymDict; // 同义词词典
    private String dictSerial; // 词典编号（多词典支持）
    private Configuration configuration; // 配置参数

    private Dictionary(String dictSerial) {
        this.dictSerial = dictSerial;
        configuration = Configuration.getInstance();
        LinkedHashMap<String, Boolean> map = reload(new String[]{"stopword", "quantifier", "word", "synonym"});
        if (!map.get("status")) {
            throw new RuntimeException("init dictionary error:" + configuration.toString());
        }
    }

    /**
     * 词典初始化 由于IK Analyzer的词典采用Dictionary类的静态方法进行词典初始化
     * 只有当Dictionary类被实际调用时，才会开始载入词典， 这将延长首次分词操作的时间 该方法提供了一个在应用加载阶段就初始化字典的手段
     */
    public static void initial() {
        String[] dictSerials = Configuration.getInstance().getInitSerials();
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
        if (null == dictSerial) dictSerial = "";
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

    public static String buildCollectionName(String collectionName,
                                             String dictSerial) {
        if (null == dictSerial || "".equals(dictSerial))
            return collectionName;
        return collectionName + "." + dictSerial;
    }

    public synchronized LinkedHashMap<String, Boolean> reload(String[] dicts) {
        LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();
        boolean status = true;
        try {
            Boolean bool;
            for (int i = 0; i < dicts.length; i++) {
                String dict = dicts[i];
                if (status) {
                    switch (dict) {
                        case "quantifier":
                            bool = loadQuantifierDict();
                            break;
                        case "stopword":
                            bool = loadStopWordDict();
                            break;
                        case "synonym":
                            bool = loadSynonymDict();
                            // bool &= IKSynonymFilter.updateDict(_SynonymDict,
                            // true);
                            break;
                        case "word":
                            bool = loadMainDict();
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
            logger.error(e.getMessage(), e);
            status = false;
        }
        map.put("status", status);
        return map;
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

    /**
     * 加载主词典及扩展词典
     */
    private boolean loadMainDict() {
        _MainDict = this.loadDictSegment(dictSerial, configuration.getWord());
        return null != _MainDict;
    }

    /**
     * 加载用户扩展的停止词词典
     */
    private boolean loadStopWordDict() {
        _StopWordDict = this.loadDictSegment(dictSerial, configuration.getStopword());
        return null != _StopWordDict;
    }

    /**
     * 加载量词词典
     */
    private boolean loadQuantifierDict() {
        _QuantifierDict = this.loadDictSegment(dictSerial, configuration.getQuantifier());
        return null != _QuantifierDict;
    }

    private String content(String dictSerial, String dictType) {
        String url = configuration.getUrl();
        if (DPUtil.empty(url)) url = "./";
        if (url.startsWith("http")) {
            url += "?catalogue=" + dictSerial + "&type=" + dictType;
            return HttpUtil.requestGet(url);
        } else {
            if (!url.endsWith("/")) url += "/";
            url += dictSerial + "-" + dictType + ".dict";
            return FileUtil.getContent(url);
        }
    }

    private DictSegment loadDictSegment(String dictSerial, String dictType) {
        String content = content(dictSerial, dictType);
        if (null == content) return null;
        DictSegment dictSegment = new DictSegment((char) 0);
        for (String line : DPUtil.explode(content, "[\r\n]+", " ", true)) {
            dictSegment.fillSegment(line.toLowerCase().toCharArray());
        }
        return dictSegment;
    }

    private boolean loadSynonymDict() throws Exception {
        String content = content(dictSerial, configuration.getSynonym());
        if (null == content) return false;
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new WhitespaceTokenizer();
                // TokenStream stream = ignoreCase ? new
                // LowerCaseFilter(tokenizer) : tokenizer;
                TokenStream stream = new LowerCaseFilter(tokenizer);
                return new TokenStreamComponents(tokenizer, stream);
            }
        };
        IKSynonymParser parser = new IKSynonymParser(true, false, analyzer);
        for (String line : DPUtil.explode(content, "\n", " ", true)) {
            parser.addInternal(line);
        }
        try {
            _SynonymDict = parser.build();
            if (_SynonymDict.maxHorizontalContext < 1) {
                parser.addInternal("同义词单元=>同义词单元"); // 至少包含一个同义词词典才能正常加载
                _SynonymDict = parser.build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
