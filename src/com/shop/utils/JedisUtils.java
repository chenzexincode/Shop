package com.shop.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtils {
	//�������ӳض���
	private static JedisPool pool = null;

	static {
		//���������ļ�
		InputStream inputStream = JedisUtils.class.getClassLoader().getResourceAsStream("jedisUtils.properties");
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//������ӳض���
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.parseInt(prop.getProperty("redis.maxIdle")));
		poolConfig.setMinIdle(Integer.parseInt(prop.getProperty("redis.minIdle")));
		poolConfig.setMaxTotal(Integer.parseInt(prop.getProperty("redis.maxTotal")));
		pool = new JedisPool(poolConfig, prop.getProperty("redis.url"), Integer.parseInt(prop.getProperty("redis.port")));
	}
	
	//��ȡJedis����ķ���
	public static Jedis getJedis() {
		return pool.getResource();
	}

}
