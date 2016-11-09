package com.iisquare.solr.test;

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

public class TestCase {

	public static void main(String[] args) {
		char[] chs = "北".toCharArray();
		for (int i = 0; i < chs.length; i++) {
			char ch = chs[i];
			int codePoint = ch;
			System.out.println("----------" + ch + "-----------------");
			System.out.println("isValidCodePoint:"
					+ Character.isValidCodePoint(codePoint));
			System.out.println("isBmpCodePoint:"
					+ Character.isBmpCodePoint(codePoint));
			System.out.println("isSupplementaryCodePoint:"
					+ Character.isSupplementaryCodePoint(codePoint));
			System.out.println("isHighSurrogate:"
					+ Character.isHighSurrogate((char) codePoint));
			System.out.println("isLowSurrogate:"
					+ Character.isLowSurrogate((char) codePoint));
			System.out.println("isSurrogate:"
					+ Character.isSurrogate((char) codePoint));
			System.out.println("isLowerCase:"
					+ Character.isLowerCase(codePoint));
			System.out.println("isUpperCase:"
					+ Character.isUpperCase(codePoint));
			System.out.println("isTitleCase:"
					+ Character.isTitleCase(codePoint));
			System.out.println("isDigit:" + Character.isDigit(codePoint));
			System.out.println("isDefined:" + Character.isDefined(codePoint));
			System.out.println("isLetter:" + Character.isLetter(codePoint));
			System.out.println("isLetterOrDigit:"
					+ Character.isLetterOrDigit(codePoint));
			System.out.println("isAlphabetic:"
					+ Character.isAlphabetic(codePoint));
			System.out.println("isIdeographic:"
					+ Character.isIdeographic(codePoint));
			System.out.println("isJavaIdentifierStart:"
					+ Character.isJavaIdentifierStart(codePoint));
			System.out.println("isJavaIdentifierPart:"
					+ Character.isJavaIdentifierPart(codePoint));
			System.out.println("isUnicodeIdentifierStart:"
					+ Character.isUnicodeIdentifierStart(codePoint));
			System.out.println("isUnicodeIdentifierPart:"
					+ Character.isUnicodeIdentifierPart(codePoint));
			System.out.println("isIdentifierIgnorable:"
					+ Character.isIdentifierIgnorable(codePoint));
			System.out.println("isSpaceChar:"
					+ Character.isSpaceChar(codePoint));
			System.out.println("isWhitespace:"
					+ Character.isWhitespace(codePoint));
			System.out.println("isISOControl:"
					+ Character.isISOControl(codePoint));
			System.out.println("isMirrored:" + Character.isMirrored(codePoint));
		}

	}

}
