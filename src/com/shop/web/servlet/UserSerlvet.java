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

	//�û�ע��ģ��
	public void logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		//�Ƴ����λỰ�е�user
		session.removeAttribute("user");
		
		//��������û������cookie
		//����ͬ��cookie
		Cookie cookie_username = new Cookie("cookie_username","");
		Cookie cookie_password = new Cookie("cookie_password","");
		//����cookieʧЧ
		cookie_username.setMaxAge(0);
		cookie_password.setMaxAge(0);
		//��cookie���͸��ͻ���
		response.addCookie(cookie_username);
		response.addCookie(cookie_password);
		
		response.sendRedirect(request.getContextPath()+"/login.jsp");
	}
	
	
	//�û���¼ģ��
	public void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		//��ȫ�ж�
		if (username != null && !username.equals("") && password != null && !password.equals("")) {
			UserService service = new UserService();
			User user = null;
			try {
				user = service.login(username, MD5Utils.md5(password));//��Ϊ���ݿ��е������ֶξ���MD5���ܹ������Ա�����м��ܺ���бȶ�
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//����û������ҵ���˵���û����ʺ�������ȷ�����Լ���ִ�г���
			if(user!=null) {
				//����û����ڣ�����δ��������û����м���
				if(user.getState()==0) {

					InputStream in = this.getClass().getClassLoader().getResourceAsStream("registerServlet.properties");

					Properties prop = new Properties();

					prop.load(in);

					String emailMsg = "������Ｄ�<a href='" + prop.getProperty("active.url") + "?method=activeServlet&activeCode="
							+ user.getCode() + "'>" + prop.getProperty("active.url") + "?activeCode=" + user.getCode() + "</a>";
					try {
						MailUtils.sendMail(user.getEmail(), "����", emailMsg);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					response.getWriter().write("�û�δ�������<a href='https://mail.qq.com/'>����</a>");
					return;
				}
				
				//�ж��Ƿ�ѡ���Զ���¼
				String autoLogin = request.getParameter("autoLogin");
				if(autoLogin!=null&&autoLogin.equals("autoLogin")) {
					//��Ҫ���û����ʺ����뱣�浽cookie��
					Cookie cookie_username = new Cookie("cookie_username",URLEncoder.encode(username, "UTF-8"));//cookie�в��ܳ������ģ���Ҫ���б���
					Cookie cookie_password = new Cookie("cookie_password",password);
					
					//�趨cookie����Ч��
					cookie_username.setMaxAge(60*30);//������λΪ��  60*30  ��ʾ30����
					cookie_password.setMaxAge(60*30);
					
					//��cookie���͸��ͻ���
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

	// ����û����Ƿ����
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

		// ��ȡҳ�����
		Map<String, String[]> parameterMap = request.getParameterMap();

		// �Ѳ�����װ��Userʵ������
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

		// ����Seriver
		UserService service = new UserService();

		boolean isRegistSuccess = false;
		try {
			isRegistSuccess = service.regist(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ���ע��ɹ����ʼ�����
		if (isRegistSuccess) {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("registerServlet.properties");

			Properties prop = new Properties();

			prop.load(in);

			String emailMsg = "������Ｄ�<a href='" + prop.getProperty("active.url") + "?method=activeServlet&activeCode="
					+ user.getCode() + "'>" + prop.getProperty("active.url") + "?activeCode=" + user.getCode() + "</a>";
			try {
				MailUtils.sendMail(user.getEmail(), "����", emailMsg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			response.getWriter().write("ע��ɹ�������<a href='https://mail.qq.com/'>����</a>");
		} else {
			// ���ɹ���ص�ע��ҳ
			request.getRequestDispatcher("/register.jsp");
		}
	}

	// �����û�
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
			response.getWriter().write("����ʧ�ܡ�����ϵ����Ա");
		}
	}
}
