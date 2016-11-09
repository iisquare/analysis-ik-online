package com.iisquare.solr.full;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.AttributeSource;

public class FullFilter extends TokenFilter {

	private boolean isSpliteLetter = false, isSpliteDigit = false,
			isUseWhitespace = false;
	// 词元文本属性
	private final CharTermAttribute termAtt;
	// 词元位移属性
	private final OffsetAttribute offsetAtt;
	// 定义一个状态
	private AttributeSource.State current = null;

	int offset = 0;
	private final CharacterUtils charUtils;

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

	public FullFilter(TokenStream input, Boolean isSpliteLetter,
			Boolean isSpliteDigit, Boolean isUseWhitespace) {
		super(input);
		if (null != isSpliteLetter)
			this.isSpliteLetter = isSpliteLetter;
		if (null != isSpliteDigit)
			this.isSpliteDigit = isSpliteDigit;
		if (null != isUseWhitespace)
			this.isUseWhitespace = isUseWhitespace;
		offsetAtt = addAttribute(OffsetAttribute.class);
		termAtt = addAttribute(CharTermAttribute.class);
		charUtils = CharacterUtils.getInstance();
	}

	protected boolean isTokenChar(int ch) {
		return isUseWhitespace || !Character.isWhitespace(ch);
	}

	protected int normalize(int ch) {
		return ch;
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		if (null == current) {
			if (!input.incrementToken())
				return false;
			current = captureState();
		} else {
			restoreState(current);
		}
		int ch, start;
		int dataLen = termAtt.length(); // 当前termAtt的数据长度
		char[] buffer = termAtt.buffer(); // 当前termAtt的数据数组
		termAtt.setEmpty(); // 清空当前termAtt
		do { // 过滤非词汇单元
			if (offset >= dataLen) {
				offset = 0;
				current = null;
				return incrementToken(); // 当前termAtt已处理完
			}
			start = offset;
			ch = charUtils.codePointAt(buffer, offset, dataLen);
			offset += Character.charCount(ch);
			if (isTokenChar(ch))
				break;
		} while (true);
		termAtt.append((char) ch);
		if (CharacterUtil.isDigit(ch)) { // 第一个词汇单元为数字
			if (isSpliteDigit) { // 拆分数值
				offsetAtt
						.setOffset(correctOffset(start), correctOffset(offset));
				return true;
			}
			while (offset < dataLen) {
				ch = charUtils.codePointAt(buffer, offset, dataLen);
				if (!CharacterUtil.isDigit(ch))
					break;
				termAtt.append((char) ch);
				offset += Character.charCount(ch);
			}
		} else if (CharacterUtil.isLetter(ch)) { // 第一个词汇单元为字母
			if (isSpliteLetter) { // 拆分字母
				offsetAtt
						.setOffset(correctOffset(start), correctOffset(offset));
				return true;
			}
			while (offset < dataLen) {
				ch = charUtils.codePointAt(buffer, offset, dataLen);
				if (!CharacterUtil.isLetter(ch))
					break;
				termAtt.append((char) ch);
				offset += Character.charCount(ch);
			}
		}
		offsetAtt.setOffset(correctOffset(start), correctOffset(offset));
		return true;
	}

	private int correctOffset(int offset) {
		return offsetAtt.startOffset() + offset;
	}

	@Override
	public final void end() throws IOException {
		super.end();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		offset = 0;
		current = null;
	}
}
