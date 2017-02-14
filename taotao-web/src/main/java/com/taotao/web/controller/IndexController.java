package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.service.IndexService;

@Controller
public class IndexController {

	@Autowired
	private  IndexService indexService;
	
	@RequestMapping(value="index" , method = RequestMethod.GET)
	public ModelAndView toIndex(){
		ModelAndView mv = new ModelAndView("index");
		String ad1Json1 = indexService.queryAd1();
		String ad2Json2 = indexService.queryAd2();
		String ad3Json3 = indexService.queryAd3();
		String ad4Json4 = indexService.queryAd4();
		mv.addObject("indexAd1", ad1Json1);
		mv.addObject("indexAd2", ad2Json2);
		mv.addObject("indexAd3", ad3Json3);
		mv.addObject("indexAd4", ad4Json4);
		return mv;
	}
}
