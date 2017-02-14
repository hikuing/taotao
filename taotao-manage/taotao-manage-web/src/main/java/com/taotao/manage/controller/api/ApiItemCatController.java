package com.taotao.manage.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.manage.bean.ItemCatResult;
import com.taotao.manage.service.ItemCatService;

@Controller
@RequestMapping(value="api/item/cat")
public class ApiItemCatController {

	@Autowired
	private ItemCatService itemCatService;
	
	/**
	 * 按照前端的数据结构查询所有的的商品类目数据
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<ItemCatResult> queryItemCatAll() {
		try {
			ItemCatResult itemCatResult = this.itemCatService.queryAllToTree();
			if (itemCatResult == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}else {
				return ResponseEntity.ok(itemCatResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
