package com.shop.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.shop.domain.Category;
import com.shop.domain.Product;
import com.shop.service.CategoryService;
import com.shop.service.ProductService;
import com.shop.utils.JedisUtils;
import com.shop.vo.PageBean;

import redis.clients.jedis.Jedis;

public class ProductServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
    	
	public void categoryList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		//�ȴӴ�redis�л�ȡ����
		Jedis jedis = JedisUtils.getJedis();
		String json = jedis.get("category");
		
		//���redis��û�����ݣ������ݿ��л�ȡ
		if(json==null) {
			//����CategoryService�ķ���
			CategoryService service = new CategoryService();
			List<Category> categoryList = null;
			try {
				categoryList = service.allCategoryList();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//�����ݷ�װ��json��ʽ������
			Gson gson = new Gson();
			json = gson.toJson(categoryList);
			
			//������json��ʽ�Ĵ浽redis��
			jedis.set("category", json);
		}
		
		//�����ݷ��ظ����õ�ajax
		response.getWriter().write(json);
		
	}
	
	
	public void index(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		int index = 1;
		int count = 9;
		
		// ׼��������Ʒ����		
		ProductService service = new ProductService();
		List<Product> hotProductList = null;
		try {
			hotProductList = service.findHotProductList(index, count);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("hotProductList", hotProductList);

		// ׼��������Ʒ����
		List<Product> newProductList = null;
		try {
			newProductList = service.findNewProductList(index, count);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("newProductList", newProductList);
		
		
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
	
	
	public void productListByCid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		// ��ȡҳ�����
		String cid = request.getParameter("cid");
		String currentPageStr = request.getParameter("page");

		// ���������ļ�
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("productListByCidServlet.properties");
		Properties prop = new Properties();
		prop.load(inputStream);
		
		int currentPage = Integer.parseInt(prop.getProperty("currentPage"));
		int currentCount = Integer.parseInt(prop.getProperty("currentCount"));

		if(currentPageStr!=null) {
			currentPage=Integer.parseInt(currentPageStr);
		}
		
		// ������Ʒ����ѯ�������з�ҳ������ProductService
		ProductService service = new ProductService();
		PageBean<Product> pageBean = null;
		try {
			pageBean = service.findProductListByCid(cid, currentPage, currentCount);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);
		
		//��������¼
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie cookie : cookies) {
				if("pids".equals(cookie.getName())){
					String value = cookie.getValue();
					//��pids��cookieֵ�����и�
					String[] split = value.split(",");
					//��ѯ����װ
					List<Product> browseList = new ArrayList<Product>();
					for(int i=0;i<split.length&&i<7;i++) {
						try {
							Product product = service.findProductByPid(split[i]);
							browseList.add(product);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					request.setAttribute("browseList", browseList);
				}
			}
		}
		
		request.getRequestDispatcher("/product_list.jsp").forward(request, response);
	}
	
	
	
	public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		//��ȡҳ�����
		String pid = request.getParameter("pid");
		String page = request.getParameter("page");
		
		//����ProductService����
		ProductService service = new ProductService();
		
		Product product = new Product();
		try {
			product = service.findProductByPid(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("page", page);
		request.setAttribute("product", product);
		
		//��ñ�����Ʒ��pid
		String pids = product.getPid();		
		
		//��ȡcookie
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie cookie : cookies) {
				if("pids".equals(cookie.getName())) {
					String value = cookie.getValue();
					
					//�ѻ�ȡ��cookie��pids���ݶ��Žس����顣�ٰ������ɼ��ϡ��������
					String[] split = value.split(",");
					List<String> asList = Arrays.asList(split);
					LinkedList<String> list = new LinkedList<String>(asList);
					
					//�Ƴ������е�pidԪ�أ�����pid��ӵ���һ��Ԫ��
					list.remove(pids);
					list.addFirst(pids);
					
					//�Դ�����Ľ������ƴ��
					StringBuffer buf = new StringBuffer();
					for(String str : list) {
						buf.append(str);
						buf.append(",");
					}
					pids = buf.substring(0, buf.length()-1);
				}
			}
		}
		
		//�����µ�Cookie�����͵��ͻ���
		Cookie cookie = new Cookie("pids",pids);
		response.addCookie(cookie);		
			
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
}
