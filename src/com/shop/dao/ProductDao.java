package com.shop.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.shop.domain.Product;
import com.shop.utils.DataSourceUtils;

public class ProductDao {

	public List<Product> findProductListByIs_hot(int is_hot, int index, int count) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

		String sql = "select * from product where is_hot=? limit ?,?";

		return runner.query(sql, new BeanListHandler<Product>(Product.class), is_hot, index, count);
	}

	public List<Product> findHotProductList(int index, int count) throws SQLException {

		return this.findProductListByIs_hot(1, index, count);
	}

	public List<Product> findNewProductList(int index, int count) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

		String sql = "select * from product order by pdate limit ?,?";

		return runner.query(sql, new BeanListHandler<Product>(Product.class), index, count);
	}

	public int getCountByCid(String cid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

		String sql = "select count(*) from product where cid=?";

		Long query = (Long) runner.query(sql, new ScalarHandler(), cid);

		return query.intValue();

	}

	public List<Product> findProductListByCid(String cid, int index, int count) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

		String sql = "select * from product where cid=? limit ?,?";

		return runner.query(sql, new BeanListHandler<Product>(Product.class), cid, index, count);

	}

	public Product findProductByPid(String pid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from product where pid=?";
		
		return runner.query(sql, new BeanHandler<Product>(Product.class),pid);
	}

	//查找所有商品
	public List<Product> findAllProduct() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from product";
		
		return runner.query(sql, new BeanListHandler<Product>(Product.class));
	}
	
	//添加商品
	public void addProduct(Product product) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "insert into product values(?,?,?,?,?,?,?,?,?,?)";
		
		runner.update(sql,product.getPid(),product.getPname(),product.getMarket_price(),product.getShop_price(),product.getPimage(),product.getPdate(),product.getIs_hot(),product.getPdesc(),product.getPflag(),product.getCid());
		
	}

}
