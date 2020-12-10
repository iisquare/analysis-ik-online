package com.iisquare.elasticsearch.handler;

import com.iisquare.elasticsearch.plugin.HandlerBase;
import com.iisquare.elasticsearch.wltea.lucene.IKAnalyzer;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.FileUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;

import java.util.*;

public class DemoHandler extends HandlerBase {

    @Override
    public List<Route> routes() {
        return Arrays.asList(
                new Route(RestRequest.Method.POST, uri("demo"))
        );
    }

    @Override
    public void handleRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
        Map<String, Object> param = param(request);
        String keyword = DPUtil.parseString(param.get("keyword"));
        String content = DPUtil.parseString(param.get("content"));
        String parserOperator = DPUtil.parseString(param.get("parserOperator"));
        int pageSize = DPUtil.parseInt(param.get("pageSize"));
        String dictSerial = DPUtil.parseString(param.get("dictSerial"));
        boolean useSmartIndex = !DPUtil.empty(param.get("useSmartIndex"));
        boolean useArabicIndex = !DPUtil.empty(param.get("useArabicIndex"));
        boolean useEnglishIndex = !DPUtil.empty(param.get("useEnglishIndex"));
        boolean useSynonymIndex = !DPUtil.empty(param.get("useSynonymIndex"));
        boolean useSmartQuery = !DPUtil.empty(param.get("useSmartQuery"));
        boolean useArabicQuery = !DPUtil.empty(param.get("useArabicQuery"));
        boolean useEnglishQuery = !DPUtil.empty(param.get("useEnglishQuery"));
        boolean useSynonymQuery = !DPUtil.empty(param.get("useSynonymQuery"));
        if (pageSize < 1) pageSize = 5;
        String fieldName = "text";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        IKAnalyzer indexAnalyzer = new IKAnalyzer(dictSerial,
                useSynonymIndex, useSmartIndex, useArabicIndex, useEnglishIndex);
        IKAnalyzer queryAnalyzer = new IKAnalyzer(dictSerial,
                useSynonymQuery, useSmartQuery, useArabicQuery, useEnglishQuery);
        Directory directory = null;
        IndexWriter iwriter = null;
        IndexReader ireader = null;
        IndexSearcher isearcher = null;
        try {
            // 建立内存索引对象
            directory = new RAMDirectory();
            // 配置IndexWriterConfig
            IndexWriterConfig iwConfig = new IndexWriterConfig(indexAnalyzer);
            iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            iwriter = new IndexWriter(directory, iwConfig);
            String[] contents = content.split("\n");
            for (String text : contents) { // 写入索引
                Document doc = new Document();
                doc.add(new TextField(fieldName, text, Field.Store.YES));
                iwriter.addDocument(doc);
            }
            iwriter.close();
            // 搜索过程 实例化搜索器
            ireader = DirectoryReader.open(directory);
            isearcher = new IndexSearcher(ireader);
            // 使用QueryParser查询分析器构造Query对象
            QueryParser qp = new QueryParser(fieldName, queryAnalyzer);
            switch (parserOperator) {
                case "and":
                    qp.setDefaultOperator(QueryParser.AND_OPERATOR);
                    break;
                default:
                    qp.setDefaultOperator(QueryParser.OR_OPERATOR);
            }
            Query query = qp.parse(keyword);
            // 搜索相似度最高的5条记录
            TopDocs topDocs = isearcher.search(query, pageSize);
            map.put("totalHits", topDocs.totalHits.value);
            // 输出结果
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            ArrayList<String> list = new ArrayList<>();
            int length = scoreDocs.length;
            for (int i = 0; i < length; i++) {
                Document targetDoc = isearcher.doc(scoreDocs[i].doc);
                list.add(targetDoc.get(fieldName));
            }
            map.put("docs", list);
            message(channel, 0, keyword, map);
        } catch (Exception e) {
            message(channel, 1500, e.getMessage(), keyword);
        } finally {
            FileUtil.close(ireader, directory, indexAnalyzer, queryAnalyzer);
        }
    }
}
