package com.taotao.sso.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.cookie.CookieUtils;
import com.taotao.sso.pojo.User;
import com.taotao.sso.service.UserService;

@Controller
@RequestMapping(value="user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	@RequestMapping(value="login",method=RequestMethod.GET)
	public String toLogin() {
		return "login";
	}

	/**
	 * 跳转到注册页面
	 * @return
	 */
	@RequestMapping(value="register",method=RequestMethod.GET)
	public String toRegist() {
		return "register";
	}
	
	/**
	 * 验证用户信息
	 * @param param	
	 * @param type
	 * @return
	 */
	@RequestMapping(value="check/{param}/{type}",method=RequestMethod.GET)
	public ResponseEntity<Boolean> checkParam(@PathVariable("param") String param,
			@PathVariable("type") Integer type) {
		try {
			Boolean boolean1 = this.userService.checkParam(param,type);
			if (boolean1 == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}else {
				return ResponseEntity.ok(!boolean1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 用户注册
	 * @param user
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="doRegister",method=RequestMethod.POST)
	public ResponseEntity<Map<String, String>> doRegister(@Valid User user,BindingResult bindingResult) {
		Map<String, String> res = new HashMap<>();
		try {
			List<String> msgs = new ArrayList<>();
			//取出校验信息
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			if (allErrors != null && allErrors.size() > 0) {
				//里面包含了验证失败的信息，证明验证失败，否则验证成功
				for (ObjectError error : allErrors) {
					String defaultMessage = error.getDefaultMessage();
					msgs.add(defaultMessage);
				}
				res.put("data", StringUtils.join(msgs, "|"));
				res.put("status", "250");
				return ResponseEntity.ok(res);
			}
			//没有错误的校验信息，用户提交的数据合法
			Boolean boolean1 = this.userService.doRegister(user);
			if (boolean1) {
				res.put("status", "200");
				return ResponseEntity.ok(res);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("status", "250");
		res.put("data", "服务器忙！");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	@RequestMapping(value="doLogin",method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> doLogin(HttpServletRequest request,HttpServletResponse response,User user) {
		/**
		 * 1、用户名和密码进行校验
		 * 2、匹配成功
		 *          把用户信息保存到redis中
		 *          生成一个 对应的key === token == 值
		 *    匹配失败
		 *      直接返回失败
		 *  3、把key==token 放入cookie中
		 *  
		 *  4、返回给浏览器 成功或者失败
		 */
		try {
			//校验用户名和密码是否匹配，得到token信息
			String token = this.userService.doLogin(user);
			Map<String, Object> res = new HashMap<>();
			if (StringUtils.isBlank(token)) {
				//token是空，登录失败
				res.put("status", 250);
			}else {
				res.put("status", 200);
				//把token写入到cookie中
				CookieUtils.setCookie(request, response, "TT_TOKEN", token, 60 * 30);
			}
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	
}
