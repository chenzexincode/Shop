package com.shop.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shop.domain.Product;
import com.shop.service.ProductService;
import com.shop.vo.Cart;
import com.shop.vo.CartItem;


public class CartServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	
	//��չ��ﳵ
	public void clearCart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		session.removeAttribute("cart");
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	
	//���ﳵɾ��������Ʒ
	public void delProductFromCart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String pid = request.getParameter("pid");
		
		//��ȡsession�е�cart
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		
		//�޸�cart���ܼ�
		cart.setTotal(cart.getTotal()-cart.getCartItem().get(pid).getSubtotal());
		
		//����pid�Ƴ�cart��cartItem����Ʒ
		cart.getCartItem().remove(pid);
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
 
	//���빺�ﳵ����
	public void addProductToCart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		//��ȡҳ�洫��������Ʒpid�͹�������
		String pid = request.getParameter("pid");
		String numStr = request.getParameter("num");
		int num = 0;
		//����������Ϊ����ʱ���ſ��Կ�������ִ��
		if(numStr!=null&&!numStr.equals("")) {
			num=Integer.parseInt(numStr);
			
			//����pid�ҵ���Ӧ����Ʒ
			ProductService proService = new ProductService();
			Product product = null;
			try {
				product = proService.findProductByPid(pid);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//���㱾����Ʒ��С��
			double market_price = product.getMarket_price();
			double subtotal = market_price*num;
			
			//��װ����
			CartItem cartItem = new CartItem();
			cartItem.setNum(num);
			cartItem.setProduct(product);
			cartItem.setSubtotal(subtotal);
			
			//��session�л�ȡ���ﳵ��û���򴴽�
			HttpSession session = request.getSession();
			Cart cart = (Cart) session.getAttribute("cart");
			if(cart==null) {
				cart = new Cart();
			}
			
			//�ӹ��ﳵ�и��ݱ�����Ʒ��pid��ȡ���ﳵ����򷵻�null
			CartItem item = cart.getCartItem().remove(pid);
			//����Ѿ����ڣ���Ҫ�����ۼ�
			if(item!=null) {
				cartItem.setNum(cartItem.getNum()+item.getNum());
				cartItem.setSubtotal(cartItem.getSubtotal()+item.getSubtotal());
			}
			//������ɺ󣬷ŵ�������
			cart.getCartItem().put(pid, cartItem);
			
			//���㹺�ﳵ���ܽ��
			cart.setTotal(cart.getTotal()+subtotal);
			
			session.setAttribute("cart", cart);
			response.sendRedirect(request.getContextPath()+"/cart.jsp");
			
		}
		
	}


}
