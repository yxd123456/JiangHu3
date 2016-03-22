package com.sptech.qujj.model;

import java.io.Serializable;

public class TransactionDetail implements Serializable {

	/**
	 * 交易详情
	 */
	private static final long serialVersionUID = 1L;

	private String card_bank; // 支付行

	private int addtime; // 交易时间

	private String query_time; // 付款时间

	private int is_status; // 状态 0已接受 1处理中 2处理成功 3处理失败 4请求失败

	private float money;// 金额

	private String card_no; // 支付卡号

	private String order_id;// 交易账号

	public String getCard_bank() {
		return card_bank;
	}

	public void setCard_bank(String card_bank) {
		this.card_bank = card_bank;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

	public String getQuery_time() {
		return query_time;
	}

	public void setQuery_time(String query_time) {
		this.query_time = query_time;
	}

	public int getIs_status() {
		return is_status;
	}

	public void setIs_status(int is_status) {
		this.is_status = is_status;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

}
