package com.iisquare.elasticsearch.wltea.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.iisquare.elasticsearch.wltea.dao.DictDao;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.MongoUtil;
import com.iisquare.elasticsearch.wltea.util.ValidateUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DictService extends ServiceBase {

	public DictDao dictDao;

	public DictService() {
		dictDao = new DictDao();
	}

	public String getIdName() {
		return dictDao.idName;
	}

	/**
	 * 通过JSON命令处理词典
	 * 
	 * @param cmd {
	 * 		dicName : { // 词典名称
	 * 			insert : [{ // 添加
	 * 				text : "内容，不能为空",
	 *				identity : "自定义标示（分类_ID），null时自定转为空字符串"
	 *			}],
	 *			update : [{ // 修改
	 *				from : { // 至少要设置一个查询条件
	 *					text : "不设置该key时，不作为查询条件",
	 *					identity : "不设置该key时，不作为查询条件"
	 *				},
	 *				to : {
	 *					text : "内容，不能为空",
	 *					identity : "自定义标示（分类_ID），null时自定转为空字符串"
	 *				}
	 *			}],
	 *			delete : [{ // 删除
	 *				text : "不设置该key时，不作为查询条件",
	 *				identity : "不设置该key时，不作为查询条件"
	 *			}]
	 *		}
	 *	}
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Object> runCommand(String dictSerial, LinkedHashMap<?, ?> cmd) {
		String[] dicts = new String[] { "quantifier", "stopword", "synonym", "word" };
		String[] cruds = new String[] { "insert", "update", "delete" };
		boolean status = true;
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("status", status);
		int time = DPUtil.getCurrentSeconds();
		for (String dict : dicts) {
			Object object = cmd.get(dict);
			if (DPUtil.empty(object) || !(object instanceof LinkedHashMap)) continue;
			if (!dictDao.selectTable(dict, dictSerial)) continue;
			Map<?, ?> cmdCrud = (Map<?, ?>) object;
			for (String crud : cruds) {
				object = cmdCrud.get(crud);
				if (DPUtil.empty(object) || !(object instanceof ArrayList)) continue;
				ArrayList<LinkedHashMap<?, ?>> crudArray = (ArrayList<LinkedHashMap<?, ?>> ) object;
				Iterator<LinkedHashMap<?, ?>> iter = crudArray.iterator();
				LinkedHashMap<String, Object> crudMap = new LinkedHashMap<>();
				switch (crud) {
				case "insert": // {"word":{"insert":[{"text":"中文","identity":"test"},{"text":"分词","identity":"test"}]}}
					List<DBObject> docs = new ArrayList<>();
					ArrayList<Object> texts = new ArrayList<>();
					while (iter.hasNext()) {
						LinkedHashMap<?, ?> crudItem = iter.next();
						Object text = crudItem.get("text");
						Object identity = crudItem.get("identity");
						if (DPUtil.empty(text))
							continue;
						if (null == identity)
							identity = "";
						texts.add(text);
						BasicDBObject doc = new BasicDBObject()
								.append("text", text)
								.append("identity", identity)
								.append("time_create", time)
								.append("time_update", time);
						docs.add(doc);
					}
					ArrayList<Map<?, ?>> textMapList = dictDao.getList(null,
							new BasicDBObject("text", new BasicDBObject("$in",
									texts)), null, 1, 0, 0);
					for (Map<?, ?> textMap : textMapList) {
						int size = docs.size();
						for (int i = size - 1; i >= 0; i--) {
							if (!docs.get(i).get("text")
									.equals(textMap.get("text")))
								continue;
							docs.remove(i);
						}
					}
					int insertSize = docs.size();
					if (0 != insertSize && dictDao.insert(docs) == null) {
						status = false;
						crudMap.put(crud, null);
					} else {
						crudMap.put(crud, insertSize);
					}
					break;
				case "update": // {"word":{"update":[{"from":{"text":"分词"},"to":{"text":"词语","identity":"test"}}]}}
					ArrayList<Integer> effectList = new ArrayList<>();
					while (iter.hasNext()) {
						LinkedHashMap<?, ?> crudItem = iter.next();
						object = crudItem.get("from");
						if (DPUtil.empty(object) || !(object instanceof LinkedHashMap)) {
							effectList.add(null);
							continue;
						}
						LinkedHashMap<?, ?> updateFrom = (LinkedHashMap<?, ?>) object;
						object = crudItem.get("to");
						if (DPUtil.empty(object) || !(object instanceof LinkedHashMap<?, ?>)) {
							effectList.add(null);
							continue;
						}
						LinkedHashMap<?, ?> updateTo = (LinkedHashMap<?, ?>) object;
						Object text = updateFrom.get("text");
						Object identity = updateFrom.get("identity");
						if (null == text && null == identity) {
							effectList.add(null);
							continue;
						}
						DBObject updateQuery = new BasicDBObject();
						if (null != text)
							updateQuery.put("text", text);
						if (null != identity)
							updateQuery.put("identity", identity);
						text = updateTo.get("text");
						identity = updateTo.get("identity");
						if (null == text && null == identity) {
							effectList.add(null);
							continue;
						}
						DBObject updateDoc = new BasicDBObject("time_update",
								time);
						if (null != text)
							updateDoc.put("text", text);
						if (null != identity)
							updateDoc.put("identity", identity);
						int result = dictDao.update(updateQuery, updateDoc);
						if (result >= 0) {
							effectList.add(result);
						} else {
							status = false;
							effectList.add(null);
						}
					}
					crudMap.put(crud, effectList);
					break;
				case "delete": // {"word":{"delete":[{"text":"分词"},{"text":"词语","identity":"test"}]}}
					BasicDBList dbList = new BasicDBList();
					while (iter.hasNext()) {
						LinkedHashMap<?, ?> crudItem = iter.next();
						Object text = crudItem.get("text");
						Object identity = crudItem.get("identity");
						if (null == text && null == identity) {
							continue;
						}
						DBObject doc = new BasicDBObject();
						if (null != text)
							doc.put("text", text);
						if (null != identity)
							doc.put("identity", identity);
						dbList.add(doc);
					}
					int deleteSize = dbList.size();
					if (0 != deleteSize) {
						deleteSize = dictDao.delete(new BasicDBObject("$or",
								dbList));
						if (deleteSize < 0) {
							status = false;
							crudMap.put(crud, null);
							break;
						}
					}
					crudMap.put(crud, deleteSize);
					break;
				default:
				}
				map.put(dict, crudMap);
			}
		}
		if (!status)
			map.put("status", status);
		return map;
	}

	public LinkedHashMap<String, Object> search(String dictSerial,
			Map<String, Object> args) {
		LinkedHashMap<String, Object> returnMap = getListTemplate();
		BasicDBObject query = new BasicDBObject();
		Object type = args.get("type");
		if (DPUtil.empty(type) || !dictDao.selectTable(type.toString(), dictSerial)) return null;
		Object text = args.get("text");
		if (!DPUtil.empty(text)) {
			query.append("text", Pattern.compile("^.*" + text + ".*$"));
		}
		Object identity = args.get("identity");
		if (null != identity) {
			query.append("identity", identity);
		}
		int page = ValidateUtil.filterInteger(args.get("page"), true, 1, null,
				1);
		int pageSize = ValidateUtil.filterInteger(args.get("pageSize"), true,
				0, 500, 0);
		long count = dictDao.getCount(query);
		if (count > 0) {
			returnMap.put("rows", dictDao.getList(null, query,
					new BasicDBObject("time_update", -1), page, pageSize, 0));
		}
		returnMap.put("page", page);
		returnMap.put("pageSize", pageSize);
		returnMap.put("total", count);
		if (pageSize > 0)
			returnMap
					.put("totalPage",
							Math.ceil(Double.valueOf(count)
									/ Double.valueOf(pageSize)));
		return returnMap;
	}

	public Object insert(Object type, String dictSerial, BasicDBObject doc) {
		if (DPUtil.empty(type)
				|| !dictDao.selectTable(type.toString(), dictSerial))
			return null;
		return dictDao.insert(doc);
	}

	public List<DBObject> insert(Object type, String dictSerial,
			List<DBObject> docs) {
		if (DPUtil.empty(type)
				|| !dictDao.selectTable(type.toString(), dictSerial))
			return null;
		return dictDao.insert(docs);
	}

	public long exist(Object type, String dictSerial, Object[] texts) {
		if (DPUtil.empty(type)
				|| !dictDao.selectTable(type.toString(), dictSerial))
			return -1;
		return dictDao.getCount(new BasicDBObject("text", new BasicDBObject(
				"$in", MongoUtil.arrayToList(texts))));
	}

	public int updateById(Object type, String dictSerial, Object id,
			Object text, Object identity) {
		if (DPUtil.empty(id) || DPUtil.empty(type)
				|| !dictDao.selectTable(type.toString(), dictSerial))
			return -1;
		DBObject doc = dictDao.getById(null, id);
		if (null == doc)
			return -2;
		doc.put("text", text);
		if (null != identity)
			doc.put("identity", identity);
		doc.put("time_update", DPUtil.getCurrentSeconds());
		return dictDao.updateById(doc, id);
	}

	public int deleteById(Object type, String dictSerial, Object id) {
		if (DPUtil.empty(id) || DPUtil.empty(type)
				|| !dictDao.selectTable(type.toString(), dictSerial))
			return -1;
		return dictDao.deleteById(id);
	}

	public int deleteByIds(Object type, String dictSerial, Object... ids) {
		if (DPUtil.empty(ids) || DPUtil.empty(type)
				|| !dictDao.selectTable(type.toString(), dictSerial))
			return -1;
		return dictDao.deleteByIds(ids);
	}
}
