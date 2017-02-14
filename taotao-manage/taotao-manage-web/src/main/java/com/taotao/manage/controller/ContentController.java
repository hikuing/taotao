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

import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.Content;
import com.taotao.manage.service.ContentService;

@Controller
@RequestMapping(value="content")
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	/**
	 * 修改内容
	 * @param content
	 * @return
	 */
	@RequestMapping(value="edit",method=RequestMethod.PUT)
	public ResponseEntity<Void> updateContent(Content content) {
		try {
			content.setCategoryId(null);
			Integer count = this.contentService.updateByPrimaryKey(content);
			if (count == 1) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 删除内容
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="delete", method=RequestMethod.DELETE)
	public ResponseEntity<Void> deleteContentByIds(@RequestParam("ids") Long[] ids) {
		try {
			List<Object> list = new ArrayList<>();
			for (Long id : ids) {
				list.add(id);
			}
			Integer count = this.contentService.deleteByIds(Content.class, "id", list);
			if (count == list.size()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 查询内容分页信息
	 * @param categoryId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryContentByCategoryId(
			@RequestParam(value="categoryId") Long categoryId,
			@RequestParam(value="page") Integer pageNum,
			@RequestParam(value="rows") Integer pageSize) {
		try {
			EasyUIResult result = this.contentService.queryPageByCategoryId(categoryId,pageNum,pageSize);
			if (result == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}else {
				return ResponseEntity.ok(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 保存内容
	 * @param content
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> saveContent(Content content) {
		//防止数据注入
		try {
			content.setId(null);
			Integer count = this.contentService.save(content);
			if (count == 1) {
				return ResponseEntity.status(HttpStatus.CREATED).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
