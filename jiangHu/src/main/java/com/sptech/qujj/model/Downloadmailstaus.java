package com.sptech.qujj.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Downloadmailstaus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int status;
	private int downloadnum;
	private String pop_host;
	private int pop_port;
	private int pop_type;

	private String account;// 登录账号
	private String password;// 登录密码
	private ArrayList<String> subject_filter;// 标题正则表达式
	private Map<String, String> uidl; // 已下载的所有邮件的 唯一标识
	private int pop_flag;// 链接方式

	private int is_check;// 账号是否检验

	public int getIs_check() {
		return is_check;
	}

	public void setIs_check(int is_check) {
		this.is_check = is_check;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ArrayList<String> getSubject_filter() {
		return subject_filter;
	}

	public void setSubject_filter(ArrayList<String> subject_filter) {
		this.subject_filter = subject_filter;
	}

	public Map<String, String> getUidl() {
		return uidl;
	}

	public void setUidl(Map<String, String> uidl) {
		this.uidl = uidl;
	}

	public int getPop_flag() {
		return pop_flag;
	}

	public void setPop_flag(int pop_flag) {
		this.pop_flag = pop_flag;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getDownloadnum() {
		return downloadnum;
	}

	public void setDownloadnum(int downloadnum) {
		this.downloadnum = downloadnum;
	}

	public String getPop_host() {
		return pop_host;
	}

	public void setPop_host(String pop_host) {
		this.pop_host = pop_host;
	}

	public int getPop_port() {
		return pop_port;
	}

	public void setPop_port(int pop_port) {
		this.pop_port = pop_port;
	}

	public int getPop_type() {
		return pop_type;
	}

	public void setPop_type(int pop_type) {
		this.pop_type = pop_type;
	}

}
