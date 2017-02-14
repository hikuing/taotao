package com.taotao.manage.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.service.ItemService;

@Controller
@RequestMapping(value="item")
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);
	
	/**
	 * 修改商品
	 * @param item
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "update",method=RequestMethod.PUT)
	public ResponseEntity<Void> updateItemById(Item item,@RequestParam("desc") String desc,
			@RequestParam("itemParams") String itemParams) {
		try {
			Boolean boolean1 = this.itemService.updateItemById(item,desc,itemParams);
			if (boolean1) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 保存商品
	 * @param item 商品
	 * @param desc 商品描述
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> saveItem(Item item,@RequestParam("desc") String desc,
			@RequestParam("itemParams") String itemParams) {
		 try {
			 if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("保存商品接收参数：item:{},desc:{}",item,desc);
			 }
			Boolean boolean1 = this.itemService.saveItem(item,desc,itemParams);
			 if (boolean1) {
				 if (LOGGER.isInfoEnabled()) {
					LOGGER.info("保存商品成功! item:{},desc:{}",item,desc);
				}
				return ResponseEntity.status(HttpStatus.CREATED).build();
			}else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("保存商失败!但是没有异常输出！ item:{},desc:{}",item,desc);
				}
			}
		} catch (Exception e) {
			LOGGER.error("保存商品出现异常！ item:{}",item);
			e.printStackTrace();
		}
		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 分页查询商品基本信息
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryItemPage(
			@RequestParam(value="page",defaultValue="1") Integer pageNum,
			@RequestParam(value="rows",defaultValue="30") Integer pageSize) {
		try {
			EasyUIResult uiPage = this.itemService.queryItemPage(pageNum,pageSize);
			if (uiPage == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(uiPage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 上架或下架
	 * @param ids
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Void> instork(
			@RequestParam("status") String status,
			@RequestParam("ids") Long[] ids) {
		try {
			List<Object> list = new ArrayList<>();
			for (Long id : ids) {
				list.add(id);
			}
			Integer count = this.itemService.updateInstorkByIds(status,list);
			if (count == list.size()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping(method=RequestMethod.DELETE)
	public ResponseEntity<Void> deleteByIds(@RequestParam("ids") Long[] ids) {
		try {
			List<Object> list = new ArrayList<>();
			for (Long id : ids) {
				list.add(id);
			}
			Boolean boolean1 = this.itemService.deleteItemByIds(Item.class, "id", list);
			if (boolean1) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
