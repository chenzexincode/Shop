package com.shop.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.shop.domain.Order;
import com.shop.domain.Orderitem;
import com.shop.utils.DataSourceUtils;

public class OrderDao {

	//添加订单
	public void addOrder(Order order) throws SQLException {
		QueryRunner runner = new QueryRunner();
		
		String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
		
		Connection conn = DataSourceUtils.getCurrentConnection();
		
		runner.update(conn, sql,order.getOid(),order.getOrdertime(),order.getTotal(),order.getState(),order.getAddress(),order.getName(),order.getTelephont(),order.getUser().getUid());
	}

	//添加订单项
	public void addOrderitem(Orderitem orderitem) throws SQLException {
		QueryRunner runner = new QueryRunner();
		
		String sql = "insert into orderitem values(?,?,?,?,?)";
		
		Connection conn = DataSourceUtils.getCurrentConnection();
		
		runner.update(conn, sql,orderitem.getItemid(),orderitem.getCount(),orderitem.getSubtotal(),orderitem.getProduct().getPid(),orderitem.getOrder().getOid());
	}

	//修改订单信息
	public int updateOrderByOid(String oid, String address, String name, String telephone) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "update orders set address=?,name=?,telephone=? where oid=?";
		
		int update = runner.update(sql,address,name,telephone,oid);
		
		return update;
		
	}

	//根据oid找订单
	public Order findOrderByOid(String oid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from orders where oid=?";
		
		return runner.query(sql, new BeanHandler<Order>(Order.class),oid);
	}

	public int updateStateByOid(String oid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "update orders set state=? where oid=?";
		
		return runner.update(sql,1,oid);
	}

	public List<Order> findOrderListByUid(String uid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from orders where uid=?";
		
		return runner.query(sql, new BeanListHandler<Order>(Order.class),uid);
		
	}

	public List<Map<String, Object>> findProductAndOrderListByOid(String oid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select p.pimage pimage,p.pname pname,p.market_price market_price,o.count count,o.subtotal subtotal,o.itemid itemid from product p,orderitem o where p.pid=o.pid and oid=?";

		List<Map<String,Object>> query = runner.query(sql, new MapListHandler(),oid);
		
		return query;
	}

	public List<Order> findAllOrder() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from orders";
		
		return runner.query(sql, new BeanListHandler<Order>(Order.class));
	}


}
