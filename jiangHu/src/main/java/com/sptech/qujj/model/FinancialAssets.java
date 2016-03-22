package com.sptech.qujj.model;

import java.io.Serializable;

public class FinancialAssets implements Serializable {
	/**
	 * 理财资产
	 */
	private static final long serialVersionUID = 1L;

	private int limit;// 期限
	private int id;// 订单唯一标识
	private int addtime;// 时间
	private float interest;// 年化率
	private String subject; // 产品名称
	private float buy_money;// 起投
	private int number_has; // 已购买份数
	private int target_id; // 产品唯一标识
	private String order_no;// 订单编号
	private int number_profit;// 已结算数量
	private float profit_actual;// 已结算收益
	private int endtime;// 结束时间
	private float profit_no;// 未结算收益
	private float interest_early;// 提前赎回年利率
	private int limit_min; // 最短赎回期限
	private int buy_end_time;// 上架结束时间
	private int is_raply;// 是否已结算 1 已结算，否则未结算
	private int is_profit;// 赎回条件 0 随时 1 到期
	private int is_status;// 0已接受 1处理中 2处理成功 3处理失败 4请求失败

	public int getIs_status() {
		return is_status;
	}

	public void setIs_status(int is_status) {
		this.is_status = is_status;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public int getIs_raply() {
		return is_raply;
	}

	public void setIs_raply(int is_raply) {
		this.is_raply = is_raply;
	}

	public int getIs_profit() {
		return is_profit;
	}

	public void setIs_profit(int is_profit) {
		this.is_profit = is_profit;
	}

	public int getBuy_end_time() {
		return buy_end_time;
	}

	public void setBuy_end_time(int buy_end_time) {
		this.buy_end_time = buy_end_time;
	}

	public int getLimit_min() {
		return limit_min;
	}

	public void setLimit_min(int limit_min) {
		this.limit_min = limit_min;
	}

	public float getInterest_early() {
		return interest_early;
	}

	public void setInterest_early(float interest_early) {
		this.interest_early = interest_early;
	}

	public float getProfit_no() {
		return profit_no;
	}

	public void setProfit_no(float profit_no) {
		this.profit_no = profit_no;
	}

	public int getEndtime() {
		return endtime;
	}

	public void setEndtime(int endtime) {
		this.endtime = endtime;
	}

	public float getProfit_actual() {
		return profit_actual;
	}

	public void setProfit_actual(float profit_actual) {
		this.profit_actual = profit_actual;
	}

	public int getNumber_profit() {
		return number_profit;
	}

	public void setNumber_profit(int number_profit) {
		this.number_profit = number_profit;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

	public float getInterest() {
		return interest;
	}

	public void setInterest(float interest) {
		this.interest = interest;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public float getBuy_money() {
		return buy_money;
	}

	public void setBuy_money(float buy_money) {
		this.buy_money = buy_money;
	}

	public int getNumber_has() {
		return number_has;
	}

	public void setNumber_has(int number_has) {
		this.number_has = number_has;
	}

	public int getTarget_id() {
		return target_id;
	}

	public void setTarget_id(int target_id) {
		this.target_id = target_id;
	}

}
