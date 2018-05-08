package com.shop.domain;

import java.util.ArrayList;
import java.util.List;

public class Order {
	private String oid;//订单id
	private String ordertime;//订单时间
	private double total;//订单总计
	private int state;//订单状态，0：未付款，1：已付款
	private String address;//送货地址
	private String name;//收货人名字
	private String telephont;//收货人电话
	
	private User user;//提交订单的用户
	
	private List<Orderitem> list = new ArrayList<Orderitem>();//订单包含的订单项

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephont() {
		return telephont;
	}

	public void setTelephont(String telephont) {
		this.telephont = telephont;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Orderitem> getList() {
		return list;
	}

	public void setList(List<Orderitem> list) {
		this.list = list;
	}
}
