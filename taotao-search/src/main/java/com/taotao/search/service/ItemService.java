package com.taotao.search.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.httpclient.ApiService;
import com.taotao.search.bean.Item;

@Service
public class ItemService {

	@Autowired
	private ApiService apiService;

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public Item queryItemById(Long itemId) {

		try {
			String jsonData = this.apiService
					.doGet("http://manage.taotao.com/rest/api/item/" + itemId);
			if (StringUtils.isNoneBlank(jsonData)) {

				return MAPPER.readValue(jsonData, Item.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
