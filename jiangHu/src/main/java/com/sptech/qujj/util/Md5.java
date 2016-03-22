package com.sptech.qujj.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

	public static String md5s(String plainText) {
		String str;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
			// System.out.println("result: " + buf.toString()); //32
			// System.out.println("result: " + buf.toString().substring(8,
			// 24));// 16
			return str;

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("plainText==" + plainText);
		}
		return "";
	}

	public static void main(String agrs[]) {
		Md5 md51 = new Md5();
		md51.md5s("123456");
	}

}