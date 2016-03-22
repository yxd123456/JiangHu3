package com.sptech.qujj.model;

import java.io.Serializable;

public class BankcardBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private int bankcard_id;
	private String card_no;
	private String card_bank;
	private String card_phone;
	private String card_realname;
	private int card_type;
	private int is_default;
	private int is_cashout;
	private int is_status;

	private float money_day_total;// 单日累计充值金额
	private float money_day_quota;// 单日限额
	private float money_day_once;// 单笔限额
	private int number_month_total;// 单月累计笔数
	private int number_month_quota;// 月交易笔数限制
	private float out_money_actual;// 可提现金额

	public float getOut_money_actual() {
		return out_money_actual;
	}

	public void setOut_money_actual(float out_money_actual) {
		this.out_money_actual = out_money_actual;
	}

	public float getMoney_day_total() {
		return money_day_total;
	}

	public void setMoney_day_total(float money_day_total) {
		this.money_day_total = money_day_total;
	}

	public float getMoney_day_quota() {
		return money_day_quota;
	}

	public void setMoney_day_quota(float money_day_quota) {
		this.money_day_quota = money_day_quota;
	}

	public float getMoney_day_once() {
		return money_day_once;
	}

	public void setMoney_day_once(float money_day_once) {
		this.money_day_once = money_day_once;
	}

	public int getNumber_month_total() {
		return number_month_total;
	}

	public void setNumber_month_total(int number_month_total) {
		this.number_month_total = number_month_total;
	}

	public int getNumber_month_quota() {
		return number_month_quota;
	}

	public void setNumber_month_quota(int number_month_quota) {
		this.number_month_quota = number_month_quota;
	}

	public String getCard_realname() {
		return card_realname;
	}

	public void setCard_realname(String card_realname) {
		this.card_realname = card_realname;
	}

	public int getBankcard_id() {
		return bankcard_id;
	}

	public void setBankcard_id(int bankcard_id) {
		this.bankcard_id = bankcard_id;
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

	public String getCard_phone() {
		return card_phone;
	}

	public void setCard_phone(String card_phone) {
		this.card_phone = card_phone;
	}

	public int getCard_type() {
		return card_type;
	}

	public void setCard_type(int card_type) {
		this.card_type = card_type;
	}

	public int getIs_default() {
		return is_default;
	}

	public void setIs_default(int is_default) {
		this.is_default = is_default;
	}

	public int getIs_cashout() {
		return is_cashout;
	}

	public void setIs_cashout(int is_cashout) {
		this.is_cashout = is_cashout;
	}

	public int getIs_status() {
		return is_status;
	}

	public void setIs_status(int is_status) {
		this.is_status = is_status;
	}

}
