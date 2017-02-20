package com.taotao.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.bean.Cart;
import com.taotao.web.bean.Item;
import com.taotao.web.bean.Order;
import com.taotao.web.service.CartService;
import com.taotao.web.service.ItemService;
import com.taotao.web.service.OrderService;

@Controller
@RequestMapping(value="order")
public class OrderController {

	@Autowired
	private ItemService itemService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CartService cartService;
	
	/**
	 * 进入下单页面
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ModelAndView toOrder(@PathVariable("itemId") Long itemId) {
		ModelAndView mv = new ModelAndView("order");
		Item item = this.itemService.queryItemByItemId(itemId);
		mv.addObject("item", item);
		return mv;
	}
	
	/**
	 * 提交订单
	 * @param order
	 * @return
	 */
	@RequestMapping(value="submit",method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> submitOrder(Order order) {
		try {
			String orderId = this.orderService.submitOrder(order);
			Map<String, Object> map = new HashMap<>();
			if (StringUtils.isNotBlank(orderId)) {
				map.put("status", 200);
				map.put("data", orderId);
			}else {
				map.put("status", 250);
			}
			return ResponseEntity.ok(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 跳转到提交订单成功页面
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value="success",method=RequestMethod.GET)
	public ModelAndView success(@RequestParam("id") String orderId) {
		ModelAndView mv = new ModelAndView("success");
		Order order = this.orderService.queryOrderByOrderId(orderId);
		mv.addObject("order", order);
		mv.addObject("date", new DateTime().plusDays(2).toString("MM月dd日"));
		return mv;
	}
	
	/**
	 * 跳转到订单确认页面
	 * @return
	 */
	@RequestMapping(value="create",method=RequestMethod.GET)
	public ModelAndView orderCart() {
		ModelAndView view = new ModelAndView("order-cart");
		//查询购物车信息
		List<Cart> carts = this.cartService.queryCart();
		view.addObject("carts", carts);
		return view;
	}
	
}
