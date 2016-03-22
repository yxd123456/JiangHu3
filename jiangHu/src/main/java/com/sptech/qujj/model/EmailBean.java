package com.sptech.qujj.model;

import java.io.Serializable;

public class EmailBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String addtime;
	private String account;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
