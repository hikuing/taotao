package com.taotao.web.rabbitmq;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.web.service.ItemService;

public class ItemListener {

	@Autowired
	private ItemService itemService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public void execute(String msg){
		//接受消息，进行处理
		//根据商品id删除缓存
		try {
			JsonNode node = MAPPER.readTree(msg);
			//获取商品id --- 先获取 这个商品操作类型
			String type = node.get("type").asText();
			//关系type 是 update 删除缓存  和 delete 删除缓存
			if(StringUtils.equals(type, "update") || StringUtils.equals(type, "delete")){
				
				//根据商品id删除对应的缓存
				this.itemService.delItemCache(node.get("itemId").asText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
