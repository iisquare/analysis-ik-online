/**
 * IK 中文分词  版本 5.0.1
 * IK Analyzer release 5.0.1
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
package com.iisquare.elasticsearch.wltea.lucene;

import com.iisquare.elasticsearch.wltea.dic.Dictionary;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;

/**
 * IK分词器，Lucene Analyzer接口实现 兼容Lucene 4.0版本
 */
public final class IKAnalyzer extends Analyzer {

    private String dictSerial; // 词典编号
    private boolean useSmart; // 是否使用智能分词
    private boolean useArabic; // 是否拆分阿拉伯数字
    private boolean useEnglish; // 是否拆分字母
    private boolean useSynonym; // 是否使用同义词过滤器

    /**
     * IK分词器Lucene Analyzer接口实现类
     *
     * 默认采用非智能分词算法
     */
    public IKAnalyzer() {
        this(null, false, false, false, false);
    }

    /**
     * IK分词器Lucene Analyzer接口实现类
     *
     * @param useSmart
     *            当为true时，分词器进行智能切分
     */
    public IKAnalyzer(String dictSerial, boolean useSynonym, boolean useSmart,
                      boolean useArabic, boolean useEnglish) {
        super();
        this.dictSerial = dictSerial;
        this.useSynonym = useSynonym;
        this.useSmart = useSmart;
        this.useArabic = useArabic;
        this.useEnglish = useEnglish;

    }

    public String getDictSerial() {
        return dictSerial;
    }

    public void setDictSerial(String dictSerial) {
        this.dictSerial = dictSerial;
    }

    public boolean isUseSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    public boolean isUseArabic() {
        return useArabic;
    }

    public void setUseArabic(boolean useArabic) {
        this.useArabic = useArabic;
    }

    public boolean isUseEnglish() {
        return useEnglish;
    }

    public void setUseEnglish(boolean useEnglish) {
        this.useEnglish = useEnglish;
    }

    public boolean isUseSynonym() {
        return useSynonym;
    }

    public void setUseSynonym(boolean useSynonym) {
        this.useSynonym = useSynonym;
    }

    /**
     * 重载Analyzer接口，构造分词组件
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer _IKTokenizer = new IKTokenizer(dictSerial, useSmart, useArabic, useEnglish);
        TokenStreamComponents components = new TokenStreamComponents(_IKTokenizer);
        if (!useSynonym) return components;
        SynonymGraphFilter filter = new SynonymGraphFilter(components.getTokenStream(),
                Dictionary.getSingleton(dictSerial).getSynonymDict(), true);
        components = new TokenStreamComponents(_IKTokenizer, filter);
        return components;
    }

}
