package com.sptech.qujj.model;

import java.io.Serializable;

//红包
public class RedBag implements Serializable {

	/**
	 * 红包
	 */
	private static final long serialVersionUID = 1L;

	private int id;

	private float rb_money;

	private int first_use;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getRb_money() {
		return rb_money;
	}

	public void setRb_money(float rb_money) {
		this.rb_money = rb_money;
	}

	public int getFirst_use() {
		return first_use;
	}

	public void setFirst_use(int first_use) {
		this.first_use = first_use;
	}

}
