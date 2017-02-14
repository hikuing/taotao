package com.taotao.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="page")
public class pageController {

	
	/**
	 * 返回值是string，表示是viewname 视图名称，（前提是没有@responseBody）
	 * @param pageName
	 * @return
	 */
	@RequestMapping(value="{pageName}")
	public String toPage(@PathVariable("pageName") String pageName) {
		return pageName;
	}
}
