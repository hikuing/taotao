package com.taotao.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.mapper.ContentMapper;
import com.taotao.manage.pojo.Content;

@Service
public class ContentService extends BaseService<Content> {

	@Autowired
	private ContentMapper contentMapper;
	
	/**
	 * 查询内容分页信息
	 * @param categoryId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public EasyUIResult queryPageByCategoryId(Long categoryId, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		//查询每页信息
		List<Content> list = contentMapper.queryPageByCategoryId(categoryId);
		if (list == null || list.isEmpty()) {
			return null;
		}
		PageInfo<Content> pageInfo = new PageInfo<>(list);
		return new EasyUIResult(pageInfo.getTotal(), list);
	}
}
