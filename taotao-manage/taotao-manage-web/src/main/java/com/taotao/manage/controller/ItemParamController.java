package com.taotao.manage.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.manage.pojo.ItemParam;
import com.taotao.manage.service.ItemParamService;

@Controller
@RequestMapping(value="item/param")
public class ItemParamController {

	@Autowired
	private ItemParamService itemParamService;
	
	/**
	 * 批量删除商品规格模板
	 * @param Ids
	 * @return
	 */
	@RequestMapping(value="delete",method=RequestMethod.DELETE)
	public ResponseEntity<Void> deleteItemParamByIds(@RequestParam("ids") Long[] Ids) {
		try {
			List<Object> list = new ArrayList<>();
			for (Long id : Ids) {
				list.add(id);
			}
			Integer count = this.itemParamService.deleteByIds(ItemParam.class, "id", list);
			if (count == list.size()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 查询所有规格参数模板列表
	 * @return
	 */
	@RequestMapping(value="list",method=RequestMethod.GET)
	public ResponseEntity<List<ItemParam>> queryAll() {
		try {
			List<ItemParam> list = this.itemParamService.queryAll();
			if (list == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}else {
				return ResponseEntity.ok(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 根据类目id查询对应的模板
	 * @param cid
	 * @return
	 */
	@RequestMapping(value="{cid}",method=RequestMethod.GET)
	public ResponseEntity<ItemParam> queryItemParamByCid(@PathVariable("cid") Long cid) {
		try {
			ItemParam record = new ItemParam();
			record.setItemCatId(cid);
			ItemParam itemParam = this.itemParamService.queryOne(record);
			if (itemParam == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}else {
				return ResponseEntity.ok(itemParam);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	
	/**
	 * 保存商品规格
	 * @param cid
	 * @param paramData
	 * @return
	 */
	@RequestMapping(value="{cid}",method=RequestMethod.POST)
	public ResponseEntity<Void> saveItemParam(@PathVariable("cid") Long cid,
			@RequestParam("paramData") String paramData) {
		try {
			ItemParam record = new ItemParam();
			record.setItemCatId(cid);
			record.setParamData(paramData);
			Integer count = this.itemParamService.save(record);
			if (count == 1) {
				return ResponseEntity.status(HttpStatus.CREATED).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
