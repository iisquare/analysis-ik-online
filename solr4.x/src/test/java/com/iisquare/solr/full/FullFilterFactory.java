package com.iisquare.solr.full;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class FullFilterFactory extends TokenFilterFactory {

	private Boolean isSpliteLetter, isSpliteDigit, isUseWhitespace;

	protected FullFilterFactory(Map<String, String> args) {
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
	public TokenStream create(TokenStream input) {
		return new FullFilter(input, isSpliteLetter, isSpliteDigit,
				isUseWhitespace);
	}

}
