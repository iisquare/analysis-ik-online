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
		String useSmart = settings.get("useSmart");
		String useArabic = settings.get("useArabic");
		String useEnglish = settings.get("useEnglish");
		String useSynonym = settings.get("useSynonym");
		analyzer = new IKAnalyzer(dictSerial.trim(), useSynonym.equals("true"),
				useSmart.equals("true"), useArabic.equals("true"), useEnglish.equals("true"));
	}

	public static IKAnalyzerProvider enhanceIndexerForIndex(IndexSettings indexSettings, Environment env, String name, Settings settings) {
		IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
		provider.analyzer.setDictSerial("suggest");
		provider.analyzer.setUseSmart(true);
		provider.analyzer.setUseArabic(false);
		provider.analyzer.setUseEnglish(false);
		provider.analyzer.setUseSynonym(true);
		return provider;
	}
	
	public static IKAnalyzerProvider enhanceIndexerForQuery(IndexSettings indexSettings, Environment env, String name, Settings settings) {
		IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
		provider.analyzer.setDictSerial("suggest");
		provider.analyzer.setUseSmart(true);
		provider.analyzer.setUseArabic(false);
		provider.analyzer.setUseEnglish(false);
		provider.analyzer.setUseSynonym(false);
		return provider;
	}
	
	public static IKAnalyzerProvider enhanceQuerierForIndex(IndexSettings indexSettings, Environment env, String name, Settings settings) {
		IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
		provider.analyzer.setUseSmart(false);
		provider.analyzer.setUseArabic(true);
		provider.analyzer.setUseEnglish(true);
		provider.analyzer.setUseSynonym(false);
		return provider;
	}
	
	public static IKAnalyzerProvider enhanceQuerierForQuery(IndexSettings indexSettings, Environment env, String name, Settings settings) {
		IKAnalyzerProvider provider = new IKAnalyzerProvider(indexSettings, env, name, settings);
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
