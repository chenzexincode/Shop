package com.shop.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSourceUtils {	
	private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
	
	private static ThreadLocal<Connection> tl =new ThreadLocal<Connection>();
	//获取数据源
	public static DataSource getDataSource() {
		return dataSource;
	}
	//获取连接
	public static Connection getConnection() {
		Connection conn=null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	//获取当前线程的连接
	public static Connection getCurrentConnection() {
		Connection conn=tl.get();
		
		if(conn==null) {
			conn=getConnection();
			
			tl.set(conn);
		}
		return conn;
	}
	//开启事务
	public static  void startTransaction() throws SQLException {
		Connection conn=getCurrentConnection();
		if(conn!=null) {
			conn.setAutoCommit(false);
		}
	}
	//提交事务并关闭资源
	public static void commitAndRelease() throws SQLException {
		Connection conn = getCurrentConnection();
		
		if(conn!=null) {
			conn.commit();
			conn.close();
			tl.remove();
		}
	}
	//事务回滚
	public static void rollback() throws SQLException {
		Connection conn=getCurrentConnection();
		
		if(conn!=null) {
			conn.rollback();
		}
	}
	// 关闭资源方法
		public static void closeConnection() throws SQLException {
			Connection con = getConnection();
			if (con != null) {
				con.close();
			}
		}

		public static void closeStatement(Statement st) throws SQLException {
			if (st != null) {
				st.close();
			}
		}

		public static void closeResultSet(ResultSet rs) throws SQLException {
			if (rs != null) {
				rs.close();
			}
		}
	
}
