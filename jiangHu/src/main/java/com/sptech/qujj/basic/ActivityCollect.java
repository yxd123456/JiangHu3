package com.sptech.qujj.basic;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityCollect {

	public static ArrayList list = new ArrayList();

	public static void add(int pid) {

		for (int i = 0; i < list.size(); i++) {
			if (((Integer) list.get(i)) == pid) {
				return;
			}
		}
		list.add(pid);
	}

	public static void add(Activity aty) {
		if (aty != null)
			list.add(aty);
	}
	
	public static void remove(Activity aty) {
		if (aty != null) {
			for (int i = 0; i < list.size(); i++) {
				if (((Activity) list.get(i)) == aty) {
					list.remove(i);
					break;
				}
			}
		}
	}
}
