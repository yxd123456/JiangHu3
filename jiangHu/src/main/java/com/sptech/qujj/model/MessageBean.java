package com.sptech.qujj.model;

import java.io.Serializable;

public class MessageBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String content;
	private int is_type;
	private int addtime;
	private String read_time;
	
	public String getRead_time() {
		return read_time;
	}
	public void setRead_time(String read_time) {
		this.read_time = read_time;
	}
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getIs_type() {
		return is_type;
	}
	public void setIs_type(int is_type) {
		this.is_type = is_type;
	}
	public int getAddtime() {
		return addtime;
	}
	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

	
	
}
