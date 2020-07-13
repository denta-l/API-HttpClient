package com.api.util;

public enum EndPointURL {
	
	
	PING("/ping/"),
	GET_ALL("/all"),
	GET_BY_ID("/find/"),
	POST("/add"),
	PUT("/update"),
	DELETE("/delete/");
	
	String urlPath;
	
	private EndPointURL(String urlPath) {
		this.urlPath = urlPath;
	}
	
	
	public String getUrlPath() {
		return this.urlPath;
	}
	
	public String getUrlpath(String id) {
		return this.urlPath + id;
	}

}
