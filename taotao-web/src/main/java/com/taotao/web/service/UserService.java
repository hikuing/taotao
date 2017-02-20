package com.taotao.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.httpclient.ApiService;
import com.taotao.web.bean.User;

@Service
public class UserService {

	@Autowired
	private ApiService apiService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * 根据cookie查询用户
	 * @param token
	 * @return
	 */
	public User queryUserByToken(String token) {
		try {	
			String json = this.apiService.doGet("http://sso.taotao.com/service/api/user/"+token);
			if (StringUtils.isNotBlank(json)) {
				return MAPPER.readValue(json, User.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
