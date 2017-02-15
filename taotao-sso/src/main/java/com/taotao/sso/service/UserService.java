package com.taotao.sso.service;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.redis.RedisService;
import com.taotao.sso.mapper.UserMapper;
import com.taotao.sso.pojo.User;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RedisService redisService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Integer TIME_OUT = 60 * 30;
	private static final String KEY = "TAOTAO_SSO_USER_"; 

	/**
	 * 验证用户信息
	 * @param param
	 * @param type 1 用户名
	 * @param type 2 手机
	 * @param type 3 邮箱
	 * @return
	 */
	public Boolean checkParam(String param, Integer type) {
		User record = new User();
		switch (type) {
		case 1:
			//校验用户名
			record.setUsername(param);
			break;
		case 2:
			//校验手机号
			record.setPhone(param);
			break;
		case 3:
			//校验邮箱
			record.setEmail(param);
			break;
		default:
			return null;
		}
		//可用的时候
		return userMapper.selectOne(record) == null;
	}

	/**
	 * 保存用户注册信息
	 * @param user
	 * @return
	 */
	public Boolean doRegister(User user) {
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		user.setId(null);
		//对密码进行MD5加密
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		//添加成功，返回true
		return userMapper.insertSelective(user) == 1;
	}

	/**
	 * 用户登录
	 * @param user
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String doLogin(User user) throws JsonProcessingException {
		/**
		 * 1、用户名和密码进行校验
		 * 2、匹配成功
		 *          把用户信息保存到redis中
		 *          生成一个 对应的key === token == 值
		 *    匹配失败
		 *      直接返回失败
		 */
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		User user2 = this.userMapper.selectOne(user);
		if (user2 != null) {
			//查询到了数据，登录成功
			String token = DigestUtils.md5Hex(user2.getId() + "" + System.currentTimeMillis());
			this.redisService.set(KEY + token, MAPPER.writeValueAsString(user2), TIME_OUT);
			return token;
		}
		return null;
	}
	
	/**
	 * 根据token查询用户信息
	 * @param token
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public User queryUserByToken(String token) throws JsonParseException, JsonMappingException, IOException {
		String json = this.redisService.get(KEY + token);
		if (StringUtils.isNotBlank(json)) {
			//重置token的有效时间
			this.redisService.expire(KEY + token, TIME_OUT);
			return MAPPER.readValue(json, User.class);
		}
		return null;
	}
	
}
