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
    
	
	
	
	//�����Ʒģ��
	public void addProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.���������ļ�����
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*1024);//���û����ļ���С��������λΪ�ֽڣ�����1M�����ڴ�����ʱ�ļ�
		//������ʱ�ļ��Ĵ��λ��
		String tempPath = this.getServletContext().getRealPath("temp");
		factory.setRepository(new File(tempPath));
		
		//2.�����ļ��ϴ�������
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		//3.����request����ļ����
		try {
			List<FileItem> parseRequest = upload.parseRequest(request);
			
			//����һ��map���ϣ�������װҳ������
			Map<String,Object> map = new HashMap<String,Object>();
			Product product = new Product();
			Date date = new Date();
			//4.�����ļ����
			for(FileItem fileItem : parseRequest) {
				//5.�ж��Ƿ���ͨ����
				boolean formField = fileItem.isFormField();
				if(formField) {
					//��ͨ����
					String fieldName = fileItem.getFieldName();
					String value = fileItem.getString("UTF-8");
					map.put(fieldName,value);
				}
				else {
					//�ļ��ϴ���
					String name = fileItem.getName();
					InputStream input = fileItem.getInputStream();
					
					//�ļ�����·��
					String uploadPath = this.getServletContext().getRealPath("upload");
					
					//��ȡ����ʱ���
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
					String str = format.format(date);
					
					//�ļ�����������֤Ψһ��
					String fileName = str+name;
					
					OutputStream output = new FileOutputStream(new File(uploadPath+"/"+fileName));
					IOUtils.copy(input,output);
					fileItem.delete();
			
					//private String pimage;
					product.setPimage("upload/"+fileName);

				}
			}
			
			//��װproduct
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
	
	//���ҷ����б�
	public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//����Service
		CategoryService service = new CategoryService();
		List<Category> categoryList = null;
		try {
			categoryList = service.allCategoryList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//��categoryListת����json��ʽ
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		
		//��json���ظ��ͻ���
		response.getWriter().write(json);
	}
	
	
	//��Ʒ�б�
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
