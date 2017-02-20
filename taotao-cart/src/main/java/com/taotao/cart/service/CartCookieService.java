package com.taotao.cart.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.cart.bean.Item;
import com.taotao.cart.pojo.Cart;
import com.taotao.common.cookie.CookieUtils;

@Service
public class CartCookieService {

	private static final String CART_COOKIE_NAME = "TT_CART";
	private static final Integer TIME_OUT = 60 * 60 * 24 * 7;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Autowired
	private ItemService itemService;
	
	/**
	 * 添加商品到购物车
	 * @param itemId
	 * @param request
	 * @param response
	 */
	public void addItemToCart(Long itemId, HttpServletRequest request, HttpServletResponse response) {
		try {
			List<Cart> carts;
			//从cookie中获取所有商品的数据
			String cartsJson = CookieUtils.getCookieValue(request, CART_COOKIE_NAME, true);
			if (StringUtils.isNotBlank(cartsJson)) {
				//购物车已经存在,获取数据
				carts = MAPPER.readValue(cartsJson, MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
				Boolean flag = false;
				for (Cart cart : carts) {
					//根据商品id查询是否存在该商品
					if (cart.getItemId().longValue() == itemId.longValue()) {
						//商品存在，数量+1
						cart.setNum(cart.getNum() + 1);
						cart.setUpdated(new Date());
						flag = true;
						break;
					}
				}
				if (! flag) {
					//商品不存在,当前购物车中没有该商品
					Cart cart = new Cart();
					//组装购物车信息
					cart.setCreated(new Date());
					cart.setItemId(itemId);
					cart.setNum(1);
					cart.setUpdated(cart.getCreated());
					//商品信息相关数据没有,调用itemService去后台查询
					Item item = this.itemService.queryItemById(itemId);
					if (item == null) {
						//商品信息不存在
						return ;
					}
					cart.setItemImage(item.getImage());
					cart.setItemPrice(item.getPrice());
					cart.setItemTitle(item.getTitle());
					
					carts.add(cart);
				}
			}else {
				//购物车是空的
				carts = new ArrayList<>();
				//创建一个新的cart
				Cart cart = new Cart();
				//组装购物车信息
				cart.setItemId(itemId);
				cart.setNum(1);
				cart.setCreated(new Date());
				cart.setUpdated(cart.getCreated());
				//商品信息相关数据没有,调用itemService去后台查询
				Item item = this.itemService.queryItemById(itemId);
				if (item == null) {
					//商品信息不存在
					return ;
				}
				cart.setItemImage(item.getImage());
				cart.setItemPrice(item.getPrice());
				cart.setItemTitle(item.getTitle());
				
				carts.add(cart);
			}
			//把新的购物车信息写入到cookie中
			CookieUtils.setCookie(request, response, CART_COOKIE_NAME, MAPPER.writeValueAsString(carts), TIME_OUT, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从cookie中查询购物车
	 * @param request
	 * @return
	 */
	public List<Cart> queryCarts(HttpServletRequest request) {
		try {
			String cartsJson = CookieUtils.getCookieValue(request, CART_COOKIE_NAME, true);
			if (StringUtils.isNotBlank(cartsJson)) {
				//购物车存在,返回数据
				return MAPPER.readValue(cartsJson, MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改购物车中商品的数量
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 */
	public void updateItemNum(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
		try {
			//从cookie中获取所有的购物车信息
			List<Cart> carts = this.queryCarts(request);
			if (carts != null) {
				for (Cart cart : carts) {
					if (cart.getItemId().longValue() == itemId.longValue()) {
						//修改数量
						cart.setNum(num);
						cart.setUpdated(new Date());
					}
				}
			}
			//把新的购物车信息写入到cookie中
			CookieUtils.setCookie(request, response, CART_COOKIE_NAME, MAPPER.writeValueAsString(carts), TIME_OUT, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除商品
	 * @param itemId
	 * @param response 
	 * @param request 
	 */
	public void deleteCartByItemId(Long itemId, HttpServletRequest request, HttpServletResponse response) {
		try {
			//从cookie中获取所有的购物车信息
			List<Cart> carts = this.queryCarts(request);
			if (carts != null) {
				for (Cart cart : carts) {
					if (cart.getItemId().longValue() == itemId.longValue()) {
						//从购物车中移除
						carts.remove(cart);
						CookieUtils.setCookie(request, response, CART_COOKIE_NAME, MAPPER.writeValueAsString(carts), TIME_OUT, true);
						return ;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	
}
