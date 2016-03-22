package com.sptech.qujj.jsonUtil;

import com.alibaba.fastjson.JSON;

public class FastJsonTools {
	public static String createJsonString(Object object) {
		String jsonString = "";
		try {
			jsonString = JSON.toJSONString(object);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return jsonString;
	}
}