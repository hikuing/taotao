package com.taotao.web.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.httpclient.ApiService;
import com.taotao.common.httpclient.HttpResult;
import com.taotao.web.bean.Order;
import com.taotao.web.bean.User;
import com.taotao.web.threadlocal.UserLoginThreadLocal;

@Service
public class OrderService {
	
	@Autowired
	private ApiService apiService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * 调用下单接口,完成下单
	 * @param order
	 * @return
	 */
	public String submitOrder(Order order) {
		try {
			//从线程变量中取出用户信息
			User user = UserLoginThreadLocal.getUser();
			//缺少当前用户信息
			order.setBuyerNick(user.getUsername());
			order.setUserId(user.getId());
			HttpResult result = this.apiService.doPostJson("http://order.taotao.com/order/create",
					MAPPER.writeValueAsString(order));
			//result.getStatusCode();获取http状态码
			if (result.getStatusCode() == 200) {
				//获取响应体
				String resJson = result.getContent();
				JsonNode node = MAPPER.readTree(resJson);
				//判断响应体中，自定义的响应状态码是不是200
				if (node.get("status").asInt() == 200) {
					//取出订单id，并且返回
					return node.get("data").asText();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据订单id查询订单
	 * @param orderId
	 * @return
	 */
	public Order queryOrderByOrderId(String orderId) {
		try {
			String json = this.apiService.doGet("http://order.taotao.com/order/query/" + orderId);
			if (StringUtils.isNotBlank(json)) {
				return MAPPER.readValue(json, Order.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

}
