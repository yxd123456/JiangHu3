package com.sptech.qujj.model;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 用户
	 */

	private static final long serialVersionUID = 1L;

	private float investment_wait;// 投资金额

	private float profit_wait; // 总收益

	private float liabilities_wait;// 代还款金额

	private float interest_wait;// 待还利息

	private float user_money;// 用户可用金额

	private float frozen_money;// 用户不可用余额(冻结余额)

	private int count_product;// 产品个数

	private int reg_count;// 红包个数

	private float profit_total;// 累计收益

	private float yseterday_profit;// 昨日收益

	// count_product

	// 累计收益 = 用户可用余额 +不可用余额+投资金额

	public float getProfit_total() {
		return profit_total;
	}

	public float getYseterday_profit() {
		return yseterday_profit;
	}

	public void setYseterday_profit(float yseterday_profit) {
		this.yseterday_profit = yseterday_profit;
	}

	public void setProfit_total(float profit_total) {
		this.profit_total = profit_total;
	}

	public float getInvestment_wait() {
		return investment_wait;
	}

	public int getCount_product() {
		return count_product;
	}

	public void setCount_product(int count_product) {
		this.count_product = count_product;
	}

	public void setInvestment_wait(float investment_wait) {
		this.investment_wait = investment_wait;
	}

	public float getProfit_wait() {
		return profit_wait;
	}

	public void setProfit_wait(float profit_wait) {
		this.profit_wait = profit_wait;
	}

	public float getLiabilities_wait() {
		return liabilities_wait;
	}

	public void setLiabilities_wait(float liabilities_wait) {
		this.liabilities_wait = liabilities_wait;
	}

	public float getInterest_wait() {
		return interest_wait;
	}

	public void setInterest_wait(float interest_wait) {
		this.interest_wait = interest_wait;
	}

	public float getUser_money() {
		return user_money;
	}

	public void setUser_money(float user_money) {
		this.user_money = user_money;
	}

	public float getFrozen_money() {
		return frozen_money;
	}

	public void setFrozen_money(float frozen_money) {
		this.frozen_money = frozen_money;
	}

}
