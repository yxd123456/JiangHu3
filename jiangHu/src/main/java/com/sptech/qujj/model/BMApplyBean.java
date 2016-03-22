package com.sptech.qujj.model;

import java.io.Serializable;
/**
 *  借你钱-响应参数
 * @author Administrator
 *
 */
public class BMApplyBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private int borrow_id;//借你钱申请唯一标识
	
	public int getBorrow_id() {
		return borrow_id;
	}
	
	public void setBorrow_id(int borrow_id) {
		this.borrow_id = borrow_id;
	}
	

}
