package com.sptech.qujj.model;

import java.io.Serializable;

public class BalanceDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private float bills_id;
	private String description; // 描述
	private float balance;// 当前余额
	private float money;// 动态金额

	// private float user_money;
	private int is_type; // 类型 1充值 2提现 3投资 4收益(本金+利息) 5借款 6还款(本金+利息+滞纳金)
	private int is_status; // 0等待中 1处理中 2处理成功 3处理失败
	private int addtime;
	private int in_out;// 1收入 2支出

	// private int target_id;

	// public int getTarget_id() {
	// return target_id;
	// }
	//
	// public void setTarget_id(int target_id) {
	// this.target_id = target_id;
	// }

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public int getIn_out() {
		return in_out;
	}

	public void setIn_out(int in_out) {
		this.in_out = in_out;
	}

	// public float getUser_money() {
	// return user_money;
	// }
	//
	// public void setUser_money(float user_money) {
	// this.user_money = user_money;
	// }

	public float getBills_id() {
		return bills_id;
	}

	public void setBills_id(float bills_id) {
		this.bills_id = bills_id;
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

	public int getIs_type() {
		return is_type;
	}

	public void setIs_type(int is_type) {
		this.is_type = is_type;
	}

	public int getIs_status() {
		return is_status;
	}

	public void setIs_status(int is_status) {
		this.is_status = is_status;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

}
