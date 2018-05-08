package com.shop.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtils {
	//创建连接池对象
	private static JedisPool pool = null;

	static {
		//加载配置文件
		InputStream inputStream = JedisUtils.class.getClassLoader().getResourceAsStream("jedisUtils.properties");
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//获得连接池对象
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.parseInt(prop.getProperty("redis.maxIdle")));
		poolConfig.setMinIdle(Integer.parseInt(prop.getProperty("redis.minIdle")));
		poolConfig.setMaxTotal(Integer.parseInt(prop.getProperty("redis.maxTotal")));
		pool = new JedisPool(poolConfig, prop.getProperty("redis.url"), Integer.parseInt(prop.getProperty("redis.port")));
	}
	
	//获取Jedis对象的方法
	public static Jedis getJedis() {
		return pool.getResource();
	}

}
