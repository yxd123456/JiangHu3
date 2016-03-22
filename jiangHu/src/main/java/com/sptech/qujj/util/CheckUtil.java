package com.sptech.qujj.util;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.location.LocationManager;

public class CheckUtil {

	// public static boolean checkPwd(String info) {
	// Pattern p = Pattern.compile("^\\w{6,8}$");
	// Matcher m = p.matcher(info);
	// if (m.find()) {
	// return true;
	// }
	// return false;
	// }

	//

	// 验证手机号码
	public static boolean checkMobile(String info) {
		Pattern p = Pattern.compile("^13[0-9]{1}[0-9]{8}$|15[0-9]{1}[0-9]{8}$|17[0678]{1}[0-9]{8}$|18[0-9]{1}[0-9]{8}$");
		Matcher m = p.matcher(info);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// 验证注册密码 6-16位字母与数字组合
	public static boolean checkPWD(String pwd) {
		// ^[a-zA-Z0-9]{6,8}$
		Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$");
		Matcher m = p.matcher(pwd);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// 验证交易密码 6-16位字母或数字，或者组合
	public static boolean checkDealPWD(String pwd) {
		// ^[a-zA-Z0-9]{6,8}$
		Pattern p = Pattern.compile("^[0-9a-zA-Z]{6,16}$");// ^[0-9a-zA-Z]{6,16}$
		Matcher m = p.matcher(pwd);
		if (m.find()) {
			return true;
		}
		return false;
	}

	//
	// 验证身份证号码
	public static boolean checkCard(String card) {
		// ^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		// "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$"
		String mobileString = "^\\d{17}[0-9xX]$";
		Pattern p = Pattern.compile(mobileString);
		Matcher m = p.matcher(card);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// 验证身份证号码是否年满18周岁
	public static boolean checkCard18(String card) {
		// 根据输入的身份证，获取出身年月日
		int size = card.length();
		if (size == 18) {
			String strBirthDay = card.substring(6, 14); // 身份证第6位到第13位为出生日期
			System.out.println("年月日==" + strBirthDay);
			String strYear = strBirthDay.substring(0, 4);
			String strMonth = strBirthDay.substring(4, 6);
			String strDay = strBirthDay.substring(6, 8);
			int year = Integer.valueOf(strYear).intValue(); // atoi(strYear.c_str());
			int month = Integer.valueOf(strMonth).intValue();// atoi(strMonth.c_str());
			int day = Integer.valueOf(strDay).intValue();// atoi(strDay.c_str());
			return CheckBirthDay(year, month, day);
		} else if (size == 15) {
			String strBirthDay = card.substring(6, 12); // 身份证第6位到第11位为出生日期
			System.out.println("年月日==" + strBirthDay);
			String strYear = strBirthDay.substring(0, 2);
			String strMonth = strBirthDay.substring(2, 4);
			String strDay = strBirthDay.substring(4, 6);
			int year = Integer.valueOf(strYear).intValue() + 1900; // 15位身份证
			int month = Integer.valueOf(strMonth).intValue();
			int day = Integer.valueOf(strDay).intValue();
			return CheckBirthDay(year, month, day);
		}

		return false;
	}

	// CheckBirthDay 验证是否年满18
	public static boolean CheckBirthDay(int year, int month, int day) {
		System.out.println("年月日==" + year + " " + month + " " + day);
		// 系统当前时间
		Calendar now = Calendar.getInstance();
		int nowYear = now.get(Calendar.YEAR);
		int nowMonth = (now.get(Calendar.MONTH) + 1);
		int nowDay = now.get(Calendar.DATE);

		System.out.println("年: " + nowYear);
		System.out.println("月: " + nowMonth);
		System.out.println("日: " + nowDay);

		// Date nowTime = new Date(System.currentTimeMillis());
		// System.out.println("系统时间: " + nowTime);

		if ((nowYear - year) < 18) {
			return false;
		} else if ((nowYear - year) == 18) {
			if (nowMonth < month) {
				return false;
			} else if (nowMonth == month) {
				if (nowDay < day) {
					return false;
				}

			}
			return true;
		}
		return true;
	}

	// 验证中文真实姓名
	public static boolean checkRealName(String name) {
		Pattern p = Pattern.compile("^[\u4e00-\u9fa5]{2,4}$");
		Matcher m = p.matcher(name);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// 验证6位短信验证码
	public static boolean checkCode(String info) {
		Pattern p = Pattern.compile("^\\d{6,6}$");
		Matcher m = p.matcher(info);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// 验证是否为邮箱
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	// qp 解码方法
	public static byte[] qpDecoding(String str) {
		if (str == null) {
			return null;
		}
		try {
			str = str.replaceAll("=\n", "");
			byte[] bytes = str.getBytes("US-ASCII");
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				if (b != 95) {
					bytes[i] = b;
				} else {
					bytes[i] = 32;
				}
			}
			if (bytes == null) {
				return null;
			}
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			for (int i = 0; i < bytes.length; i++) {
				int b = bytes[i];
				if (b == '=') {
					try {
						int u = Character.digit((char) bytes[++i], 16);
						int l = Character.digit((char) bytes[++i], 16);
						if (u == -1 || l == -1) {
							continue;
						}
						buffer.write((char) ((u << 4) + l));
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				} else {
					buffer.write(b);
				}
			}
			// return new String(buffer.toByteArray(), "GBK");
			return buffer.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 邮件标题验证
	public static boolean checkSubject(String sub, String str) {
		String mobileString = sub;//
		Pattern p = Pattern.compile(mobileString);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// GPS
	public static final boolean isOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}
		return false;
	}

	// 验证身份证
	public static boolean isIdCard(String arrIdCard) {
		int sigma = 0;
		Integer[] a = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		String[] w = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
		for (int i = 0; i < 17; i++) {
			int ai = Integer.parseInt(arrIdCard.substring(i, i + 1));
			int wi = a[i];
			sigma += ai * wi;
		}
		int number = sigma % 11;
		String check_number = w[number];
		String lastX = arrIdCard.substring(17).toUpperCase();
		System.out.println("lastX == " + lastX);
		if (!arrIdCard.substring(17).equals(check_number)) {
			return false;
		} else {
			return true;
		}
	}

}
