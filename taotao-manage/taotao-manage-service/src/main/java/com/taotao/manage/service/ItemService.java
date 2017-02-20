package com.taotao.manage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.httpclient.ApiService;
import com.taotao.manage.mapper.ItemMapper;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;

/**
 * 商品业务层
 * @author zhang
 *
 */
@Service
public class ItemService extends BaseService<Item> {

	@Autowired
	private ItemDescService itemDescService;
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private ItemParamItemService itemParamItemService;
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * 保存商品
	 * @param item
	 * @param desc
	 * @param itemParam 
	 * @return
	 */
	public Boolean saveItem(Item item, String desc, String itemParam) {
		//保存商品基本信息
		item.setId(null);//强制设置null，防止注入
		item.setStatus(1);//默认正常
		Integer count = save(item);
		if (count != 1) {
			throw new RuntimeException("保存商品基本信息失败，但是没有异常，item:" + item);
		}
		//保存商品描述
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemDesc(desc);
		itemDesc.setItemId(item.getId());
		Integer count2 = this.itemDescService.save(itemDesc);
		if (count2 != 1) {
			throw new RuntimeException("保存商品描述失败，但是没有异常，item:" + item + ",desc:" + desc);
		}
		//保存商品规格
		ItemParamItem itemParamItem = new ItemParamItem();
		itemParamItem.setItemId(item.getId());
		itemParamItem.setParamData(itemParam);
		Integer count3 = this.itemParamItemService.save(itemParamItem);
		/*if (count3 != 1) {
			throw new RuntimeException("保存商品规格失败，但是没有异常，item:" + item + ",desc:" + desc + ",itemParam:" + itemParam);
		}*/
		this.sendMsg(String.valueOf(item.getId()), "insert");
		return true;
	}

	/**
	 * 查询商品分页数据，并且按照修改时间，倒序排列
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public EasyUIResult queryItemPage(Integer pageNum, Integer pageSize) {
		
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(Item.class);
		//设置排序条件，此时这个排序条件是sql语句的一部分，order by 之后的内容
		example.setOrderByClause("updated desc , created desc ");
		List<Item> list = this.itemMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return null;
		}
		PageInfo<Item> pageInfo = new PageInfo<>(list);
		
		return new EasyUIResult(pageInfo.getTotal(), list);
	}

	/**
	 * 批量上架或下架
	 * @param list
	 * @return
	 */
	public Integer updateInstorkByIds(String status,List<Object> ids) {
		Item item = new Item();
		if (status.equals("reshelf")) {
			//上架
			item.setStatus(1);
			this.sendMsg(StringUtils.join(ids, ","), "insert");
		}
		if (status.equals("instock")) {
			//下架
			item.setStatus(2);
			this.sendMsg(StringUtils.join(ids, ","), "delete");
		}
		Example example = new Example(Item.class);
		example.createCriteria().andIn("id", ids);
		
		
		return itemMapper.updateByExampleSelective(item, example);
	}

	/**
	 * 批量删除
	 * @param class1
	 * @param string
	 * @param list
	 * @return
	 */
	public Boolean deleteItemByIds(Class<Item> clazz, String property, List<Object> ids) {
		//删除商品
		Integer count = super.deleteByIds(clazz, property, ids);
		if (count != ids.size()) {
			throw new RuntimeException("删除商品基本信息失败，但无异常！");
		}
		//删除商品描述
		Integer count2 = this.itemDescService.deleteByIds(ItemDesc.class, "itemId", ids);
		if (count2 != ids.size()) {
			throw new RuntimeException("删除商品描述失败，但无异常！");
		}
		//删除商品规格
		Integer count3 = this.itemParamItemService.deleteByIds(ItemParamItem.class, "itemId", ids);
		/*if (count3 != ids.size()) {
			throw new RuntimeException("删除商品规格失败，但无异常！");
		}*/
		
		//发送消息通知，商品被删除了
		try {
			/*StringBuilder sb = new StringBuilder();
			for (Object id : ids) {
				sb.append(id + ",");
			}*/
			//this.apiService.doGet("http://www.taotao.com/api/item/" + sb.substring(0, sb.length()-1) + ".html");
			this.sendMsg(StringUtils.join(ids, ","), "delete");
			//sb.delete(0, sb.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * 修改商品
	 * @param item
	 * @param desc
	 * @param itemParams 
	 * @return
	 */
	public Boolean updateItemById(Item item, String desc, String itemParams) {
		//修改商品基本信息
		item.setStatus(null);
		Integer count1 = super.updateByPrimaryKey(item);
		if (count1 != 1) {
			throw new RuntimeException("修改商品信息失败，但是没有异常抛出：item:" + item);
		}
		//修改商品描述
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemDesc(desc);
		itemDesc.setItemId(item.getId());
		Integer count2 = this.itemDescService.updateByPrimaryKey(itemDesc);
		if (count2 != 1) {
			throw new RuntimeException("修改商品描述失败，但是没有异常抛出：item:" + item + ",desc:" + desc);
		}
		//修改商品规格
		Integer count3 = this.itemParamItemService.updateByItemId(item.getId(),itemParams);
		/*if (count3 != 1) {
			throw new RuntimeException("修改商品规格失败，但是没有异常抛出：item:" + item + ",desc:" + desc + ",itemParams" + itemParams);
		}*/
		
		//发送消息通知，某个商品被修改了
		try {
			//this.apiService.doGet("http://www.taotao.com/api/item/" + item.getId() + ".html");
			this.sendMsg(String.valueOf(item.getId()), "update");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * 消息需要发送什么内容 itemId + 操作类型 + 时间 以什么样的格式去发送 json
	 */
	private void sendMsg(String itemId,String type) {
		try {
			Map<String , Object> msgMap = new HashMap<>();
			msgMap.put("itemId", itemId);
			msgMap.put("type", type);
			msgMap.put("time", System.currentTimeMillis());
			this.rabbitTemplate.convertAndSend("item." + type, MAPPER.writeValueAsString(msgMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
