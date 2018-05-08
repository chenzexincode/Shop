package com.shop.service;

import java.sql.SQLException;
import java.util.List;

import com.shop.dao.ProductDao;
import com.shop.domain.Product;
import com.shop.vo.PageBean;

public class ProductService {
	//���������Ʒ�б�
	public List<Product> findHotProductList(int index, int count) throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findHotProductList(index, count);
	}

	//���������Ʒ�б�
	public List<Product> findNewProductList(int index, int count) throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findNewProductList(index, count);
	}

	//����cid��ȡ��Ʒ�б�
	public PageBean<Product> findProductListByCid(String cid,int currentPage,int currentCount) throws SQLException {
		PageBean<Product> pageBean = new PageBean<Product>();
		ProductDao dao = new ProductDao();
		
		//����ҳ��		private int currentPage;
		pageBean.setCurrentPage(currentPage);
		
		//��ǰҳ����	private int currentCount;
		pageBean.setCurrentCount(currentCount);
		
		//������		private int totalCount;
		int totalCount = dao.getCountByCid(cid);
		pageBean.setTotalCount(totalCount);
		
		//��ҳ��		private int totalPage;
		int totalPage = (int) Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		
		//ÿҳ����		private List<T> list = new ArrayList<T>();
		int index = (currentPage-1)*currentCount;
		List<Product> list = dao.findProductListByCid(cid,index,currentCount);
		pageBean.setList(list);
		
		return pageBean;
	}

	public Product findProductByPid(String pid) throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findProductByPid(pid);
	}

	//����������Ʒ
	public List<Product> findAllProduct() throws SQLException {
		ProductDao dao = new ProductDao();
		return dao.findAllProduct();
		
	}

	public void addProduct(Product product) throws SQLException {
		ProductDao dao = new ProductDao();
		dao.addProduct(product);
	}

}
