package com.taotao.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.service.ItemDescService;

@Controller
@RequestMapping(value="item/desc")
public class ItemDescController {
	
	@Autowired
	private ItemDescService itemDescService;
	
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ResponseEntity<ItemDesc> queryItemDescByItemId(@PathVariable("itemId") Long itemId) {
		try {
			ItemDesc desc = this.itemDescService.queryById(itemId);
			if (desc == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}else {
				return ResponseEntity.ok(desc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	} 
}
