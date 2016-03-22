package com.sptech.qujj.model;

import java.io.Serializable;

public class DownloadMail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String uidl;// 邮件id

	private int curMailNum;// 当前包

	private int mailcountNum;// 总包数

	private String curMailContent;// 内容

	public int getMailcountNum() {
		return mailcountNum;
	}

	public void setMailcountNum(int mailcountNum) {
		this.mailcountNum = mailcountNum;
	}

	public String getCurMailContent() {
		return curMailContent;
	}

	public void setCurMailContent(String curMailContent) {
		this.curMailContent = curMailContent;
	}

	public String getUidl() {
		return uidl;
	}

	public void setUidl(String uidl) {
		this.uidl = uidl;
	}

	public int getCurMailNum() {
		return curMailNum;
	}

	public void setCurMailNum(int curMailNum) {
		this.curMailNum = curMailNum;
	}

}
