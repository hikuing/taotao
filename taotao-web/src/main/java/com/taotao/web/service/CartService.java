package com.taotao.web.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.httpclient.ApiService;
import com.taotao.web.bean.Cart;
import com.taotao.web.threadlocal.UserLoginThreadLocal;

@Service
public class CartService {

	@Autowired
	private ApiService apiService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public List<Cart> queryCart() {
		try {
			String jsonStr = this.apiService.doGet("http://cart.taotao.com/service/api/cart/" + UserLoginThreadLocal.getUser().getId());
			if (StringUtils.isNotBlank(jsonStr)) {
				return MAPPER.readValue(jsonStr, MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
