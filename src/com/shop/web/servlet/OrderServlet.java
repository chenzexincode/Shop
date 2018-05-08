package com.shop.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.shop.domain.Order;
import com.shop.domain.Orderitem;
import com.shop.domain.Product;
import com.shop.domain.User;
import com.shop.service.OrderService;
import com.shop.utils.CommonsUtils;
import com.shop.utils.PaymentUtil;
import com.shop.vo.Cart;
import com.shop.vo.CartItem;

public class OrderServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	//我的订单
	public void orderList(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		OrderService service = new OrderService();

		//封装一个orderList用于页面显示
		List<Order> orderList = null;
		
		//查找所有属于该用户的订单
		try {
			orderList = service.findOrderListByUid(user.getUid());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//封装orderList中的每一个order
		if(orderList!=null&&orderList.size()>0) {
			for(Order order : orderList) {
				//Order类中封装了User,数据库中没有，需要单独封装
				order.setUser(user);
				
				//封装Order中的List<Orderitem>
				try {
					//mapList中存放的是多个订单项的商品信息
					List<Map<String, Object>> mapList = service.findProductAndOrderListByOid(order.getOid());					
					
					for(Map<String,Object> map : mapList) {
						//封装每一个订单项，一个商品对应一个订单项
						try {
							//private Product product;//订单项商品
							Product product = new Product();
							BeanUtils.populate(product, map);

							//封装订单项
							Orderitem item = new Orderitem();
							BeanUtils.populate(item, map);
							
							//封装订单项的Product
							item.setProduct(product);
							
							//把订单项添加到order的List<Orderitem>中
							order.getList().add(item);
						
						} catch (IllegalAccessException | InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		
		request.setAttribute("orderList", orderList);
		
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
		
	}
	
    
	//确认订单----修改收货人信息====+付款
	public void confirmOrder(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		String oid = request.getParameter("oid");
		
		if(oid!=null&&!oid.equals("")) {
			String address = request.getParameter("address");
			String name = request.getParameter("name");
			String telephont = request.getParameter("telephont");
			
			OrderService service = new OrderService();
			if(address!=null&&name!=null&&telephont!=null) {
				try {
					service.updateOrderByOid(oid,address,name,telephont);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
			try {
				Order order= service.findOrderByOid(oid);

				// 获得 支付必须基本数据
				String orderid = oid;
				String money = 0.01+"";//正式使用        order.getTotal()+"";
				// 银行
				String pd_FrpId = request.getParameter("pd_FrpId");
	
				// 发给支付公司需要哪些数据
				String p0_Cmd = "Buy";
				String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
				String p2_Order = orderid;
				String p3_Amt = money;
				String p4_Cur = "CNY";
				String p5_Pid = "";
				String p6_Pcat = "";
				String p7_Pdesc = "";
				// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
				// 第三方支付可以访问网址
				String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
				String p9_SAF = "";
				String pa_MP = "";
				String pr_NeedResponse = "1";
				// 加密hmac 需要密钥
				String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
						"keyValue");
				String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
						p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
						pd_FrpId, pr_NeedResponse, keyValue);
				
				
				String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
								"&p0_Cmd="+p0_Cmd+
								"&p1_MerId="+p1_MerId+
								"&p2_Order="+p2_Order+
								"&p3_Amt="+p3_Amt+
								"&p4_Cur="+p4_Cur+
								"&p5_Pid="+p5_Pid+
								"&p6_Pcat="+p6_Pcat+
								"&p7_Pdesc="+p7_Pdesc+
								"&p8_Url="+p8_Url+
								"&p9_SAF="+p9_SAF+
								"&pa_MP="+pa_MP+
								"&pr_NeedResponse="+pr_NeedResponse+
								"&hmac="+hmac;
	
				//重定向到第三方支付平台
				response.sendRedirect(url);
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
		}else{
			response.getWriter().write("交易超时");
		}
				
	}
	
	
	//提交订单
	public void submitOrder(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Cart cart = (Cart) session.getAttribute("cart");
		
		//如果未登录。则登录
//		if(user==null) {
//			response.sendRedirect(request.getContextPath()+"/login.jsp");
//			return;
//		}
		
		//购物车session未超时，提交订单
		if(cart!=null) {
			//创建订单对象
			Order order = new Order();
			//封装订单对象
//			private String oid;//订单id
			order.setOid(CommonsUtils.getUUID());
			
//			private Date ordertime;//订单时间
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			order.setOrdertime(format.format(date));
			
//			private double total;//订单总计
			order.setTotal(cart.getTotal());
			
//			private int state;//订单状态，0：未付款，1：已付款
			order.setState(0);
			
//			private String address;//送货地址
			order.setAddress(null);
			
//			private String name;//收货人名字
			order.setName(null);
			
//			private String telephont;//收货人电话
			order.setTelephont(null);
		
//			private User user;//提交订单的用户
			order.setUser(user);
			
			//获取购物项集合
			Map<String, CartItem> cartItem = cart.getCartItem();
			
			//把购物车项中的数据封装到订单项中
			for(Entry<String, CartItem> entry : cartItem.entrySet()) {
				//获取购物项
				CartItem item = entry.getValue();
				//创建订单项对象
				Orderitem orderitem = new Orderitem();
				
//				private String itemid;//订单项id
				orderitem.setItemid(CommonsUtils.getUUID());
				
//				private int count;//订单项购买数量
				orderitem.setCount(item.getNum());
				
//				private double subtotal;//订单项小计
				orderitem.setSubtotal(item.getSubtotal());
				
//				private Product product;//订单项商品
				orderitem.setProduct(item.getProduct());
				
//				private Order order;//订单项隶属于哪个订单
				orderitem.setOrder(order);
				
//			private List<Orderitem> list = new ArrayList<Orderitem>();//订单包含的订单项
				order.getList().add(orderitem);
			}
			
			
			OrderService service = new OrderService();
			boolean submitOrder = service.submitOrder(order);
			if(submitOrder) {
				session.removeAttribute("cart");
				session.setAttribute("order", order);
				response.sendRedirect(request.getContextPath()+"/order_info.jsp");
			}else {
				response.getWriter().write("提交订单失败");
			}
			
		}else {
			response.getWriter().write("购物车超时");
		}		
		
	}
}
