package com.taotao.search.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.search.bean.SearchResult;
import com.taotao.search.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam("q") String keywords,
			@RequestParam(value = "page", defaultValue = "1") Integer page) throws UnsupportedEncodingException {
		// 1、分页 当前页面，如何传递？
		// 2、如何把分页信息，传递页面
		ModelAndView mv = new ModelAndView("search");
		keywords = new String(keywords.getBytes("iso-8859-1"), "utf-8");
		SearchResult res = searchService.query(keywords, page);
		mv.addObject("itemList", res.getRows());
		mv.addObject("page", page);
		mv.addObject("pages",
				Math.ceil(1.0 * res.getTotal() / SearchService.ROWS));// 总页数
		mv.addObject("query", keywords);
		return mv;
	}
}
