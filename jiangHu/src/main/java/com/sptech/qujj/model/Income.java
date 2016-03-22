package com.sptech.qujj.model;

import java.io.Serializable;

//理财收益
public class Income implements Serializable {

	/**
	 * 理财收益
	 */
	private static final long serialVersionUID = 1L;

	private String subject;

	private float profit_actual; // 收益

	private int addtime;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public float getProfit_actual() {
		return profit_actual;
	}

	public void setProfit_actual(float profit_actual) {
		this.profit_actual = profit_actual;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

}
