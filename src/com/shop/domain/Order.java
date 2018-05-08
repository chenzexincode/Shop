package com.shop.domain;

import java.util.ArrayList;
import java.util.List;

public class Order {
	private String oid;//����id
	private String ordertime;//����ʱ��
	private double total;//�����ܼ�
	private int state;//����״̬��0��δ���1���Ѹ���
	private String address;//�ͻ���ַ
	private String name;//�ջ�������
	private String telephont;//�ջ��˵绰
	
	private User user;//�ύ�������û�
	
	private List<Orderitem> list = new ArrayList<Orderitem>();//���������Ķ�����

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
