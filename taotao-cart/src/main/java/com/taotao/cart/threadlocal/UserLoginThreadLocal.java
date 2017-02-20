package com.taotao.cart.threadlocal;

import com.taotao.cart.bean.User;


public class UserLoginThreadLocal {

	private static final ThreadLocal<User> USER_LOCAL = new ThreadLocal<User>();
	
	/**
	 * 向线程变量中去赋值
	 */
	public static void setUser(User user){
		USER_LOCAL.set(user);
	}
	
	public static User getUser(){
		return USER_LOCAL.get();
	}
	public static void removeUser(){
		USER_LOCAL.remove();
	}
}
