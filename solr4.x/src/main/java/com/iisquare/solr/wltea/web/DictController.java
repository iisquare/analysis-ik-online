package com.iisquare.solr.wltea.web;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.solr.cloud.ZkController;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.core.CoreContainer;

import com.iisquare.solr.wltea.dao.DaoBase;
import com.iisquare.solr.wltea.dic.Dictionary;
import com.iisquare.solr.wltea.lucene.IKAnalyzer;
import com.iisquare.solr.wltea.service.DictService;
import com.iisquare.solr.wltea.util.ApiUtil;
import com.iisquare.solr.wltea.util.DPUtil;
import com.iisquare.solr.wltea.util.HttpUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DictController extends ControllerBase {

	public DictService dictService;

	@Override
	public Object init() {
		dictService = new DictService();
		return super.init();
	}

	@Override
	public Object destroy(Object actionVal) {
		// MongoUtil.close();
		return super.destroy(actionVal);
	}

	public Object debugAction() throws Exception {
		boolean debug = "1".equals(get("debug"));
		DaoBase.isDebug = debug;
		return displayText(ApiUtil.echoMessage(request, 0, "ok", debug));
	}

	public Object demoAction() throws Exception {
		String keyword = get("keyword");
		String content = get("content");
		String parserOperator = get("parserOperator");
		int pageSize = DPUtil.parseInt(get("pageSize"));
		String dictSerial = get("dictSerial");
		boolean useSmartIndex = !DPUtil.empty(get("useSmartIndex"));
		boolean useArabicIndex = !DPUtil.empty(get("useArabicIndex"));
		boolean useEnglishIndex = !DPUtil.empty(get("useEnglishIndex"));
		boolean useSynonymIndex = !DPUtil.empty(get("useSynonymIndex"));
		boolean useSmartQuery = !DPUtil.empty(get("useSmartQuery"));
		boolean useArabicQuery = !DPUtil.empty(get("useArabicQuery"));
		boolean useEnglishQuery = !DPUtil.empty(get("useEnglishQuery"));
		boolean useSynonymQuery = !DPUtil.empty(get("useSynonymQuery"));

		if (pageSize < 1)
			pageSize = 5;
		String fieldName = "text";
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();

		IKAnalyzer indexAnalyzer = new IKAnalyzer(dictSerial, useSynonymIndex,
				useSmartIndex, useArabicIndex, useEnglishIndex);
		IKAnalyzer queryAnalyzer = new IKAnalyzer(dictSerial, useSynonymQuery,
				useSmartQuery, useArabicQuery, useEnglishQuery);
		Directory directory = null;
		IndexWriter iwriter = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		try {
			// 建立内存索引对象
			directory = new RAMDirectory();
			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LATEST,
					indexAnalyzer);
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
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
			map.put("totalHits", topDocs.totalHits);
			// 输出结果
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			ArrayList<String> list = new ArrayList<>();
			int length = scoreDocs.length;
			for (int i = 0; i < length; i++) {
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				list.add(targetDoc.get(fieldName));
			}
			map.put("docs", list);
		} catch (Exception e) {
			map = null;
			e.printStackTrace();
		} finally {
			if (ireader != null) {
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			indexAnalyzer.close();
			queryAnalyzer.close();
		}
		if (null == map)
			return displayText(ApiUtil.echoMessage(request, 1500, "error",
					keyword));
		return displayText(ApiUtil.echoMessage(request, 0, keyword, map));
	}

	public Object indexAction() throws Exception {
		String keyword = get("keyword");
		if (DPUtil.empty(keyword)) {
			return displayText(ApiUtil.echoMessage(request, 0, "ok",
					DPUtil.getCurrentSeconds()));
		}
		String dictSerial = get("dictSerial");
		boolean useSmart = !DPUtil.empty(get("useSmart"));
		boolean useArabic = !DPUtil.empty(get("useArabic"));
		boolean useEnglish = !DPUtil.empty(get("useEnglish"));
		boolean useSynonym = !DPUtil.empty(get("useSynonym"));
		Analyzer analyzer = new IKAnalyzer(dictSerial, useSynonym, useSmart,
				useArabic, useEnglish);
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
			e.printStackTrace();
		} finally {
			if (ts != null) {
				try {
					ts.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			analyzer.close();
		}
		if (null == list)
			return displayText(ApiUtil.echoMessage(request, 1500, "error",
					keyword));
		return displayText(ApiUtil.echoMessage(request, 0, keyword, list));
	}

	public Object runCommandAction() throws Exception {
		String cmd = get("cmd");
		String dictSerial = get("dictSerial");
		JSONObject jsonObject = DPUtil.parseJSON(cmd);
		if (DPUtil.empty(jsonObject)) {
			return displayText(ApiUtil.echoMessage(request, 1001, "命令无法识别",
					null));
		}
		LinkedHashMap<String, Object> map = dictService.runCommand(dictSerial,
				jsonObject);
		map.put("command", cmd);
		if (DPUtil.empty(map.get("status"))) {
			return displayText(ApiUtil.echoMessage(request, 1500,
					"部分或全部指令执行失败", map));
		} else {
			return displayText(ApiUtil.echoMessage(request, 0, "执行成功", map));
		}
	}

	public Object reloadAction() throws Exception {
		String dictSerial = get("dictSerial");
		Object forceNode = get("forceNode");
		String[] dicts = getArray("dicts");
		if (DPUtil.empty(dicts)) {
			return displayText(ApiUtil.echoMessage(request, 1001, "参数错误", null));
		}
		if (!DPUtil.empty(forceNode)) {
			LinkedHashMap<String, Boolean> map = Dictionary.getSingleton(
					dictSerial).reload(dicts);
			if (map.get("status")) {
				return displayText(ApiUtil.echoMessage(request, 0, "载入成功", map));
			} else {
				return displayText(ApiUtil.echoMessage(request, 1500, "载入失败",
						map));
			}
		}
		if (params.containsKey("forceNode")) {
			return displayText(ApiUtil.echoMessage(request, 1001,
					"请勿将forceNode设置为空值", null));
		}
		CoreContainer cores = (CoreContainer) request
				.getAttribute("org.apache.solr.CoreContainer");
		if (cores == null) {
			return displayText(ApiUtil.echoMessage(request, 1500,
					"Missing request attribute org.apache.solr.CoreContainer.",
					null));
		}
		ZkController zkController = cores.getZkController();
		SolrZkClient zkClient = null;
		if (null != zkController) {
			zkClient = zkController.getZkClient();
		}
		if (null == zkClient || zkClient.isClosed()) {
			return displayText(ApiUtil.echoMessage(request, 1500,
					"zkClient 连接丢失", null));
		}
		String path = "/live_nodes";
		List<String> list = null;
		try {
			if (!zkClient.exists(path, true)) {
				return displayText(ApiUtil.echoMessage(request, 1500,
						"存活节点不存在", null));
			}
			list = zkClient.getChildren(path, null, true);
		} catch (Exception e) {
			e.printStackTrace();
			return displayText(ApiUtil.echoMessage(request, 1500, "读取节点异常",
					null));
		}
		if (null == list) {
			return displayText(ApiUtil.echoMessage(request, 1500, "未读取到任何存活节点",
					null));
		}
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		boolean status = true;
		String queryString = request.getQueryString();
		if (!DPUtil.empty(queryString)) {
			queryString += "&";
		}
		queryString += "forceNode=1";
		for (String nodeName : list) {
			String url = "http://" + nodeName.split("_")[0] + appPath
					+ controllerName + "/" + actionName + "/?" + queryString;
			JSONObject result = DPUtil.parseJSON(HttpUtil.requestGet(url));
			if (null == result) {
				status = false;
			} else {
				status &= 0 == DPUtil.parseInt(result.get("code"));
			}
			map.put(nodeName, result);
		}
		if (status) {
			return displayText(ApiUtil.echoMessage(request, 0, "集群执行载入成功", map));
		} else {
			return displayText(ApiUtil.echoMessage(request, 1500, "集群执行载入失败",
					map));
		}
	}

	public Object listAction() throws Exception {
		String dictSerial = get("dictSerial");
		LinkedHashMap<String, Object> returnMap = dictService.search(
				dictSerial, params);
		if (null == returnMap) {
			return displayText(ApiUtil.echoMessage(request, 1002, "类型错误", null));
		}
		return displayJSON(returnMap);
	}

	public Object updateAction() throws Exception {
		Object text = get("text");
		if (DPUtil.empty(text)) {
			return displayText(ApiUtil.echoMessage(request, 1001, "参数错误", null));
		}
		int result = dictService.updateById(get("type"), get("dictSerial"),
				get("id"), text,
				params.containsKey("identity") ? get("identity") : null);
		if (result >= 0) {
			return displayText(ApiUtil.echoMessage(request, 0, "更新成功", result));
		}
		return displayText(ApiUtil.echoMessage(request, 1500, "更新失败", result));
	}

	public Object deleteAction() throws Exception {
		int result = dictService.deleteByIds(get("type"), get("dictSerial"),
				(Object[]) getArray("ids"));
		if (result >= 0) {
			return displayText(ApiUtil.echoMessage(request, 0, "删除成功", result));
		}
		return displayText(ApiUtil.echoMessage(request, 1500, "删除失败", result));
	}

	public Object insertAction() throws Exception {
		String dictSerial = get("dictSerial");
		Object[] texts = getArray("texts");
		if (DPUtil.empty(texts)) {
			return displayText(ApiUtil.echoMessage(request, 1001, "参数错误", null));
		}
		if (dictService.exist(get("type"), dictSerial, texts) > 0) {
			return displayText(ApiUtil
					.echoMessage(request, 1005, "信息已存在", null));
		}
		int time = DPUtil.getCurrentSeconds();
		List<DBObject> docs = new ArrayList<>();
		for (Object text : texts) {
			BasicDBObject doc = new BasicDBObject().append("text", text)
					.append("identity", get("identity"))
					.append("time_create", time).append("time_update", time);
			docs.add(doc);
		}
		docs = dictService.insert(get("type"), dictSerial, docs);
		if (null == docs) {
			return displayText(ApiUtil.echoMessage(request, 1002, "类型错误", null));
		}
		String id = null;
		if (1 == docs.size()) {
			id = docs.get(0).get(dictService.getIdName()).toString();
		}
		return displayText(ApiUtil.echoMessage(request, 0, "添加成功", id));
	}
}
