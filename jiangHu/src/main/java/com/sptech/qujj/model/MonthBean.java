package com.sptech.qujj.model;

import java.io.Serializable;

/**
 * 本月应还
 * 
 * @author LiLin
 * 
 */
public class MonthBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String borrow_id;
	private float help_money;
	private String addtime;
	private String takeeffect_time;
	private String title;
	private String help_limit;
	private float help_service_fee;
	private float help_interest;
	private String help_expect;

	public String getBorrow_id() {
		return borrow_id;
	}

	public void setBorrow_id(String borrow_id) {
		this.borrow_id = borrow_id;
	}


	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getTakeeffect_time() {
		return takeeffect_time;
	}

	public void setTakeeffect_time(String takeeffect_time) {
		this.takeeffect_time = takeeffect_time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHelp_limit() {
		return help_limit;
	}

	public void setHelp_limit(String help_limit) {
		this.help_limit = help_limit;
	}

	public float getHelp_money() {
		return help_money;
	}

	public void setHelp_money(float help_money) {
		this.help_money = help_money;
	}

	public float getHelp_service_fee() {
		return help_service_fee;
	}

	public void setHelp_service_fee(float help_service_fee) {
		this.help_service_fee = help_service_fee;
	}

	public float getHelp_interest() {
		return help_interest;
	}

	public void setHelp_interest(float help_interest) {
		this.help_interest = help_interest;
	}


	public String getHelp_expect() {
		return help_expect;
	}

	public void setHelp_expect(String help_expect) {
		this.help_expect = help_expect;
	}

}
