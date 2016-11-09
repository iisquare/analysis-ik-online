package com.iisquare.solr.full;

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

public class FullTokenizerFactory extends TokenizerFactory {
	private Boolean isSpliteLetter, isSpliteDigit, isUseWhitespace;

	public FullTokenizerFactory(Map<String, String> args) {
		super(args);
		String spliteLetter = args.get("spliteLetter");
		String spliteDigit = args.get("spliteDigit");
		String useWhitespace = args.get("useWhitespace");
		if (null != spliteLetter)
			isSpliteLetter = "true".equalsIgnoreCase(spliteLetter);
		if (null != spliteDigit)
			isSpliteDigit = "true".equalsIgnoreCase(spliteDigit);
		if (null != useWhitespace)
			isUseWhitespace = "true".equalsIgnoreCase(useWhitespace);
	}

	@Override
	public Tokenizer create(AttributeFactory factory, Reader input) {
		FullTokenizer tokenizer = new FullTokenizer(factory, input);
		if (null != isSpliteLetter)
			tokenizer.setSpliteLetter(isSpliteLetter);
		if (null != isSpliteDigit)
			tokenizer.setSpliteDigit(isSpliteDigit);
		if (null != isUseWhitespace)
			tokenizer.setUseWhitespace(isUseWhitespace);
		return tokenizer;
	}

}
