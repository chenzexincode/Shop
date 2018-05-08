package com.shop.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import com.shop.domain.Category;
import com.shop.service.CategoryService;
import com.shop.utils.CommonsUtils;

public class AdminCategoryServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	//ɾ������ģ��
	public void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		
		CategoryService service = new CategoryService();
		try {
			service.deleteCategoryByCid(cid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.sendRedirect(request.getContextPath()+"/adminCategory?method=categoryList");
	}
	
	
	//�޸ķ���ģ��
	public void updateCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		String cname = request.getParameter("cname");
		
		CategoryService service = new CategoryService();
		try {
			service.updateCategory(cid,cname);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.sendRedirect(request.getContextPath()+"/adminCategory?method=categoryList");
		
	}
	
	
	//����༭ҳģ��
	public void editCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
	
		CategoryService service = new CategoryService();
		Category category = null;
		try {
			category = service.findCategoryByCid(cid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("category", category);
		request.getRequestDispatcher("/admin/category/edit.jsp").forward(request, response);
	}
	
	//�����б�
	public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CategoryService service = new CategoryService();
		List<Category> categoryList =null;
		try {
			categoryList = service.allCategoryList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("categoryList", categoryList);
		request.getRequestDispatcher("/admin/category/list.jsp").forward(request, response);
	}
	
	//�����Ʒ����
	public void addCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//��ȡҳ�����
		Map<String, String[]> map = request.getParameterMap();
		
		//��װ����
		Category category = new Category();
		try {
			BeanUtils.populate(category, map);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		category.setCid(CommonsUtils.getUUID());
		
		//����service
		CategoryService service = new CategoryService();
		try {
			service.addCategory(category);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//�ض���
		response.sendRedirect(request.getContextPath()+"/adminCategory?method=categoryList");
	}

}
