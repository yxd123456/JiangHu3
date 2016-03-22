package com.sptech.qujj.model;

import java.io.Serializable;

public class Product implements Serializable {
	/**
	 * 理财产品
	 */
	private static final long serialVersionUID = 1L;

	private int id;// 订单唯一标识
	private int limit;// 投资期限，后5位为实际数量，前1位数(1为天 2为月 3为年)
	private float interest;// 年化率
	private String subject;// 产品名称
	private float buy_money_min;// 起投
	private int number_has; // 已出售份数
	private int number;// 总数
	private int number_people;// 购买人数
	private String description;// 产品描述
	private int buy_time;// 上架时间
	private int limit_on;// 上架期限
	private int buy_endtime;// 下架时间 （下单时间 +最短赎回期限）
	private int is_status;// 0已接受 1处理中 2处理成功 3处理失败 4请求失败

	private int addtime;// 交易时间
	private float buy_money;// 起投
	private int target_id; // 产品唯一标识
	private String order_no;// 订单编号
	private int number_profit;// 已结算数量
	private float profit_actual;// 已结算收益
	// private int endtime;// 结束时间
	private float profit_no;// 未结算收益
	private float interest_early;// 提前赎回年利率
	private int limit_min; // 最短赎回期限
	private int buy_end_time;// 上架结束时间
	private int is_raply;// 赎回条件 0 可赎回 1 不可赎回
	private int is_profit;// 1 已结算，否则未结算
	private int is_payment;// 支付方式 0 余额支付，1银行卡支付
	private int end_time;// 项目结束时间
	private float total_profit;// 预期收益

	private int is_buy; // 1已购买(不可以点) 否则未购买

	public int getIs_buy() {
		return is_buy;
	}

	public void setIs_buy(int is_buy) {
		this.is_buy = is_buy;
	}

	public float getTotal_profit() {
		return total_profit;
	}

	public void setTotal_profit(float total_profit) {
		this.total_profit = total_profit;
	}

	public int getEnd_time() {
		return end_time;
	}

	public void setEnd_time(int end_time) {
		this.end_time = end_time;
	}

	public int getIs_payment() {
		return is_payment;
	}

	public void setIs_payment(int is_payment) {
		this.is_payment = is_payment;
	}

	// 已购买份数
	private int buy_number;// 已购买份数

	public int getBuy_endtime() {
		return buy_endtime;
	}

	public void setBuy_endtime(int buy_endtime) {
		this.buy_endtime = buy_endtime;
	}

	public int getBuy_number() {
		return buy_number;
	}

	public void setBuy_number(int buy_number) {
		this.buy_number = buy_number;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

	public float getBuy_money() {
		return buy_money;
	}

	public void setBuy_money(float buy_money) {
		this.buy_money = buy_money;
	}

	public int getTarget_id() {
		return target_id;
	}

	public void setTarget_id(int target_id) {
		this.target_id = target_id;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public int getNumber_profit() {
		return number_profit;
	}

	public void setNumber_profit(int number_profit) {
		this.number_profit = number_profit;
	}

	public float getProfit_actual() {
		return profit_actual;
	}

	public void setProfit_actual(float profit_actual) {
		this.profit_actual = profit_actual;
	}

	// public int getEndtime() {
	// return endtime;
	// }
	//
	// public void setEndtime(int endtime) {
	// this.endtime = endtime;
	// }

	public float getProfit_no() {
		return profit_no;
	}

	public void setProfit_no(float profit_no) {
		this.profit_no = profit_no;
	}

	public float getInterest_early() {
		return interest_early;
	}

	public void setInterest_early(float interest_early) {
		this.interest_early = interest_early;
	}

	public int getBuy_end_time() {
		return buy_end_time;
	}

	public void setBuy_end_time(int buy_end_time) {
		this.buy_end_time = buy_end_time;
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

	public int getLimit_min() {
		return limit_min;
	}

	public void setLimit_min(int limit_min) {
		this.limit_min = limit_min;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLimit_on() {
		return limit_on;
	}

	public void setLimit_on(int limit_on) {
		this.limit_on = limit_on;
	}

	public int getBuy_time() {
		return buy_time;
	}

	public void setBuy_time(int buy_time) {
		this.buy_time = buy_time;
	}

	public int getNumber_people() {
		return number_people;
	}

	public void setNumber_people(int number_people) {
		this.number_people = number_people;
	}

	public int getNumber_has() {
		return number_has;
	}

	public void setNumber_has(int number_has) {
		this.number_has = number_has;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setInterest(float interest) {
		this.interest = interest;
	}

	public float getInterest() {
		return this.interest;
	}

	public void setIs_status(int is_status) {
		this.is_status = is_status;
	}

	public int getIs_status() {
		return this.is_status;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setBuy_money_min(float buy_money_min) {
		this.buy_money_min = buy_money_min;
	}

	public float getBuy_money_min() {
		return this.buy_money_min;
	}

}