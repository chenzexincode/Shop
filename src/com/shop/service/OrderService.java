package com.shop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.shop.dao.OrderDao;
import com.shop.domain.Order;
import com.shop.domain.Orderitem;
import com.shop.utils.DataSourceUtils;

public class OrderService {

	public boolean submitOrder(Order order) {
		OrderDao dao = new OrderDao();
		
		boolean flag = true;
		//������Ҫִ�ж���sql����Ҫ����һ������
		try {
			DataSourceUtils.startTransaction();
			//��Ӷ���
			dao.addOrder(order);
			
			//��Ӷ��������
			List<Orderitem> list = order.getList();
			for(Orderitem orderitem : list) {
				dao.addOrderitem(orderitem);
			}
		
		} catch (SQLException e) {
			//�����쳣ʱ���ع�����
			try {
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			flag = false;
		}finally {
			//�ύ����
			try {
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return flag;
	}

	public boolean updateOrderByOid(String oid,String address,String name,String telephone) throws SQLException {
		OrderDao dao = new OrderDao();
		int updateOrderByOid = dao.updateOrderByOid(oid,address,name,telephone);
		return updateOrderByOid>0;
		
	}

	public Order findOrderByOid(String oid) throws SQLException {
		OrderDao dao = new OrderDao();
		return dao.findOrderByOid(oid);	
	}

	public boolean updateStateByOid(String oid) throws SQLException {
		OrderDao dao = new OrderDao();
		int updateStateByOid = dao.updateStateByOid(oid);
		return updateStateByOid>0;
		
	}

	public List<Order> findOrderListByUid(String uid) throws SQLException {
		OrderDao dao = new OrderDao();
		return dao.findOrderListByUid(uid);
	}

	public List<Map<String,Object>> findProductAndOrderListByOid(String oid) throws SQLException {
		OrderDao dao = new OrderDao();
		return dao.findProductAndOrderListByOid(oid);
	}

	public List<Order> findAllOrder() throws SQLException {
		OrderDao dao = new OrderDao();
		return dao.findAllOrder();
	}

}
