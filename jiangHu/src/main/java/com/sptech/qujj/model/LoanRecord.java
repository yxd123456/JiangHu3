package com.sptech.qujj.model;

import java.io.Serializable;

public class LoanRecord implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * { "id":7, "user_realname":"李焕", "bill_month":1443628800,
	 * "help_money":4000, "help_limit":28, "help_service_fee":82.51,
	 * "help_interest":0, "takeeffect_time":1446393600,
	 * "card_no":"6225561340683306", "card_bank":"广发银行", "is_status":5,
	 * "bill_cycle_set":1443024000, "bill_cycle_end":1445529600,
	 * "loan_money":4082.51 }
	 */

	private int id;
	private String user_realname; // 真实姓名
	private float help_money; // 借款金额
	private float help_service_fee;// 服务费
	private float help_interest;// 借款利率

	private int help_limit;// 借款周期
	private int takeeffect_time;// 借款生效时间

	private String card_no;// 信用卡卡号
	private String card_bank;// 信用开卡行
	private float money;// 应还金额
	private int bill_month; // 申请日期

	private int bill_cycle_set;// 账单期开始
	private int bill_cycle_end;// 账单期

	private int is_status;// 状态

	private float loan_money;// 欠款金额

	private int repayment_time;// 还款时间

	private int addtime;// 申请时间

	private float repayment_money;// 应还金额

	public float getRepayment_money() {
		return repayment_money;
	}

	public void setRepayment_money(float repayment_money) {
		this.repayment_money = repayment_money;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

	public int getRepayment_time() {
		return repayment_time;
	}

	public void setRepayment_time(int repayment_time) {
		this.repayment_time = repayment_time;
	}

	public int getIs_status() {
		return is_status;
	}

	public void setIs_status(int is_status) {
		this.is_status = is_status;
	}

	public String getUser_realname() {
		return user_realname;
	}

	public void setUser_realname(String user_realname) {
		this.user_realname = user_realname;
	}

	public float getLoan_money() {
		return loan_money;
	}

	public void setLoan_money(float loan_money) {
		this.loan_money = loan_money;
	}

	// private int getIs_status() {
	// return is_status;
	// }
	//
	// public void setIs_status(int is_status) {
	// this.is_status = is_status;
	// }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getHelp_limit() {
		return help_limit;
	}

	public void setHelp_limit(int help_limit) {
		this.help_limit = help_limit;
	}

	public int getTakeeffect_time() {
		return takeeffect_time;
	}

	public void setTakeeffect_time(int takeeffect_time) {
		this.takeeffect_time = takeeffect_time;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getCard_bank() {
		return card_bank;
	}

	public void setCard_bank(String card_bank) {
		this.card_bank = card_bank;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public int getBill_month() {
		return bill_month;
	}

	public void setBill_month(int bill_month) {
		this.bill_month = bill_month;
	}

	public int getBill_cycle_set() {
		return bill_cycle_set;
	}

	public void setBill_cycle_set(int bill_cycle_set) {
		this.bill_cycle_set = bill_cycle_set;
	}

	public int getBill_cycle_end() {
		return bill_cycle_end;
	}

	public void setBill_cycle_end(int bill_cycle_end) {
		this.bill_cycle_end = bill_cycle_end;
	}

}
