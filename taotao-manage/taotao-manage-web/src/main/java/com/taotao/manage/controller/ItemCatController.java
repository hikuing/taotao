package com.taotao.manage.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.manage.pojo.ItemCat;
import com.taotao.manage.service.ItemCatService;

@Controller
@RequestMapping(value="item/cat")
public class ItemCatController {
	
	@Autowired
	private ItemCatService itemCatService;

	/**
	 * 根据父类目查询子类目
	 * 需要接受传递的参数
	 * @param parentId
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ItemCat>> queryItemCatByParentId(@RequestParam(value="id",defaultValue="0") Long parentId) {
		try {
			ItemCat record = new ItemCat();
			record.setParentId(parentId);
			List<ItemCat> itemCats = this.itemCatService.queryListByWhere(record);
			if (itemCats == null || itemCats.isEmpty()) {
				//没有查询到数据，返回404
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}else {
				//查询成功，返回200
				return ResponseEntity.ok(itemCats);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//异常，返回500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 根据商品类目id查询类目名称
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/name",method=RequestMethod.GET)
	public ResponseEntity<ItemCat> queryItemCatById(@RequestParam(value="id") Long id) {
		try {
			ItemCat itemCat = this.itemCatService.queryById(id);
			if (itemCat == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}else {
				return ResponseEntity.ok(itemCat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
