package com.iisquare.solr.wltea.lucene;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.CharsRefBuilder;

public class IKSynonymParser extends SynonymMap.Parser {
	private final boolean expand;

	public IKSynonymParser(boolean dedup, boolean expand, Analyzer analyzer) {
		super(dedup, analyzer);
		this.expand = expand;
	}

	@Override
	public void parse(Reader in) throws IOException, ParseException {
		throw new IOException("not support,use SolrSynonymParser only");
	}

	/**
	 * "aaa => a11", // 将aaa替换为111 "bbb => b11 b22", //
	 * 将bbb替换为b11，将bbb之后的位置替换为b22，依次顺延 "ccc => c11,c22", // 将ccc替换为c11和c22
	 * "ddd,d11,d22", // 将d11或d22替换为ddd "e11,e22 => eee", // 将e11或e22替换为eee
	 */
	public void addInternal(String line) throws IOException {
		if (line.length() == 0 || line.charAt(0) == '#') {
			return; // ignore empty lines and comments
		}

		CharsRef inputs[];
		CharsRef outputs[];

		// TODO: we could process this more efficiently.
		String sides[] = split(line, "=>");
		if (sides.length > 1) { // explicit mapping
			if (sides.length != 2) {
				throw new IllegalArgumentException(
						"more than one explicit mapping specified on the same line");
			}
			String inputStrings[] = split(sides[0], ",");
			inputs = new CharsRef[inputStrings.length];
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = analyze(unescape(inputStrings[i]).trim(),
						new CharsRefBuilder());
			}

			String outputStrings[] = split(sides[1], ",");
			outputs = new CharsRef[outputStrings.length];
			for (int i = 0; i < outputs.length; i++) {
				outputs[i] = analyze(unescape(outputStrings[i]).trim(),
						new CharsRefBuilder());
			}
		} else {
			String inputStrings[] = split(line, ",");
			inputs = new CharsRef[inputStrings.length];
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = analyze(unescape(inputStrings[i]).trim(),
						new CharsRefBuilder());
			}
			if (expand) {
				outputs = inputs;
			} else {
				outputs = new CharsRef[1];
				outputs[0] = inputs[0];
			}
		}

		// currently we include the term itself in the map,
		// and use includeOrig = false always.
		// this is how the existing filter does it, but its actually a bug,
		// especially if combined with ignoreCase = true
		// System.out.println("++++++++++++++");
		// System.out.println(line);
		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < outputs.length; j++) {
				// System.out.println("----");
				// System.out.println("input[" + i + "]:" + inputs[i]);
				// System.out.println("outputs[" + j + "]:" + outputs[j]);
				add(inputs[i], outputs[j], false);
			}
		}
	}

	public int countWords(CharsRef chars) {
		int wordCount = 1;
		int upto = chars.offset;
		final int limit = chars.offset + chars.length;
		while (upto < limit) {
			if (chars.chars[upto++] == SynonymMap.WORD_SEPARATOR) {
				wordCount++;
			}
		}
		return wordCount;
	}

	private static String[] split(String s, String separator) {
		ArrayList<String> list = new ArrayList<>(2);
		StringBuilder sb = new StringBuilder();
		int pos = 0, end = s.length();
		while (pos < end) {
			if (s.startsWith(separator, pos)) {
				if (sb.length() > 0) {
					list.add(sb.toString());
					sb = new StringBuilder();
				}
				pos += separator.length();
				continue;
			}

			char ch = s.charAt(pos++);
			if (ch == '\\') {
				sb.append(ch);
				if (pos >= end)
					break; // ERROR, or let it go?
				ch = s.charAt(pos++);
			}

			sb.append(ch);
		}

		if (sb.length() > 0) {
			list.add(sb.toString());
		}

		return list.toArray(new String[list.size()]);
	}

	private String unescape(String s) {
		if (s.indexOf("\\") >= 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				char ch = s.charAt(i);
				if (ch == '\\' && i < s.length() - 1) {
					sb.append(s.charAt(++i));
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}
		return s;
	}

}
