package com.shop.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.shop.domain.User;
import com.shop.service.UserService;
import com.shop.utils.CommonsUtils;
import com.shop.utils.MD5Utils;
import com.shop.utils.MailUtils;

public class UserSerlvet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	//用户注销模块
	public void logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		//移除本次会话中的user
		session.removeAttribute("user");
		
		//清除保存用户密码的cookie
		//创建同名cookie
		Cookie cookie_username = new Cookie("cookie_username","");
		Cookie cookie_password = new Cookie("cookie_password","");
		//设置cookie失效
		cookie_username.setMaxAge(0);
		cookie_password.setMaxAge(0);
		//把cookie发送给客户端
		response.addCookie(cookie_username);
		response.addCookie(cookie_password);
		
		response.sendRedirect(request.getContextPath()+"/login.jsp");
	}
	
	
	//用户登录模块
	public void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		//安全判断
		if (username != null && !username.equals("") && password != null && !password.equals("")) {
			UserService service = new UserService();
			User user = null;
			try {
				user = service.login(username, MD5Utils.md5(password));//因为数据库中的密码字段经过MD5加密过，所以必须进行加密后进行比对
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//如果用户可以找到。说明用户的帐号密码正确，可以继续执行程序
			if(user!=null) {
				//如果用户存在，但是未激活，则让用户进行激活
				if(user.getState()==0) {

					InputStream in = this.getClass().getClassLoader().getResourceAsStream("registerServlet.properties");

					Properties prop = new Properties();

					prop.load(in);

					String emailMsg = "点击这里激活：<a href='" + prop.getProperty("active.url") + "?method=activeServlet&activeCode="
							+ user.getCode() + "'>" + prop.getProperty("active.url") + "?activeCode=" + user.getCode() + "</a>";
					try {
						MailUtils.sendMail(user.getEmail(), "激活", emailMsg);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					response.getWriter().write("用户未激活。请点击<a href='https://mail.qq.com/'>激活</a>");
					return;
				}
				
				//判断是否勾选了自动登录
				String autoLogin = request.getParameter("autoLogin");
				if(autoLogin!=null&&autoLogin.equals("autoLogin")) {
					//需要把用户的帐号密码保存到cookie中
					Cookie cookie_username = new Cookie("cookie_username",URLEncoder.encode(username, "UTF-8"));//cookie中不能出现中文，需要进行编码
					Cookie cookie_password = new Cookie("cookie_password",password);
					
					//设定cookie的有效期
					cookie_username.setMaxAge(60*30);//基础单位为秒  60*30  表示30分钟
					cookie_password.setMaxAge(60*30);
					
					//把cookie发送给客户端
					response.addCookie(cookie_username);
					response.addCookie(cookie_password);
					
				}
				
				HttpSession session = request.getSession();
				session.setAttribute("user", user);
				
				response.sendRedirect(request.getContextPath()+"/product?method=index");
			}
			else {
				response.sendRedirect(request.getContextPath()+"/login.jsp");
			}
			

		}else {
			response.sendRedirect(request.getContextPath()+"/login.jsp");
		}

	}

	// 检查用户名是否存在
	public void checkUsername(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");

		UserService service = new UserService();

		boolean isExist = false;
		try {
			isExist = service.checkUsername(username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.getWriter().write(isExist + "");
	}

	public void register(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 获取页面参数
		Map<String, String[]> parameterMap = request.getParameterMap();

		// 把参数封装到User实体类中
		User user = new User();

		try {
			BeanUtils.populate(user, parameterMap);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setPassword(MD5Utils.md5(user.getPassword()));

		user.setUid(CommonsUtils.getUUID());

		user.setTelephone(null);

		user.setState(0);

		user.setCode(CommonsUtils.getUUID());

		// 调用Seriver
		UserService service = new UserService();

		boolean isRegistSuccess = false;
		try {
			isRegistSuccess = service.regist(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 如果注册成功则发邮件激活
		if (isRegistSuccess) {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("registerServlet.properties");

			Properties prop = new Properties();

			prop.load(in);

			String emailMsg = "点击这里激活：<a href='" + prop.getProperty("active.url") + "?method=activeServlet&activeCode="
					+ user.getCode() + "'>" + prop.getProperty("active.url") + "?activeCode=" + user.getCode() + "</a>";
			try {
				MailUtils.sendMail(user.getEmail(), "激活", emailMsg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			response.getWriter().write("注册成功。请点击<a href='https://mail.qq.com/'>激活</a>");
		} else {
			// 不成功则回到注册页
			request.getRequestDispatcher("/register.jsp");
		}
	}

	// 激活用户
	public void activeServlet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String parameter = request.getParameter("activeCode");

		UserService service = new UserService();

		boolean isSuccess = false;
		try {
			isSuccess = service.active(parameter);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (isSuccess) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		} else {
			response.getWriter().write("激活失败。情联系管理员");
		}
	}
}
