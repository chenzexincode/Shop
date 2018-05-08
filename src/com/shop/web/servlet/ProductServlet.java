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
		
		//先从从redis中获取数据
		Jedis jedis = JedisUtils.getJedis();
		String json = jedis.get("category");
		
		//如果redis中没有数据，则到数据库中获取
		if(json==null) {
			//调用CategoryService的方法
			CategoryService service = new CategoryService();
			List<Category> categoryList = null;
			try {
				categoryList = service.allCategoryList();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//把数据封装成json格式的数据
			Gson gson = new Gson();
			json = gson.toJson(categoryList);
			
			//把数据json格式的存到redis中
			jedis.set("category", json);
		}
		
		//把数据返回给调用的ajax
		response.getWriter().write(json);
		
	}
	
	
	public void index(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		int index = 1;
		int count = 9;
		
		// 准备热门商品数据		
		ProductService service = new ProductService();
		List<Product> hotProductList = null;
		try {
			hotProductList = service.findHotProductList(index, count);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("hotProductList", hotProductList);

		// 准备最新商品数据
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

		// 获取页面参数
		String cid = request.getParameter("cid");
		String currentPageStr = request.getParameter("page");

		// 加载配置文件
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("productListByCidServlet.properties");
		Properties prop = new Properties();
		prop.load(inputStream);
		
		int currentPage = Integer.parseInt(prop.getProperty("currentPage"));
		int currentCount = Integer.parseInt(prop.getProperty("currentCount"));

		if(currentPageStr!=null) {
			currentPage=Integer.parseInt(currentPageStr);
		}
		
		// 根据商品类别查询，并进行分页。调用ProductService
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
		
		//最近浏览记录
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie cookie : cookies) {
				if("pids".equals(cookie.getName())){
					String value = cookie.getValue();
					//对pids的cookie值进行切割
					String[] split = value.split(",");
					//查询并封装
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
		
		//获取页面参数
		String pid = request.getParameter("pid");
		String page = request.getParameter("page");
		
		//调用ProductService方法
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
		
		//获得本次商品的pid
		String pids = product.getPid();		
		
		//获取cookie
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie cookie : cookies) {
				if("pids".equals(cookie.getName())) {
					String value = cookie.getValue();
					
					//把获取到cookie的pids根据逗号截成数组。再把数组变成集合。方便操作
					String[] split = value.split(",");
					List<String> asList = Arrays.asList(split);
					LinkedList<String> list = new LinkedList<String>(asList);
					
					//移除集合中的pid元素，并把pid添加到第一个元素
					list.remove(pids);
					list.addFirst(pids);
					
					//对处理完的结果重新拼串
					StringBuffer buf = new StringBuffer();
					for(String str : list) {
						buf.append(str);
						buf.append(",");
					}
					pids = buf.substring(0, buf.length()-1);
				}
			}
		}
		
		//创建新的Cookie并发送到客户端
		Cookie cookie = new Cookie("pids",pids);
		response.addCookie(cookie);		
			
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
}
