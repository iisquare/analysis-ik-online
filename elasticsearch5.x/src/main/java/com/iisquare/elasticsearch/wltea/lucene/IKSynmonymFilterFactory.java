package com.iisquare.elasticsearch.wltea.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import com.iisquare.elasticsearch.wltea.dic.Dictionary;

public class IKSynmonymFilterFactory extends AbstractTokenFilterFactory {

	private String dictSerial = null;

	public String getDictSerial() {
		return dictSerial;
	}

	public void setDictSerial(String dictSerial) {
		this.dictSerial = dictSerial;
	}

	public IKSynmonymFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
		super(indexSettings, name, settings);
		String dictSerial = settings.get("dictSerial");
		if(null != dictSerial) this.dictSerial = dictSerial.trim();
	}

	@Override
	public TokenStream create(TokenStream tokenStream) {
		return new SynonymFilter(tokenStream, Dictionary.getSingleton(dictSerial).getSynonymDict(), true);
	}
}
