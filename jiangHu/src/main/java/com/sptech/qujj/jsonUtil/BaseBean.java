package com.sptech.qujj.jsonUtil;


 /**
 *  Class Name: BaseBean.java
 *  Function:
 *  
 *     Modifications:   
 *  
 *  @author Arthur  DateTime 2012-7-31 ����7:37:12    
 *  @version 1.0
 */
public abstract class BaseBean {

	public  String filterNull(String data) {
		if(data == null) return "";
		return data;
	}
}
