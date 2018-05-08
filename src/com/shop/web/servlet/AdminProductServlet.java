package com.shop.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.shop.domain.Category;
import com.shop.domain.Product;
import com.shop.service.CategoryService;
import com.shop.service.ProductService;
import com.shop.utils.CommonsUtils;

public class AdminProductServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
    
	
	
	
	//添加商品模块
	public void addProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.创建磁盘文件工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*1024);//设置缓存文件大小，基本单位为字节，超过1M则存放在磁盘临时文件
		//设置临时文件的存放位置
		String tempPath = this.getServletContext().getRealPath("temp");
		factory.setRepository(new File(tempPath));
		
		//2.创建文件上传核心类
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		//3.解析request获得文件项集合
		try {
			List<FileItem> parseRequest = upload.parseRequest(request);
			
			//创建一个map集合，用来封装页面数据
			Map<String,Object> map = new HashMap<String,Object>();
			Product product = new Product();
			Date date = new Date();
			//4.遍历文件项集合
			for(FileItem fileItem : parseRequest) {
				//5.判断是否普通表单项
				boolean formField = fileItem.isFormField();
				if(formField) {
					//普通表单项
					String fieldName = fileItem.getFieldName();
					String value = fileItem.getString("UTF-8");
					map.put(fieldName,value);
				}
				else {
					//文件上传项
					String name = fileItem.getName();
					InputStream input = fileItem.getInputStream();
					
					//文件保存路径
					String uploadPath = this.getServletContext().getRealPath("upload");
					
					//获取生成时间戳
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
					String str = format.format(date);
					
					//文件重命名，保证唯一性
					String fileName = str+name;
					
					OutputStream output = new FileOutputStream(new File(uploadPath+"/"+fileName));
					IOUtils.copy(input,output);
					fileItem.delete();
			
					//private String pimage;
					product.setPimage("upload/"+fileName);

				}
			}
			
			//封装product
			try {
				BeanUtils.populate(product, map);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			private String pid;
			product.setPid(CommonsUtils.getUUID());

//			private Date pdate;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String time = format.format(date);
			try {
				product.setPdate(format.parse(time));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
//			private int pflag;
			product.setPflag(0);
			
			ProductService service = new ProductService();
			try {
				service.addProduct(product);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.sendRedirect(request.getContextPath()+"/adminProduct?method=productList");
	}
	
	//查找分类列表
	public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//调用Service
		CategoryService service = new CategoryService();
		List<Category> categoryList = null;
		try {
			categoryList = service.allCategoryList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//把categoryList转换成json格式
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		
		//把json返回给客户端
		response.getWriter().write(json);
	}
	
	
	//商品列表
	public void productList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		List<Product> productList = null;
		try {
			productList = service.findAllProduct();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("productList", productList);
		request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);
	}

}
