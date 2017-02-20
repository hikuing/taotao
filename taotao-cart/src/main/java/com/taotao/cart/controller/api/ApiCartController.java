package com.taotao.cart.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.cart.pojo.Cart;
import com.taotao.cart.service.CartService;

@Controller
@RequestMapping(value="api/cart")
public class ApiCartController {

	@Autowired
	private CartService cartService;
	
	/**
	 * 根据用户id查询购物车信息
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="{userId}",method=RequestMethod.GET)
	public ResponseEntity<List<Cart>> queryCartsByUserId(@PathVariable("userId") Long userId) {
		try {
			List<Cart> carts = cartService.queryCartByUserId(userId);
			if (carts == null || carts.size() == 0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(carts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
