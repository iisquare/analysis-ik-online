package com.iisquare.solr.wltea.lucene;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import com.iisquare.solr.wltea.dic.Dictionary;

public class IKSynonymFilterFactory extends TokenFilterFactory {

	private String dictSerial = null;

	public String getDictSerial() {
		return dictSerial;
	}

	public void setDictSerial(String dictSerial) {
		this.dictSerial = dictSerial;
	}

	public IKSynonymFilterFactory(Map<String, String> args) {
		super(args);
		if (args.containsKey("dictSerial")) {
			this.setDictSerial(args.get("dictSerial").toString().trim());
		}
	}

	@Override
	public TokenStream create(TokenStream input) {
		return new SynonymFilter(input, Dictionary.getSingleton(dictSerial).getSynonymDict(), true);
	}

}
