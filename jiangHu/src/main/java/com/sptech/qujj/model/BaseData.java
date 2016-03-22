package com.sptech.qujj.model;

import java.io.Serializable;

/**
 * 父类数据结构 map里面无限添加对象
 */
public class BaseData<T> implements Serializable {

	private static final long serialVersionUID = 1497048108915513658L;

	/* 标志位 : success 成功, 失败 */
	public String code;
	public String desc;
	public String time;
	public String data;
	/* 总数据结构 */
//	public MapData<T> data;

}