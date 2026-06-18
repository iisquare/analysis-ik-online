package com.iisquare.elasticsearch.wltea.lucene;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;

public class IKTokenizerFactory extends AbstractTokenizerFactory {

    private String dictSerial = null; // 词典编号
    private boolean useSmart = false; // 是否使用只能分词，true-粗粒度，false-细粒度
    private boolean useArabic = false; // 是否拆分阿拉伯数字
    private boolean useEnglish = false; // 是否拆分字母
    private boolean useSynonym = false; // 是否使用同义词过滤器

    public IKTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, settings, name);
        String dictSerial = settings.get("dictSerial");
        if (null != dictSerial) this.dictSerial = dictSerial.trim();
        String useSmart = settings.get("useSmart");
        if (null != useSmart) this.useSmart = useSmart.equals("true");
        String useArabic = settings.get("useArabic");
        if (null != useArabic) this.useArabic = useArabic.equals("true");
        String useEnglish = settings.get("useEnglish");
        if (null != useEnglish) this.useEnglish = dictSerial.equals("true");
        String useSynonym = settings.get("useSynonym");
        if (null != useSynonym) this.useSynonym = useSynonym.equals("true");
    }

    public static IKTokenizerFactory getDefault(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new IKTokenizerFactory(indexSettings, env, name, settings);
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

    public Tokenizer create() {
        return new IKTokenizer(dictSerial, useSmart, useArabic, useEnglish);
    }
}
