package com.taotao.web.bean;

import org.apache.commons.lang3.StringUtils;

public class Item extends com.taotao.manage.pojo.Item {

	/**
	 * 新添加images的get方法
	 * @return
	 */
	public String[] getImages() {
		return StringUtils.split(getImage(), ',');
	}
}
