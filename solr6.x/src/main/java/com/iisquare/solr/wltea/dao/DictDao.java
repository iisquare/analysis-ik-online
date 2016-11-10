package com.iisquare.solr.wltea.dao;

import com.iisquare.solr.wltea.cfg.Configuration;
import com.iisquare.solr.wltea.cfg.DefaultConfig;
import com.iisquare.solr.wltea.dic.Dictionary;

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
