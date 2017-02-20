package com.taotao.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.httpclient.ApiService;
import com.taotao.common.redis.RedisService;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;
import com.taotao.web.bean.Item;


@Service
public class ItemService {

	@Autowired
	private ApiService apiService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Autowired
	private RedisService redisService;
	//设置生存时间为一周
	private static final Integer TIME_OUT = 60 * 60 * 24 * 7;
	
	private static final String KEY = "TAOTO_WEB_ITEM_ITEM_BASIS_";
	
	/**
	 * 根据商品id查询商品信息
	 * @param itemId
	 * @return
	 */
	public Item queryItemByItemId(Long itemId) {
		
		String key = KEY + itemId;
		//去缓存中读取商品的基本数据
		try {
			String jsonStr = this.redisService.get(key);
			if (StringUtils.isNotBlank(jsonStr)) {
				return MAPPER.readValue(jsonStr, Item.class);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		try {
			String jsonData = this.apiService.doGet("http://manage.taotao.com/rest/api/item/" + itemId);
			
			if (StringUtils.isNotBlank(jsonData)) {
				try {
					//缓存操作不能够影响原有的业务逻辑
					this.redisService.set(key, jsonData, TIME_OUT);
				} catch (Exception e) {
					e.printStackTrace();
			}
				
				return MAPPER.readValue(jsonData, Item.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 根据商品id查询商品描述
	 * @param itemId
	 * @return
	 */
	public ItemDesc queryItemDescByItemId(Long itemId) {
		try {
			String jsonData = this.apiService.doGet("http://manage.taotao.com/rest/api/item/desc/" + itemId);
			if (StringUtils.isNotBlank(jsonData)) {
				return MAPPER.readValue(jsonData, ItemDesc.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据商品id查询商品规格
	 * @param itemId
	 * @return
	 */
	public String queryItemParamItemByItemId(Long itemId) {
		try {
			String jsonData = this.apiService.doGet("http://manage.taotao.com/rest/api/item/param/item/" + itemId);
			if(StringUtils.isNoneBlank(jsonData)){
				//进行html格式的封装拼接
				ItemParamItem itemParamItem = MAPPER.readValue(jsonData, ItemParamItem.class);
				String paramData = itemParamItem.getParamData();
				ArrayNode groups = (ArrayNode) MAPPER.readTree(paramData);
				StringBuffer res = new StringBuffer("<table class=\"Ptable\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"100%\">");
				res.append("<tbody>");
				for(JsonNode group : groups){
					res.append("<tr>");
					res.append("<th class=\"tdTitle\" colspan=\"2\">");
					res.append(group.get("group").asText());
					res.append("</th>");
					res.append("</tr>");
					res.append("<tr></tr>");
					ArrayNode params = (ArrayNode) group.get("params");
					for(JsonNode node : params){
						res.append("<tr>");
						res.append("<td class=\"tdTitle\">");
						res.append(node.get("k").asText());
						res.append("</td>");
						res.append("<td>");
						res.append(node.get("v").asText());
						res.append("</td>");
						res.append("</tr>");
						
					}
				}
				res.append("</tbody></table>");
				return res.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 删除商品缓存
	 * @param itemId
	 */
	public void delItemCache(String itemId) {
		//this.redisService.del(KEY + itemId);
		String[] split = StringUtils.split(itemId, ",");
		for (String id : split) {
			this.redisService.del(KEY + id);
		}
	}
}
