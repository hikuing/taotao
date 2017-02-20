package com.taotao.cart.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.cart.bean.User;
import com.taotao.common.httpclient.ApiService;

@Service
public class UserService {
	
	@Autowired
	private ApiService apiService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public User queryUserByToken(String token) {
		try {
			String json = this.apiService.doGet("http://sso.taotao.com/service/api/user/"+token);
			if(StringUtils.isNoneBlank(json)){
				return MAPPER.readValue(json, User.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
