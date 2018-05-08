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
	
	
	//清空购物车
	public void clearCart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		session.removeAttribute("cart");
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	
	//购物车删除单个商品
	public void delProductFromCart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String pid = request.getParameter("pid");
		
		//获取session中的cart
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		
		//修改cart的总计
		cart.setTotal(cart.getTotal()-cart.getCartItem().get(pid).getSubtotal());
		
		//根据pid移除cart中cartItem的商品
		cart.getCartItem().remove(pid);
		
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
 
	//加入购物车功能
	public void addProductToCart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		//获取页面传过来的商品pid和购买数量
		String pid = request.getParameter("pid");
		String numStr = request.getParameter("num");
		int num = 0;
		//当购买数量为正数时，才可以可以往下执行
		if(numStr!=null&&!numStr.equals("")) {
			num=Integer.parseInt(numStr);
			
			//根据pid找到对应的商品
			ProductService proService = new ProductService();
			Product product = null;
			try {
				product = proService.findProductByPid(pid);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//计算本次商品的小计
			double market_price = product.getMarket_price();
			double subtotal = market_price*num;
			
			//封装对象
			CartItem cartItem = new CartItem();
			cartItem.setNum(num);
			cartItem.setProduct(product);
			cartItem.setSubtotal(subtotal);
			
			//从session中获取购物车，没有则创建
			HttpSession session = request.getSession();
			Cart cart = (Cart) session.getAttribute("cart");
			if(cart==null) {
				cart = new Cart();
			}
			
			//从购物车中根据本次商品的pid获取购物车项，无则返回null
			CartItem item = cart.getCartItem().remove(pid);
			//如果已经存在，需要进行累加
			if(item!=null) {
				cartItem.setNum(cartItem.getNum()+item.getNum());
				cartItem.setSubtotal(cartItem.getSubtotal()+item.getSubtotal());
			}
			//处理完成后，放到购物项
			cart.getCartItem().put(pid, cartItem);
			
			//计算购物车的总金额
			cart.setTotal(cart.getTotal()+subtotal);
			
			session.setAttribute("cart", cart);
			response.sendRedirect(request.getContextPath()+"/cart.jsp");
			
		}
		
	}


}
