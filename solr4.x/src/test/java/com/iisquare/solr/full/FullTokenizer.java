package com.iisquare.solr.full;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.analysis.util.CharacterUtils.CharacterBuffer;

public class FullTokenizer extends Tokenizer {

	int offset = 0, dataLen = 0;
	private static final int IO_BUFFER_SIZE = 4096;

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

	private final CharacterUtils charUtils;
	private final CharacterBuffer ioBuffer = CharacterUtils
			.newCharacterBuffer(IO_BUFFER_SIZE);

	private boolean isSpliteLetter = false, isSpliteDigit = false,
			isUseWhitespace = false;

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

	public FullTokenizer(Reader input) {
		super(input);
		charUtils = CharacterUtils.getInstance();
	}

	public FullTokenizer(AttributeFactory factory, Reader input) {
		super(factory, input);
		charUtils = CharacterUtils.getInstance();
	}

	protected boolean isTokenChar(int ch) {
		return isUseWhitespace || !Character.isWhitespace(ch);
	}

	protected int normalize(int ch) {
		return ch;
	}

	@Override
	public final boolean incrementToken() throws IOException {
		clearAttributes();

		if (0 == offset) {
			charUtils.fill(ioBuffer, input);
			dataLen = ioBuffer.getLength();
		}

		int start; // 当前词汇单元开始位置
		int ch;
		char[] buffer = ioBuffer.getBuffer();
		do { // 过滤非词汇单元
			if (offset >= dataLen)
				return false; // 没有可处理数据
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

	@Override
	public final void end() throws IOException {
		super.end();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		offset = 0;
		dataLen = 0;
		ioBuffer.reset(); // make sure to reset the IO buffer!!
	}
}