package com.shop.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.shop.domain.Order;
import com.shop.service.OrderService;

public class AdminOrderServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
      
	
	//查看订单详情模块
	public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String oid = request.getParameter("oid");
		
		OrderService service = new OrderService();
		List<Map<String, Object>> mapList = null;
		try {
			mapList = service.findProductAndOrderListByOid(oid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		String json = gson.toJson(mapList);
		
		response.getWriter().write(json);
	}	
	
	//查询所有订单
	public void orderList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OrderService service = new OrderService();
		List<Order> orderList = null;
		try {
			orderList = service.findAllOrder();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
	}

}
