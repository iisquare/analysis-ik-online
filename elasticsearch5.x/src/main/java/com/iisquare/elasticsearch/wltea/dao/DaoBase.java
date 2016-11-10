package com.iisquare.elasticsearch.wltea.dao;

public abstract class DaoBase {
	public String idName = "";
	public String dbName = "";
	public String tableName = "";

	protected Exception lastException;
	public static boolean isDebug = false;

	protected void setLastException(Exception e) {
		lastException = e;
		if (isDebug)
			throw new RuntimeException("Database error!", e);
	}

	public Exception getLastException() {
		return lastException;
	}
}
