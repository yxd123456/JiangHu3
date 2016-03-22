package com.sptech.qujj.model;

import java.io.Serializable;

/**
 * 借款明细实体类
 * 
 * @author LiLin
 * 
 */
public class BMListBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String title;
	private int borrow_id;
	private float help_money;
	private int takeeffect_time;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBorrow_id() {
		return borrow_id;
	}

	public void setBorrow_id(int borrow_id) {
		this.borrow_id = borrow_id;
	}

	public float getHelp_money() {
		return help_money;
	}

	public void setHelp_money(float help_money) {
		this.help_money = help_money;
	}

	public int getTakeeffect_time() {
		return takeeffect_time;
	}

	public void setTakeeffect_time(int takeeffect_time) {
		this.takeeffect_time = takeeffect_time;
	}

}
