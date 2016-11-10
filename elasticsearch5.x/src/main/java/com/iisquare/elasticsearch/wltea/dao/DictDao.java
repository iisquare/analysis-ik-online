package com.iisquare.elasticsearch.wltea.dao;

import com.iisquare.elasticsearch.wltea.cfg.Configuration;
import com.iisquare.elasticsearch.wltea.cfg.DefaultConfig;
import com.iisquare.elasticsearch.wltea.dic.Dictionary;

public class DictDao extends MongoBase {

	private Configuration configuration; // 配置参数
	
	public DictDao() {
		configuration = DefaultConfig.getInstance();
		dbName = configuration.getDictDatabase();
	}

	public boolean selectTable(String type, String dictSerial) {
		switch (type) {
		case "quantifier":
			tableName = Dictionary.buildCollectionName(configuration.getDictQuantifier(), dictSerial);
			break;
		case "stopword":
			tableName = Dictionary.buildCollectionName(configuration.getDictStopword(), dictSerial);
			break;
		case "synonym":
			tableName = Dictionary.buildCollectionName(configuration.getDictSynonym(), dictSerial);
			break;
		case "word":
			tableName = Dictionary.buildCollectionName(configuration.getDictWord(), dictSerial);
			break;
		default:
			return false;
		}
		return true;
	}

}
