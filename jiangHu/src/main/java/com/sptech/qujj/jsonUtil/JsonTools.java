package com.sptech.qujj.jsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * @author: jiangsheng 2012-10-10
 */
public class JsonTools {

	/*
	 * public static String getCommonJson(Context ctx,String action) throws
	 * JSONException{ JSONStringer stringer = new JSONStringer().object();
	 * getBaseJson(stringer, ctx, action); stringer.endObject(); return
	 * stringer.toString(); }
	 */

	public static Map<String, Object> getBaseJson(Map<String, Object> map,
			Context ctx) throws JSONException {
		String passtime = String.valueOf(System.currentTimeMillis());
		// // Date date = new Date(System.currentTimeMillis());
		// // //��ʽ��ʱ��
		// // SimpleDateFormat sdf=new
		// // java.text.SimpleDateFormat("yyyyMMddHHmmss");
		// // String returnStr = sdf.format(date);
		// // System.out.println("��ǰʱ����    "+returnStr);
		//
		// String curTime = DateManage.getformatTodayDate();
		// System.out.println("��ǰʱ����     " + curTime);
		// map.put("time", curTime); // ��ǰʱ��
		// map.put("accesskey", "chuhaiwang"); // accesskey
		// // map.put("privacykey", " "); //��Կ
		// map.put("acesstoken", Md5.md5s("chuhaiwang" + curTime)); // token
		//
		// map.put("platform", "android");
		// map.put("deviceInfo", Helper.getBrand() + " " + Helper.getOS());
		// // map.put("pushToken", SPHelper.getCilentId());
		// map.put("passTime", passtime);
		// // map.put("sign", Tools.createPwd("rocky"+"&"+Helper.getImei(ctx) +
		// // "&"+SPHelper.getAccessToken()+ "&" + SPHelper.getRefreshToken()+
		// "&"
		// // + passtime));
		return map;
	}

	public static ArrayList<Map<String, Object>> getListFromArray(
			JSONArray jsonArray) {
		try {
			JSONObject jsonObject;
			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				list.add(getMap(jsonObject.toString()));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Map<String, Object>>();

	}

	public static Map<String, Object> getMap(String jsonString) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				if (value instanceof String) {
					value = filterNull((String) value);
				}
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String filterNull(String data) {
		if (data == null)
			return "";
		return data;
	}
}
