package com.iisquare.solr.full;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;

public class FullAnalyzer extends Analyzer {

	private boolean isSpliteLetter, isSpliteDigit, isUseWhitespace;
	private boolean useLowerCase;

	public boolean isSpliteLetter() {
		return isSpliteLetter;
	}

	public void setSpliteLetter(boolean isSpliteLetter) {
		this.isSpliteLetter = isSpliteLetter;
	}

	public boolean isSpliteDigit() {
		return isSpliteDigit;
	}

	public void setSpliteDigit(boolean isSpliteDigit) {
		this.isSpliteDigit = isSpliteDigit;
	}

	public boolean isUseWhitespace() {
		return isUseWhitespace;
	}

	public void setUseWhitespace(boolean isUseWhitespace) {
		this.isUseWhitespace = isUseWhitespace;
	}

	public boolean useLowerCase() {
		return useLowerCase;
	}

	public void setUseSmart(boolean useLowerCase) {
		this.useLowerCase = useLowerCase;
	}

	public FullAnalyzer() {
		this(true, true, false, true);
	}

	public FullAnalyzer(boolean isSpliteLetter, boolean isSpliteDigit,
			boolean isUseWhitespace, boolean useLowerCase) {
		super();
		this.isSpliteLetter = isSpliteLetter;
		this.isSpliteDigit = isSpliteDigit;
		this.isUseWhitespace = isUseWhitespace;
		this.useLowerCase = useLowerCase;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer fullTokenizer = new FullTokenizer(reader);
		if (!useLowerCase)
			return new TokenStreamComponents(fullTokenizer);
		return new TokenStreamComponents(fullTokenizer) {

			@Override
			public TokenStream getTokenStream() {
				// 使用小写过滤器
				return new LowerCaseFilter(super.getTokenStream());
			}

		};
	}

}
