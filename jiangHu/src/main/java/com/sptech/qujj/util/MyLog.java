package com.sptech.qujj.util;

import com.umeng.socialize.utils.Log;

public class MyLog {
	
	private static final int flag = 0;
	
	public static void doLog(String text){
		if(flag == 0){
			Log.d("Test", text);
		}
	}

}
