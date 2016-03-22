package com.sptech.qujj.model;

import java.io.Serializable;

public class Banner implements Serializable {

	/**
	 * 轮播图
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private String title;
	private String desc;
	private String picurl;
	private String url;
	private Integer status;
	private String createtime;
	private Integer appversion;

	private int isdelete;
	private int priority;

	//
	// private void writeObject(ObjectOutputStream oos) throws IOException {
	// oos.defaultWriteObject();
	// System.out.println("session serialized");
	// }
	//
	// private void readObject(ObjectInputStream ois) throws IOException,
	// ClassNotFoundException {
	// ois.defaultReadObject();
	// System.out.println("session deserialized");
	// }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public Integer getAppversion() {
		return appversion;
	}

	public void setAppversion(Integer appversion) {
		this.appversion = appversion;
	}

	public int getIsdelete() {
		return isdelete;
	}

	public void setIsdelete(int isdelete) {
		this.isdelete = isdelete;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
