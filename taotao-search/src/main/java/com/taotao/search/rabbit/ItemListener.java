package com.taotao.search.rabbit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.search.service.SearchService;

public class ItemListener {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Autowired
	private SearchService searchService;
	
	public void execute(String msg){
		/**
		 * 更新索引库
		 * 增加   根据itemid 把商品信息获取到，保存到索引库
		 * 修改   根据itemid 把商品信息获取到，更新到索引库
		 * 删除   根据itemid 删除索引库对应的信息
		 * 
		 */
		try {
			JsonNode node = MAPPER.readTree(msg);
			String type = node.get("type").asText();
			String itemId = node.get("itemId").asText();
			if(StringUtils.equals(type, "insert") || StringUtils.equals(type, "update")){
//				增加   根据itemid 把商品信息获取到，保存到索引库
				searchService.addItemToSolr(Long.valueOf(itemId));
			}else if(StringUtils.equals(type, "delete")){
//				根据itemid 删除索引库对应的信息
				String[] ids = StringUtils.split(itemId, ",");
				for (String id : ids) {
					this.searchService.deleteItemFromSolr(Long.valueOf(id));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
