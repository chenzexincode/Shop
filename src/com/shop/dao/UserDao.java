package com.shop.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.shop.domain.User;
import com.shop.utils.DataSourceUtils;

public class UserDao {

	public int addUser(User user) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

		String sql = "insert into user values(?,?,?,?,?,?,?,?,?,?)";

		int update = runner.update(sql, user.getUid(), user.getUsername(), user.getPassword(), user.getName(), user.getEmail(),
				user.getTelephone(), user.getBirthday(), user.getSex(), user.getState(), user.getCode());
		
		return update;
	}

	public int updateStateByCode(String code) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "update user set state=1 where code=?";
		
		int update = runner.update(sql, code);
		
		return update;
	}

	public User selectUserByUsername(String username) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from user where username=?";
		
		User user = runner.query(sql, new BeanHandler<User>(User.class),username);
		
		return user;
		
	}

	public User selectUserByUsernameAndPassword(String username, String password) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from user where username=? and password=?";
		
		User user = runner.query(sql, new BeanHandler<User>(User.class),username,password);
		
		return user;
		
	}

}
