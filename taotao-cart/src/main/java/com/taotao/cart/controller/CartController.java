package com.taotao.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.cart.bean.User;
import com.taotao.cart.pojo.Cart;
import com.taotao.cart.service.CartCookieService;
import com.taotao.cart.service.CartService;
import com.taotao.cart.threadlocal.UserLoginThreadLocal;

@Controller
@RequestMapping(value="cart")
public class CartController {
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private CartCookieService cartCookieService;

	/**
	 * 添加购物车
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ModelAndView addItemToCart(@PathVariable("itemId") Long itemId,
			HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("redirect:/cart/show.html");
		//判断用户是否登录
		User user = UserLoginThreadLocal.getUser();
		if (user == null) {
			//没有登录，保存进cookie中
			this.cartCookieService.addItemToCart(itemId,request,response);
		}else {
			//已登录，商品加入购物车
			this.cartService.addItemToCart(itemId);
		}
		return view;
	}
	
	/**
	 * 显示购物车信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value="show")
	public ModelAndView show(HttpServletRequest request) {
		ModelAndView view = new ModelAndView("cart");
		//判断用户是否登录
		User user = UserLoginThreadLocal.getUser();
		if (user == null) {
			//没有登录,从cookie中查询购物车信息
			List<Cart> carts = this.cartCookieService.queryCarts(request);
			//把购物车信息添加到view中，传递到页面
			view.addObject("cartList", carts);
		}else {
			//已登录，根据用户id查询购物车信息
			List<Cart> carts = this.cartService.queryCartByUserId(user.getId());
			//把购物车信息添加到view中，传递到页面
			view.addObject("cartList", carts);
		}
		return view;
	}
	
	/**
	 * 修改商品的数量
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="update/num/{itemId}/{num}",method=RequestMethod.POST)
	public ResponseEntity<Void> updateItemNum(
			@PathVariable("itemId") Long itemId,@PathVariable("num") Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		try {
			//判断用户是否登录
			User user = UserLoginThreadLocal.getUser();
			if (user == null) {
				//没有登录
				this.cartCookieService.updateItemNum(itemId,num,request,response);
			}else {
				//已登录
				this.cartService.updateItemNum(itemId,num);
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 删除商品
	 * @param itemId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="delete/{itemId}",method=RequestMethod.GET)
	public String deleteCart(@PathVariable("itemId") Long itemId,
			HttpServletRequest request,HttpServletResponse response) {
		//判断用户是否已经登录
		User user = UserLoginThreadLocal.getUser();
		if (user == null) {
			//没有登录
			this.cartCookieService.deleteCartByItemId(itemId,request,response);
		}else {
			//已经登录
			this.cartService.deleteCartByitemId(itemId);
		}
		return "redirect:/cart/show.html";
	}
}
