package com.iisquare.elasticsearch.handler;

import com.iisquare.elasticsearch.plugin.HandlerBase;
import com.iisquare.elasticsearch.wltea.lucene.IKAnalyzer;
import com.iisquare.elasticsearch.wltea.util.ApiUtil;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;

import java.io.StringReader;
import java.util.*;

public class IndexHandler extends HandlerBase {

    @Override
    public List<Route> routes() {
        return Arrays.asList(
                new Route(RestRequest.Method.POST, uri("index"))
        );
    }

    @Override
    public void handleRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
        Map<String, Object> param = param(request);
        String keyword = DPUtil.parseString(param.get("keyword"));
        if (DPUtil.empty(keyword)) {
            text(channel, ApiUtil.echoMessage(0, "ok", DPUtil.getCurrentSeconds()));
            return;
        }
        String dictSerial = DPUtil.parseString(param.get("dictSerial"));
        boolean useSmart = !DPUtil.empty(param.get("useSmart"));
        boolean useArabic = !DPUtil.empty(param.get("useArabic"));
        boolean useEnglish = !DPUtil.empty(param.get("useEnglish"));
        boolean useSynonym = !DPUtil.empty(param.get("useSynonym"));
        Analyzer analyzer = new IKAnalyzer(dictSerial, useSynonym, useSmart, useArabic, useEnglish);
        TokenStream ts = null;
        ArrayList<LinkedHashMap<String, Object>> list = null;
        try {
            ts = analyzer.tokenStream("text", new StringReader(keyword));
            OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);
            ts.reset();
            list = new ArrayList<>();
            while (ts.incrementToken()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("startOffset", offset.startOffset());
                map.put("endOffset", offset.endOffset());
                map.put("term", term.toString());
                map.put("type", type.type());
                list.add(map);
            }
            ts.end();
        } catch (Exception e) {
            list = null;
            logger.error(e.getMessage(), e);
        } finally {
            FileUtil.close(ts, analyzer);
        }
        if (null == list) {
            message(channel, 1500, "error", keyword);
        } else {
            message(channel, 0, keyword, list);
        }
    }
}
