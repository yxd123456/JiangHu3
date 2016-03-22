package com.sptech.qujj.db;

import java.util.List;

import com.sptech.qujj.model.Banner;

public class DataProvider {

	public static void addBannerTransaction(List<Banner> list) {
		DbManager.addBannerListTransaction(list);
	}

}
