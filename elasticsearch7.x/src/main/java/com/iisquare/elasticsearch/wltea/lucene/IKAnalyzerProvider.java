package com.iisquare.elasticsearch.wltea.lucene;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

public class IKAnalyzerProvider extends AbstractIndexAnalyzerProvider<IKAnalyzer> {

    private final IKAnalyzer analyzer;

    public IKAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        String dictSerial = settings.get("dictSerial");
        String useSmart = settings.get("useSmart", "");
        String useArabic = settings.get("useArabic", "");
        String useEnglish = settings.get("useEnglish", "");
        String useSynonym = settings.get("useSynonym", "");
        analyzer = new IKAnalyzer(null == dictSerial ? null : dictSerial.trim(),
                useSynonym.equals("true"), useSmart.equals("true"), useArabic.equals("true"), useEnglish.equals("true"));
    }

    /**
     * 用于联想匹配的索引
     */
    public static IKAnalyzerProvider enhanceIndexerForIndex(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
        provider.analyzer.setDictSerial("suggest");
        provider.analyzer.setUseSmart(false);
        provider.analyzer.setUseArabic(false);
        provider.analyzer.setUseEnglish(false);
        provider.analyzer.setUseSynonym(true);
        return provider;
    }

    /**
     * 用于联想匹配的检索
     */
    public static IKAnalyzerProvider enhanceIndexerForQuery(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
        provider.analyzer.setDictSerial("suggest");
        provider.analyzer.setUseSmart(true);
        provider.analyzer.setUseArabic(false);
        provider.analyzer.setUseEnglish(false);
        provider.analyzer.setUseSynonym(false);
        return provider;
    }

    /**
     * 用于智能分词的索引和search_quote_analyzer的检索
     */
    public static IKAnalyzerProvider enhanceQuerierForIndex(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
        provider.analyzer.setDictSerial("main");
        provider.analyzer.setUseSmart(false);
        provider.analyzer.setUseArabic(true);
        provider.analyzer.setUseEnglish(true);
        provider.analyzer.setUseSynonym(false);
        return provider;
    }

    /**
     * 用于智能分词的检索，对检索关键词进行同义词处理比存储同义词索引的适用性要好
     */
    public static IKAnalyzerProvider enhanceQuerierForQuery(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
        provider.analyzer.setDictSerial("main");
        provider.analyzer.setUseSmart(true);
        provider.analyzer.setUseArabic(true);
        provider.analyzer.setUseEnglish(true);
        provider.analyzer.setUseSynonym(true);
        return provider;
    }

    @Override
    public IKAnalyzer get() {
        return this.analyzer;
    }
}
