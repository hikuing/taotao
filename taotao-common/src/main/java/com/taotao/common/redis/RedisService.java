package com.taotao.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * redis操作工具类
 * @author zhang
 *
 */
@Service
public class RedisService {

	@Autowired(required = false)//只有当使用到这个对象的时候，才会被注入
	private ShardedJedisPool shardedJedisPool;

	public <T> T execute(Fun<T, ShardedJedis> fun) {
		ShardedJedis shardedJedis = null;
		try {
			// 从连接池中获取到jedis分片对象
			shardedJedis = shardedJedisPool.getResource();
			return fun.callback(shardedJedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != shardedJedis) {
				// 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
				shardedJedis.close();
			}
		}
		return null;
	}

	/**
	 * get set del expire 设置生存时间
	 */
	public String set(final String key, final String value) {
		return this.execute(new Fun<String, ShardedJedis>() {
			@Override
			public String callback(ShardedJedis e) {
				return e.set(key, value);
			}
		});
	}

	/**
	 * 设置参数值的时候，直接设置生存时间
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public String set(final String key, final String value,
			final Integer seconds) {
		return this.execute(new Fun<String, ShardedJedis>() {
			@Override
			public String callback(ShardedJedis e) {
				return e.setex(key, seconds, value);
			}
		});
	}

	/**
	 * get方法
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		return this.execute(new Fun<String, ShardedJedis>() {
			@Override
			public String callback(ShardedJedis e) {
				return e.get(key);
			}
		});
	}

	/**
	 * 删除一个key
	 * @param key
	 * @return
	 */
	public Long del(final String key) {
		return this.execute(new Fun<Long, ShardedJedis>() {
			@Override
			public Long callback(ShardedJedis e) {
				return e.del(key);
			}
		});
	}
	

	/**
	 * 设置有效时间 key 时间，单位秒
	 * 
	 * @return
	 */
	public Long expire(final String key, final Integer seconds) {
		return this.execute(new Fun<Long, ShardedJedis>() {

			@Override
			public Long callback(ShardedJedis e) {
				return e.expire(key, seconds);
			}
		});
	}
}
