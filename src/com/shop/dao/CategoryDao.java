package com.shop.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.shop.domain.Category;
import com.shop.utils.DataSourceUtils;

public class CategoryDao {

	//查找所有category
	public List<Category> allCategoryList() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from category";
		
		return runner.query(sql, new BeanListHandler<Category>(Category.class));
	}

	//添加category
	public int addCategory(Category category) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "insert into category values(?,?)";

		return runner.update(sql,category.getCid(),category.getCname());
		
	}

	//根据cid查找分类
	public Category findCategoryByCid(String cid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "select * from category where cid=?";
		
		return runner.query(sql, new BeanHandler<Category>(Category.class),cid);
	}

	//根据cid修改分类
	public int updateCategory(String cid, String cname) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "update category set cname=? where cid=?";
		
		return runner.update(sql,cname,cid);
		
	}

	//根据cid删除分类
	public int deleteCategoryByCid(String cid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		
		String sql = "delete from category where cid=?";
		
		return runner.update(sql,cid);
	}

}
