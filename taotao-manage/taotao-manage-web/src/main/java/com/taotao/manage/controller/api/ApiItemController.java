package com.taotao.manage.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.manage.pojo.Item;
import com.taotao.manage.service.ItemService;

@Controller
@RequestMapping(value="api/item")
public class ApiItemController {
	
	@Autowired
	private ItemService itemService;

	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ResponseEntity<Item> quertItemByItemId(@PathVariable("itemId") Long itemId) {
		try {
			Item item = this.itemService.queryById(itemId);
			if (item != null) {
				return ResponseEntity.ok(item);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
