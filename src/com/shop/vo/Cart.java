package com.shop.vo;

import java.util.HashMap;
import java.util.Map;

public class Cart {
	private Map<String,CartItem> cartItem = new HashMap<String,CartItem>();
	private double total;
	public Map<String, CartItem> getCartItem() {
		return cartItem;
	}
	public void setCartItem(Map<String, CartItem> cartItem) {
		this.cartItem = cartItem;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	

}
