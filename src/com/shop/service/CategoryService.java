package com.shop.service;

import java.sql.SQLException;
import java.util.List;

import com.shop.dao.CategoryDao;
import com.shop.domain.Category;

public class CategoryService {

	
	//查找所有Category
	public List<Category> allCategoryList() throws SQLException {
		CategoryDao dao = new CategoryDao();
		
		return dao.allCategoryList();
	}

	//添加Category
	public boolean addCategory(Category category) throws SQLException {
		CategoryDao dao = new CategoryDao();
		
		int addCategory = dao.addCategory(category);
		
		return addCategory>0;
	}
	
	//根据cid查找Category
	public Category findCategoryByCid(String cid) throws SQLException {
		CategoryDao dao = new CategoryDao();
		
		return dao.findCategoryByCid(cid);
	}

	//根据cid修改cname
	public boolean updateCategory(String cid, String cname) throws SQLException {
		CategoryDao dao = new CategoryDao();
		
		int updateCategory = dao.updateCategory(cid,cname);
		
		return updateCategory>0;
	}

	//根据cid删除Category
	public boolean deleteCategoryByCid(String cid) throws SQLException {
		CategoryDao dao = new CategoryDao();
		
		int deleteCategory = dao.deleteCategoryByCid(cid);
		
		return deleteCategory>0;
	}
}
