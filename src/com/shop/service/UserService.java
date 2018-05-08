package com.shop.service;

import java.sql.SQLException;

import com.shop.dao.UserDao;
import com.shop.domain.User;

public class UserService {

	public boolean regist(User user) throws SQLException {
		UserDao dao = new UserDao();
		
		int addUser = dao.addUser(user);
		
		return addUser>0;
	}

	public boolean active(String code) throws SQLException {
		UserDao dao = new UserDao();
		
		int update = dao.updateStateByCode(code);
		
		return update>0;
		
	}

	public boolean checkUsername(String username) throws SQLException {
		UserDao dao = new UserDao();
		
		User user = dao.selectUserByUsername(username);
		
		return user==null;
	}

	public User login(String username,String password) throws SQLException {
		UserDao dao = new UserDao();
		return dao.selectUserByUsernameAndPassword(username,password);
	}
}
