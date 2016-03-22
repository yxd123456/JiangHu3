package com.sptech.qujj.model;

import java.io.Serializable;

public class BuyPronumber implements Serializable {

	/**
	 * 购买人数
	 */
	private static final long serialVersionUID = 1L;

	private int number_has; // 购买份数

	private float buy_money; // 每份金额

	private int interest; // 年利率

	private int limit; // 期限

	private int addtime; // 购买时间

	private String user_phone; // 手机号码

	public int getNumber_has() {
		return number_has;
	}

	public void setNumber_has(int number_has) {
		this.number_has = number_has;
	}

	public float getBuy_money() {
		return buy_money;
	}

	public void setBuy_money(float buy_money) {
		this.buy_money = buy_money;
	}

	public int getInterest() {
		return interest;
	}

	public void setInterest(int interest) {
		this.interest = interest;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

}
