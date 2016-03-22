package com.sptech.qujj.model;

import java.io.Serializable;

/**
 * 支持 的银行卡
 * 
 * @author yebr
 * 
 */

public class UsablebankBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String key;
	private String img;
	private String stream;
	private String first;
	private String customer;// 客服
	private String emergency;// 紧急挂失
	private String artificial;// 人工服务

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getEmergency() {
		return emergency;
	}

	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}

	public String getArtificial() {
		return artificial;
	}

	public void setArtificial(String artificial) {
		this.artificial = artificial;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
