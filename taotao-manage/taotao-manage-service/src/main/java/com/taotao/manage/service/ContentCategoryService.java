package com.taotao.manage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.taotao.manage.pojo.ContentCategory;

@Service
public class ContentCategoryService extends BaseService<ContentCategory> {

	/**
	 * 添加节点
	 * @param contentCategory
	 * @return
	 */
	public Boolean saveContentCategory(ContentCategory contentCategory) {
		//设置默认值
		contentCategory.setIsParent(false);
		contentCategory.setSortOrder(1);
		contentCategory.setStatus(1);
		Integer count = super.save(contentCategory);
		if (count != 1) {
			throw new RuntimeException("新增节点失败！contentCategory：" + "contentCategory");
		}
		//判断父节点是不是父节点
		//根据id查询父节点
		ContentCategory parent = super.queryById(contentCategory.getParentId());
		if (!parent.getIsParent()) {
			parent.setIsParent(true);
			super.updateByPrimaryKey(parent);
		}
		return true;
	}

	/**
	 * 删除节点
	 * @param contentCategory
	 * @return
	 */
	public Boolean deleteContentCategory(ContentCategory contentCategory) {
		/**
		 *   1、如果有后代
			   需要把所有的后代删除
			   如果有后代，需要把所有的后代的id查询出来
			 然后统一使用一条sql 进行删除	
			 delete  from   ... where id in ();
			 2、删除本身
		     3、删除的时候 
			   判断当前的节点是不是有兄弟节点
			   如果没有：需要把父亲节点修改成叶子节点
			   如果有：不用修改
		 */
		//判断当前节点是不是父节点
		List<Object> ids = new ArrayList<>();
		ids.add(contentCategory.getId());
		this.queryIdsByParentId(ids, contentCategory.getId());
		
		//ids 是要删除的所有id
		super.deleteByIds(ContentCategory.class, "id", ids);
		ContentCategory parent = new ContentCategory();
		parent.setParentId(contentCategory.getParentId());
		List<ContentCategory> brothers = super.queryListByWhere(parent);
		if (brothers == null || brothers.isEmpty()) {
			//修改父节点，变为子节点
			ContentCategory p = new ContentCategory();
			p.setId(contentCategory.getParentId());
			p.setIsParent(false);
			super.updateByPrimaryKey(p);
		}
		return true;
	}
	
	/**
	 * 根据父节点查询所有子节点id
	 * @param ids
	 * @param parentId
	 */
	private void queryIdsByParentId(List<Object> ids,Long parentId) {
		ContentCategory record = new ContentCategory();
		record.setParentId(parentId);
		List<ContentCategory> list = super.queryListByWhere(record);
		if (list == null) {
			return ;
		}
		for (ContentCategory contentCategory : list) {
			ids.add(contentCategory.getId());
			//开始递归
			if (contentCategory.getIsParent()) {
				queryIdsByParentId(ids,contentCategory.getId());
			}
		}
	}

}
