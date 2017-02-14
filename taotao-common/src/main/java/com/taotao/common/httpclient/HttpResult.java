package com.taotao.common.httpclient;

public class HttpResult {

	private Integer statusCode;
	
	private String content;
	
	public HttpResult(){
	}

	public HttpResult(Integer statusCode, String content) {
		this.statusCode = statusCode;
		this.content = content;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
