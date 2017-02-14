package com.taotao.manage.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.manage.pojo.ItemParamItem;
import com.taotao.manage.service.ItemParamItemService;

@Controller
@RequestMapping(value="api/item/param/item")
public class ApiItemParamItemController {

	@Autowired
	private ItemParamItemService itemParamItemService;
	
	/**
	 * 根据商品id查询商品规格
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ResponseEntity<ItemParamItem> queryItemParamItemByItemId(@PathVariable("itemId") Long itemId) {
		try {
			ItemParamItem record = new ItemParamItem();
			record.setItemId(itemId);
			ItemParamItem itemParamItem = this.itemParamItemService.queryOne(record);
			if (itemParamItem != null) {
				return ResponseEntity.ok(itemParamItem);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
