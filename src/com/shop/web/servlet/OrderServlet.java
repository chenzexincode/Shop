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
	
	//�ҵĶ���
	public void orderList(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		OrderService service = new OrderService();

		//��װһ��orderList����ҳ����ʾ
		List<Order> orderList = null;
		
		//�����������ڸ��û��Ķ���
		try {
			orderList = service.findOrderListByUid(user.getUid());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//��װorderList�е�ÿһ��order
		if(orderList!=null&&orderList.size()>0) {
			for(Order order : orderList) {
				//Order���з�װ��User,���ݿ���û�У���Ҫ������װ
				order.setUser(user);
				
				//��װOrder�е�List<Orderitem>
				try {
					//mapList�д�ŵ��Ƕ�����������Ʒ��Ϣ
					List<Map<String, Object>> mapList = service.findProductAndOrderListByOid(order.getOid());					
					
					for(Map<String,Object> map : mapList) {
						//��װÿһ�������һ����Ʒ��Ӧһ��������
						try {
							//private Product product;//��������Ʒ
							Product product = new Product();
							BeanUtils.populate(product, map);

							//��װ������
							Orderitem item = new Orderitem();
							BeanUtils.populate(item, map);
							
							//��װ�������Product
							item.setProduct(product);
							
							//�Ѷ�������ӵ�order��List<Orderitem>��
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
	
    
	//ȷ�϶���----�޸��ջ�����Ϣ====+����
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

				// ��� ֧�������������
				String orderid = oid;
				String money = 0.01+"";//��ʽʹ��        order.getTotal()+"";
				// ����
				String pd_FrpId = request.getParameter("pd_FrpId");
	
				// ����֧����˾��Ҫ��Щ����
				String p0_Cmd = "Buy";
				String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
				String p2_Order = orderid;
				String p3_Amt = money;
				String p4_Cur = "CNY";
				String p5_Pid = "";
				String p6_Pcat = "";
				String p7_Pdesc = "";
				// ֧���ɹ��ص���ַ ---- ������֧����˾����ʡ��û�����
				// ������֧�����Է�����ַ
				String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
				String p9_SAF = "";
				String pa_MP = "";
				String pr_NeedResponse = "1";
				// ����hmac ��Ҫ��Կ
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
	
				//�ض��򵽵�����֧��ƽ̨
				response.sendRedirect(url);
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
		}else{
			response.getWriter().write("���׳�ʱ");
		}
				
	}
	
	
	//�ύ����
	public void submitOrder(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Cart cart = (Cart) session.getAttribute("cart");
		
		//���δ��¼�����¼
//		if(user==null) {
//			response.sendRedirect(request.getContextPath()+"/login.jsp");
//			return;
//		}
		
		//���ﳵsessionδ��ʱ���ύ����
		if(cart!=null) {
			//������������
			Order order = new Order();
			//��װ��������
//			private String oid;//����id
			order.setOid(CommonsUtils.getUUID());
			
//			private Date ordertime;//����ʱ��
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			order.setOrdertime(format.format(date));
			
//			private double total;//�����ܼ�
			order.setTotal(cart.getTotal());
			
//			private int state;//����״̬��0��δ���1���Ѹ���
			order.setState(0);
			
//			private String address;//�ͻ���ַ
			order.setAddress(null);
			
//			private String name;//�ջ�������
			order.setName(null);
			
//			private String telephont;//�ջ��˵绰
			order.setTelephont(null);
		
//			private User user;//�ύ�������û�
			order.setUser(user);
			
			//��ȡ�������
			Map<String, CartItem> cartItem = cart.getCartItem();
			
			//�ѹ��ﳵ���е����ݷ�װ����������
			for(Entry<String, CartItem> entry : cartItem.entrySet()) {
				//��ȡ������
				CartItem item = entry.getValue();
				//�������������
				Orderitem orderitem = new Orderitem();
				
//				private String itemid;//������id
				orderitem.setItemid(CommonsUtils.getUUID());
				
//				private int count;//�����������
				orderitem.setCount(item.getNum());
				
//				private double subtotal;//������С��
				orderitem.setSubtotal(item.getSubtotal());
				
//				private Product product;//��������Ʒ
				orderitem.setProduct(item.getProduct());
				
//				private Order order;//�������������ĸ�����
				orderitem.setOrder(order);
				
//			private List<Orderitem> list = new ArrayList<Orderitem>();//���������Ķ�����
				order.getList().add(orderitem);
			}
			
			
			OrderService service = new OrderService();
			boolean submitOrder = service.submitOrder(order);
			if(submitOrder) {
				session.removeAttribute("cart");
				session.setAttribute("order", order);
				response.sendRedirect(request.getContextPath()+"/order_info.jsp");
			}else {
				response.getWriter().write("�ύ����ʧ��");
			}
			
		}else {
			response.getWriter().write("���ﳵ��ʱ");
		}		
		
	}
}
