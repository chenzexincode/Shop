package com.shop.service;

import java.sql.SQLException;
import java.util.List;

import com.shop.dao.ProductDao;
import com.shop.domain.Product;
import com.shop.vo.PageBean;

public class ProductService {
	//获得热门商品列表
	public List<Product> findHotProductList(int index, int count) throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findHotProductList(index, count);
	}

	//获得最新商品列表
	public List<Product> findNewProductList(int index, int count) throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findNewProductList(index, count);
	}

	//根据cid获取商品列表
	public PageBean<Product> findProductListByCid(String cid,int currentPage,int currentCount) throws SQLException {
		PageBean<Product> pageBean = new PageBean<Product>();
		ProductDao dao = new ProductDao();
		
		//当期页数		private int currentPage;
		pageBean.setCurrentPage(currentPage);
		
		//当前页条数	private int currentCount;
		pageBean.setCurrentCount(currentCount);
		
		//总条数		private int totalCount;
		int totalCount = dao.getCountByCid(cid);
		pageBean.setTotalCount(totalCount);
		
		//总页数		private int totalPage;
		int totalPage = (int) Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		
		//每页数据		private List<T> list = new ArrayList<T>();
		int index = (currentPage-1)*currentCount;
		List<Product> list = dao.findProductListByCid(cid,index,currentCount);
		pageBean.setList(list);
		
		return pageBean;
	}

	public Product findProductByPid(String pid) throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findProductByPid(pid);
	}

	//查找所有商品
	public List<Product> findAllProduct() throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findAllProduct();
		
	}

	public void addProduct(Product product) throws SQLException {
		ProductDao dao = new ProductDao();
		dao.addProduct(product);
	}

}
