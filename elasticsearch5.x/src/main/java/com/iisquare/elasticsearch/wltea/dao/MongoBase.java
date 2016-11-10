package com.iisquare.elasticsearch.wltea.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.iisquare.elasticsearch.wltea.cfg.Configuration;
import com.iisquare.elasticsearch.wltea.cfg.DefaultConfig;
import com.iisquare.elasticsearch.wltea.util.MongoUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public abstract class MongoBase extends DaoBase {

	private MongoClient client;

	public MongoBase() {
		idName = "_id";
	}

	protected DBCollection getDBCollection() {
		if (null == client) {
			Configuration cfg = DefaultConfig.getInstance();
			client = MongoUtil.connect(cfg.getHost(), cfg.getPort(),
					cfg.getUserName(), cfg.getAuthDatabase(), cfg.getPassword());
		}
		return client.getDB(dbName).getCollection(tableName);
	}

	public long getCount(DBObject query) {
		try {
			return getDBCollection().count(query);
		} catch (Exception e) {
			setLastException(e);
		}
		return -1;
	}

	public ArrayList<Map<?, ?>> getList(DBObject fields, DBObject query,
			DBObject order, int page, int pageSize, int options) {
		if (page < 1)
			page = 1;
		if (pageSize < 0)
			pageSize = 0;
		DBCursor cursor = null;
		ArrayList<Map<?, ?>> list = new ArrayList<>();
		try {
			if (null == fields) {
				cursor = getDBCollection().find(query);
			} else {
				cursor = getDBCollection().find(query, fields);
			}
			if (null != order)
				cursor.sort(order);
			if (pageSize > 0) {
				cursor.skip((page - 1) * pageSize).limit(pageSize);
			}
			if (options > 0) {
				cursor.addOption(options);
			}
			while (cursor.hasNext()) {
				DBObject doc = cursor.next();
				doc.put(idName, doc.get(idName).toString());
				list.add(doc.toMap());
			}
		} catch (Exception e) {
			list = null;
			setLastException(e);
		} finally {
			if (null != cursor)
				cursor.close();
		}
		return list;
	}

	public Object insert(DBObject doc) {
		try {
			getDBCollection().insert(doc);
			return doc.get(idName).toString();
		} catch (Exception e) {
			setLastException(e);
		}
		return null;
	}

	public List<DBObject> insert(List<DBObject> docs) {
		try {
			getDBCollection().insert(docs);
		} catch (Exception e) {
			docs = null;
			setLastException(e);
		}
		return docs;
	}

	public DBObject getById(DBObject fields, Object id) {
		DBObject doc;
		try {
			if (null == fields) {
				doc = getDBCollection().findOne(new ObjectId(id.toString()));
			} else {
				doc = getDBCollection().findOne(
						new BasicDBObject(idName, new ObjectId(id.toString())),
						fields);
			}
			if (null != doc)
				doc.put(idName, doc.get(idName).toString());
		} catch (Exception e) {
			doc = null;
			setLastException(e);
		}
		return doc;
	}

	public int updateById(DBObject doc, Object id) {
		doc.removeField(idName);
		return update(new BasicDBObject(idName, new ObjectId(id.toString())),
				doc);
	}

	public int update(DBObject query, DBObject doc) {
		try {
			return getDBCollection().update(query, doc).getN();
		} catch (Exception e) {
			setLastException(e);
		}
		return -1;
	}

	public int deleteById(Object id) {
		return delete(new BasicDBObject(idName, new ObjectId(id.toString())));
	}

	public int deleteByIds(Object... ids) {
		BasicDBList list = new BasicDBList();
		for (Object id : ids) {
			list.add(new ObjectId(id.toString()));
		}
		return delete(new BasicDBObject(idName, new BasicDBObject("$in", list)));
	}

	public int delete(DBObject query) {
		try {
			return getDBCollection().remove(query).getN();
		} catch (Exception e) {
			setLastException(e);
		}
		return -1;
	}
}
