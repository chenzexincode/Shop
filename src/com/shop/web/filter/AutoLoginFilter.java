package com.shop.web.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shop.domain.User;
import com.shop.service.UserService;
import com.shop.utils.MD5Utils;

/**
 * Servlet Filter implementation class AutoLoginFilter
 */
public class AutoLoginFilter implements Filter {

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		//��ȡ�ͻ��˷�������cookie
		Cookie[] cookies = req.getCookies();
		
		if(cookies!=null&&cookies.length>0) {
			String username = null;
			String password = null;
			
			//���cookie���Ƿ����û�������
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("cookie_username")) {
					//����cookie_username��value��������󷢹����ġ�������Ҫ���н���
					username = URLDecoder.decode(cookie.getValue(), "UTF-8"); 
				}
				if(cookie.getName().equals("cookie_password")) {
					password = cookie.getValue();
				}
			}
			
			if(username!=null&&password!=null) {
				UserService service = new UserService();
				User user = null;
				try {
					user = service.login(username, MD5Utils.md5(password));//������Ҫ���м��ܺ����ݿ����ƥ��
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(user!=null) {
					HttpSession session = req.getSession();
					session.setAttribute("user", user);
				}
			}	
		}
		
		chain.doFilter(req, resp);
	}
	
    /**
     * Default constructor. 
     */
    public AutoLoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}


	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
