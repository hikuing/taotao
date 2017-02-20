package com.taotao.search.bean;

import java.util.List;

public class SearchResult {

	private Long total;
	
	private List<?> rows;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}

	public SearchResult(Long total, List<?> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	
}
