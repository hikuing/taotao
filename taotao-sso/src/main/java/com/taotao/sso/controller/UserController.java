package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="user")
public class UserController {

	@RequestMapping(value="register",method=RequestMethod.GET)
	public String toRegist() {
		return "register";
	}
}
