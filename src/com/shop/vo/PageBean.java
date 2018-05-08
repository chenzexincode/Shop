package com.shop.vo;

import java.util.ArrayList;
import java.util.List;

public class PageBean<T> {
	//����ҳ��
	private int currentPage;
	//��ǰҳ����
	private int currentCount;
	//������
	private int totalCount;
	//��ҳ��
	private int totalPage;
	//ÿҳ����
	private List<T> list = new ArrayList<T>();
	
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getCurrentCount() {
		return currentCount;
	}
	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
}
