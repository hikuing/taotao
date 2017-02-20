package com.taotao.cart.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.taotao.cart.bean.Item;
import com.taotao.cart.bean.User;
import com.taotao.cart.mapper.CartMapper;
import com.taotao.cart.pojo.Cart;
import com.taotao.cart.threadlocal.UserLoginThreadLocal;

@Service
public class CartService {
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ItemService itemService;

	/**
	 * 加入购物车
	 * 先根据用户id和商品id查询购物车中是否存在该商品，如果存在，数量+1；
	 * 如果不存在，根据商品id查询商品基本信息，并保存到数据库中
	 * @param itemId
	 */
	public void addItemToCart(Long itemId) {
		//查询购物车中是否存在该商品
		Cart record = new Cart();
		record.setItemId(itemId);
		record.setUserId(UserLoginThreadLocal.getUser().getId());
		Cart cart = cartMapper.selectOne(record);
		if (cart != null) {
			//购物车中存在该商品，数量+1
			cart.setNum(cart.getNum() + 1);
			cart.setUpdated(new Date());
			this.cartMapper.updateByPrimaryKeySelective(cart);
		}else {
			//购物车中没有该商品，添加进购物车
			cart = new Cart();
			//组装购物车信息
			cart.setCreated(new Date());
			cart.setItemId(itemId);
			cart.setNum(1);
			cart.setUpdated(cart.getCreated());
			cart.setUserId(UserLoginThreadLocal.getUser().getId());
			//商品相关信息没有，需要从后台查询
			Item item = this.itemService.queryItemById(itemId);
			if (item == null) {
				//商品信息不存在，不做处理
				return;
			}
			cart.setItemImage(item.getImage());
			cart.setItemPrice(item.getPrice());
			cart.setItemTitle(item.getTitle());
			//把组装好的购物车信息，保存到数据库
			this.cartMapper.insert(cart);
		}
	}

	/**
	 * 根据用户id查询购物车信息
	 * @param id
	 * @return
	 */
	public List<Cart> queryCartByUserId(Long userId) {
		//按照加入购物车时间倒序排序
		Example example = new Example(Cart.class);
		example.setOrderByClause("created DESC");
		example.createCriteria().andEqualTo("userId", userId);
		return this.cartMapper.selectByExample(example);
	}

	/**
	 * 修改商品数量,update set num = ? where userId = ? and itemId = ?
	 * @param itemId
	 * @param num
	 */
	public void updateItemNum(Long itemId, Integer num) {
		User user = UserLoginThreadLocal.getUser();
		Example example = new Example(Cart.class);
		example.createCriteria().andEqualTo("userId", user.getId()).andEqualTo("itemId", itemId);
		Cart record = new Cart();
		record.setNum(num);
		record.setUpdated(new Date());
		this.cartMapper.updateByExampleSelective(record, example);
	}

	/**
	 * 删除商品
	 * delete .. from .. where userId = ? and itemId = ?
	 * @param itemId
	 */
	public void deleteCartByitemId(Long itemId) {
		Cart record = new Cart();
		record.setItemId(itemId);
		record.setUserId(UserLoginThreadLocal.getUser().getId());
		this.cartMapper.delete(record);
	}

}
