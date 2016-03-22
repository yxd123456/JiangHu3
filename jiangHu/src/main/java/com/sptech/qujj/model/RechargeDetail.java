package com.sptech.qujj.model;

import java.io.Serializable;

/**
 * 充值详情
 * 
 * @author yebr
 * 
 */
public class RechargeDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float money;// 充值金额

	private float balance;// 账户余额

	private String description;// 描述

	private int addtime; // 充值时间

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

}
