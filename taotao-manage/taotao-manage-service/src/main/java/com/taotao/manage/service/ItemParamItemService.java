package com.taotao.manage.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.taotao.manage.mapper.ItemParamItemMapper;
import com.taotao.manage.pojo.ItemParamItem;

@Service
public class ItemParamItemService extends BaseService<ItemParamItem> {

	@Autowired
	private ItemParamItemMapper itemParamItemMapper;
	
	/**
	 * 根据商品id更新商品规格
	 * @param itemId
	 * @param itemParams
	 * @return
	 */
	public Integer updateByItemId(Long itemId,String itemParams) {
		ItemParamItem itemParamItem = new ItemParamItem();
		itemParamItem.setParamData(itemParams);
		itemParamItem.setUpdated(new Date());
		
		Example example = new Example(ItemParamItem.class);
		example.createCriteria().andEqualTo("itemId", itemId);
		
		return this.itemParamItemMapper.updateByExampleSelective(itemParamItem, example);
	}
}
