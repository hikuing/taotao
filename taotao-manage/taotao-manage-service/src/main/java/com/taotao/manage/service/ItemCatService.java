package com.taotao.manage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.redis.RedisService;
import com.taotao.manage.bean.ItemCatData;
import com.taotao.manage.bean.ItemCatResult;
import com.taotao.manage.pojo.ItemCat;

@Service
public class ItemCatService extends BaseService<ItemCat>{
	
	@Autowired
	private RedisService redisService;
	//java对象与json字符串转换
	private static final ObjectMapper MAPPER = new ObjectMapper();
	//设置生存时间为一个月
	private static final Integer TIME_OUT = 60 * 60 * 24 * 30;
	
	/**
	 * 全部查询，并且生成树状结构
	 * @return
	 */
	public ItemCatResult queryAllToTree() {
		
		//设置查询或保存的key
		//key说明：项目名_模块名_业务名
		String key = "TAOTAO_MANAGE_ITEM_CAT_API";
		
		/**
		 * 从redis中获取内容，如果获取到，直接return
		 */
		try {
			//不能影响原来的业务逻辑
			String jsonStr = this.redisService.get(key);
			if (StringUtils.isNotBlank(jsonStr)) {
				//缓存中命中，直接返回
				//第一个参数：要转换的json字符串，第二个参数：目标java对象
				return MAPPER.readValue(jsonStr, ItemCatResult.class);
			}
			//如果缓存没有名中，去数据库中查询
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ItemCatResult result = new ItemCatResult();
		// 全部查出，并且在内存中生成树形结构
		List<ItemCat> cats = super.queryAll();
		
		// 转为map存储，key为父节点ID，value为数据集合
		Map<Long, List<ItemCat>> itemCatMap = new HashMap<Long, List<ItemCat>>();
		for (ItemCat itemCat : cats) {
			if(!itemCatMap.containsKey(itemCat.getParentId())){
				itemCatMap.put(itemCat.getParentId(), new ArrayList<ItemCat>());
			}
			itemCatMap.get(itemCat.getParentId()).add(itemCat);
		}
		
		// 封装一级对象
		List<ItemCat> itemCatList1 = itemCatMap.get(0L);
		for (ItemCat itemCat : itemCatList1) {
			ItemCatData itemCatData = new ItemCatData();
			itemCatData.setUrl("/products/" + itemCat.getId() + ".html");
			itemCatData.setName("<a href='"+itemCatData.getUrl()+"'>"+itemCat.getName()+"</a>");
			result.getItemCats().add(itemCatData);
			if(!itemCat.getIsParent()){
				continue;
			}
			
			// 封装二级对象
			List<ItemCat> itemCatList2 = itemCatMap.get(itemCat.getId());
			List<ItemCatData> itemCatData2 = new ArrayList<ItemCatData>();
			itemCatData.setItems(itemCatData2);
			for (ItemCat itemCat2 : itemCatList2) {
				ItemCatData id2 = new ItemCatData();
				id2.setName(itemCat2.getName());
				id2.setUrl("/products/" + itemCat2.getId() + ".html");
				itemCatData2.add(id2);
				if(itemCat2.getIsParent()){
					// 封装三级对象
					List<ItemCat> itemCatList3 = itemCatMap.get(itemCat2.getId());
					List<String> itemCatData3 = new ArrayList<String>();
					id2.setItems(itemCatData3);
					for (ItemCat itemCat3 : itemCatList3) {
						itemCatData3.add("/products/" + itemCat3.getId() + ".html|"+itemCat3.getName());
					}
				}
			}
			if(result.getItemCats().size() >= 14){
				break;
			}
		}
		
		/**
		 * 把要缓存的内容，保存到redis
		 */
		try {
			//缓存不能影响原有的业务逻辑
			this.redisService.set(key, MAPPER.writeValueAsString(result), TIME_OUT);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
