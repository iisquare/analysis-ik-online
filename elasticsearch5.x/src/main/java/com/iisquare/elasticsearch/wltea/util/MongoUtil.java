package com.iisquare.elasticsearch.wltea.util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * MongoClient本身支持连接池
 */
public class MongoUtil {

	final static Logger logger = ESLoggerFactory.getLogger(MongoUtil.class);
	private static MongoClient mongoClient = null;

	public static synchronized MongoClient connect(
			String host, int port, String userName, String database, String password) {
		if (null != mongoClient) return mongoClient;
		MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());
		String[] hosts = DPUtil.explode(host, ",", " ", true);
		List<ServerAddress> list = new ArrayList<>();
		try {
			for (String item : hosts) {
				list.add(new ServerAddress(item, port));
			}
			mongoClient = new MongoClient(list, Arrays.asList(credential));
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return mongoClient;
	}

	public static synchronized void close() {
		if (null != mongoClient) {
			mongoClient.close();
			mongoClient = null;
		}
	}

	public static BasicDBList arrayToList(Object[] objs) {
		BasicDBList list = new BasicDBList();
		if (!list.addAll(Arrays.asList(objs)))
			return null;
		return list;
	}
}
