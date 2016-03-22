package com.sptech.qujj.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.model.UsablebankBean;

public class DataFormatUtil {
	/**
	 * 数据格式化
	 * 
	 * 
	 */
	// flaot 数保留两位
	public static String floatsaveTwo(float f) {
		DecimalFormat fnum = new DecimalFormat("##0.00");
		return fnum.format(f);
	}

	// 银行卡号 取后四位
	public static String bankcardsaveFour(String cardno) {
		// 412721198911163818
		String card = "";
		String text = "";
		if (cardno != null && !"".equals(cardno)) {
			System.out.println("cardno== " + cardno);
			System.out.println("cardno length == " + cardno.length());
			// 去掉卡号带中文的部分
			if (cardno.contains("(")) {
				text = cardno.substring(cardno.indexOf("("), cardno.indexOf(")") + 1);
				System.out.println("text== " + text);
				cardno = cardno.replace(text, "");
				System.out.println("cardno== " + cardno);
			}
			card = cardno.substring(cardno.length() - 4, cardno.length());
			System.out.println("card== " + card);
		}
		return card;
	}

	// 个人信息（姓名隐藏姓氏）
	public static String hideFirstname(String name) {
		String hidename = "";
		if (name != null && !"".equals(name)) {
			String firstname = name.substring(0, 1);
			hidename = name.replaceAll(firstname, "*");
		}
		return hidename;
	}

	// 个人信息（手机号隐藏中间四位）
	public static String hideMobile(String phone) {
		String mitelphone = "";
		if (phone != null && !"".equals(phone)) {
			String four = phone.substring(3, 7);
			// mitelphone = phone.replace(four, "****");
			mitelphone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
		}
		return mitelphone;
	}

	// 获取 储蓄卡 图标流的Map
	public static HashMap<String, String> getCardMap(SharedPreferences spf) {
		HashMap<String, String> cardMap = new HashMap<String, String>();
		// 获取存储的本地的 支持的银行卡
		List<UsablebankBean> usablebanklist = JSON.parseArray(spf.getString(Constant.SUPPORTBANK, ""), UsablebankBean.class);
		// 将解析的银行卡List 转换为Map (银行卡名称，图标流)
		if (usablebanklist.size() != 0) {
			for (int i = 0; i < usablebanklist.size(); i++) {
				cardMap.put(usablebanklist.get(i).getName(), usablebanklist.get(i).getStream());
			}
		}
		return cardMap;
	}

	// 获取 储蓄卡 信息
	public static UsablebankBean getCardInfo(SharedPreferences spf, String cardname) {
		UsablebankBean usablebankBean = null;
		HashMap<String, String> cardMap = new HashMap<String, String>();
		List<UsablebankBean> usablebanklist = JSON.parseArray(spf.getString(Constant.SUPPORTBANK, ""), UsablebankBean.class);
		for (int i = 0; i < usablebanklist.size(); i++) {
			String curname = usablebanklist.get(i).getName();
			if (curname.equals(cardname)) {
				usablebankBean = usablebanklist.get(i);
			}
		}
		return usablebankBean;
	}

	// 获取 信用卡 图标流的Map
	public static HashMap<String, String> getBlueCardMap(SharedPreferences spf) {
		HashMap<String, String> cardMap = new HashMap<String, String>();
		List<UsablebankBean> usablebanklist = JSON.parseArray(spf.getString(Constant.SUPPORTBLUEBANK, ""), UsablebankBean.class);
		// 将解析的银行卡List 转换为Map (银行卡名称，图标流)
		if (usablebanklist.size() != 0) {
			for (int i = 0; i < usablebanklist.size(); i++) {
				cardMap.put(usablebanklist.get(i).getName(), usablebanklist.get(i).getStream());
			}
		}
		return cardMap;
	}

	// 获取 信用卡 信息
	public static UsablebankBean getBlueCardInfo(SharedPreferences spf, String cardname) {
		UsablebankBean usablebankBean = null;
		HashMap<String, String> cardMap = new HashMap<String, String>();
		List<UsablebankBean> usablebanklist = JSON.parseArray(spf.getString(Constant.SUPPORTBLUEBANK, ""), UsablebankBean.class);
		for (int i = 0; i < usablebanklist.size(); i++) {
			String curname = usablebanklist.get(i).getName();
			if (curname.equals(cardname)) {
				usablebankBean = usablebanklist.get(i);
			}
		}
		return usablebankBean;
	}

	public static String hideCard(String card) {
		String mitelcard = "";
		if (card != null || !"".equals(card)) {
			String four = card.substring(6, 14);
			mitelcard = card.replaceAll(four, "****");
		}
		return mitelcard;
	}

	// String userPhoneString = SPHelper.getMobile();
	// String four = userPhoneString.substring(3, 7);
	// String mitelphone = userPhoneString.replaceAll(four, "****");
	// tv_user.setText(mitelphone);

}
