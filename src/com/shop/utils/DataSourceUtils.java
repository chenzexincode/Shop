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
	//��ȡ����Դ
	public static DataSource getDataSource() {
		return dataSource;
	}
	//��ȡ����
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
	//��ȡ��ǰ�̵߳�����
	public static Connection getCurrentConnection() {
		Connection conn=tl.get();
		
		if(conn==null) {
			conn=getConnection();
			
			tl.set(conn);
		}
		return conn;
	}
	//��������
	public static  void startTransaction() throws SQLException {
		Connection conn=getCurrentConnection();
		if(conn!=null) {
			conn.setAutoCommit(false);
		}
	}
	//�ύ���񲢹ر���Դ
	public static void commitAndRelease() throws SQLException {
		Connection conn = getCurrentConnection();
		
		if(conn!=null) {
			conn.commit();
			conn.close();
			tl.remove();
		}
	}
	//����ع�
	public static void rollback() throws SQLException {
		Connection conn=getCurrentConnection();
		
		if(conn!=null) {
			conn.rollback();
		}
	}
	// �ر���Դ����
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
