package com.taotao.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.cookie.CookieUtils;
import com.taotao.web.bean.User;
import com.taotao.web.service.UserService;
import com.taotao.web.threadlocal.UserLoginThreadLocal;

public class UserInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//在执行controller之前去判断用户是否登录
		/**
		 * 1、先获取cookie -- token
		 * 
		 * 2、判断token是不是空
		 * 
		 * 3、根据token去 sso 查询 用户登录信息
		 * 
		 * 4、判断是否存在该用户 ； 是否登录
		 * 
		 * 没有登录，让他去登录
		 * 登录了，放行
		 */
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		if (StringUtils.isBlank(token)) {
			//token是空，重定向到登录页面
			response.sendRedirect("http://sso.taotao.com/user/login.html");
			return false;
		}
		User user = this.userService.queryUserByToken(token);
		if (user == null) {
			//没有登录
			response.sendRedirect("http://sso.taotao.com/user/login.html");
			return false;
		}
		//说明查询到了用户，放到线程变量中
		UserLoginThreadLocal.setUser(user);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//把线程变量中的user 清除掉
		UserLoginThreadLocal.removeUser();
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
