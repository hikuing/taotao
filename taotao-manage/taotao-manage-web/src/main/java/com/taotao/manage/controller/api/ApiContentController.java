package com.taotao.manage.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.service.ContentService;

@Controller
@RequestMapping(value="api/content")
public class ApiContentController {

	@Autowired
	private ContentService contentService;
	
	/**
	 * 查询内容，根据内容节点id
	 * @param categoryId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryConentByCategoryId(
			@RequestParam(value = "categoryId" ) Long categoryId,
			@RequestParam(value ="page" , defaultValue = "1") Integer pageNum,
			@RequestParam(value="rows") Integer pageSize) {
		try {
			EasyUIResult result = contentService.queryPageByCategoryId(
					categoryId, pageNum, pageSize);
			if (result == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			} else {
				return ResponseEntity.ok(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				null);
	}
	
}
