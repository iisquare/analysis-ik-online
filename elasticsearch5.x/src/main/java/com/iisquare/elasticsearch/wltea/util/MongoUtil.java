package com.iisquare.elasticsearch.wltea.util;

import java.net.UnknownHostException;
import java.util.Arrays;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * MongoClient本身支持连接池
 */
public class MongoUtil {

	private static MongoClient mongoClient = null;

	public static synchronized MongoClient connect(String host, int port,
			String userName, String database, String password) {
		if (null != mongoClient)
			return mongoClient;
		MongoCredential credential = MongoCredential.createCredential(userName,
				database, password.toCharArray());
		try {
			mongoClient = new MongoClient(new ServerAddress(host, port),
					Arrays.asList(credential));
		} catch (UnknownHostException e) {
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
