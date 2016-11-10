package com.iisquare.solr.wltea.lucene;

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

public class IKTokenizerFactory extends TokenizerFactory {

	private String dictSerial = null; // 词典编号
	private boolean useSmart = false; // 是否使用细粒度分词
	private boolean useArabic = false; // 是否拆分阿拉伯数字
	private boolean useEnglish = false; // 是否拆分字母
	private boolean useSynonym = false; // 是否使用同义词过滤器

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

	public IKTokenizerFactory(Map<String, String> args) {
		super(args);
		assureMatchVersion();
		if (args.containsKey("dictSerial")) {
			this.setDictSerial(args.get("dictSerial").toString().trim());
		}
		if (args.containsKey("useSmart")) {
			this.setUseSmart(args.get("useSmart").toString().equals("true"));
		}
		if (args.containsKey("useArabic")) {
			this.setUseArabic(args.get("useArabic").toString().equals("true"));
		}
		if (args.containsKey("useEnglish")) {
			this.setUseEnglish(args.get("useEnglish").toString().equals("true"));
		}
		if (args.containsKey("useSynonym")) {
			this.setUseSynonym(args.get("useSynonym").toString().equals("true"));
		}
	}

	@Override
	public Tokenizer create(AttributeFactory factory, Reader input) {
		Tokenizer _IKTokenizer = new IKTokenizer(input, dictSerial, useSmart,
				useArabic, useEnglish);
		return _IKTokenizer;
	}
}
