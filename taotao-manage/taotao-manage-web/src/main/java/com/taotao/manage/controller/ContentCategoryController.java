package com.taotao.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.manage.pojo.ContentCategory;
import com.taotao.manage.service.ContentCategoryService;

@Controller
@RequestMapping(value="content/category")
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	/**
	 * 根据父节点id查询内容分类列表
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ContentCategory>> queryContentCategoryByParentId(@RequestParam(value="id",defaultValue="0") Long parentId) {
		try {
			ContentCategory record = new ContentCategory();
			record.setParentId(parentId);
			List<ContentCategory> list = this.contentCategoryService.queryListByWhere(record);
			if (list == null || list.isEmpty()) {
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
	 *添加节点
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<ContentCategory> saveContentCategory(ContentCategory contentCategory) {
		try {
			Boolean boolean1 = this.contentCategoryService.saveContentCategory(contentCategory);
			if (boolean1) {
				return ResponseEntity.status(HttpStatus.CREATED).body(contentCategory);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 重命名
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Void> updateContentCategoryById(ContentCategory contentCategory) {
		//防止注入
		try {
			contentCategory.setIsParent(null);
			contentCategory.setParentId(null);
			contentCategory.setSortOrder(null);
			contentCategory.setStatus(null);
			Integer count = this.contentCategoryService.updateByPrimaryKey(contentCategory);
			if (count == 1) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 删除节点
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping(method=RequestMethod.DELETE)
	public ResponseEntity<Void> deleteContentCategory(ContentCategory contentCategory) {
		try {
			Boolean boolean1 = this.contentCategoryService.deleteContentCategory(contentCategory);
			if (boolean1) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
