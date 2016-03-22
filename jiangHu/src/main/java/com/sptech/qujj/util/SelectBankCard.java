package com.sptech.qujj.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.baidu.location.ac;
import com.sptech.qujj.R;
import com.sptech.qujj.ReplenishCreditCardDetailActivity;
import com.sptech.qujj.activity.UsableBankActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.model.UsablebankBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectBankCard {
	
	private Activity activity;
	private boolean decode = true;
	private SharedPreferences spf;
	private List<UsablebankBean> blist = new ArrayList<UsablebankBean>();
	private String cardname;
	private String bankkey;
	private String bankid;
	private String stream;
	
	public SelectBankCard(Activity activity){
		this.activity = activity;
		spf = activity.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
	}
	
	public void select(){
		Intent banktype = new Intent(activity, UsableBankActivity.class);
		if (decode  ) {
			doDecode();
		}
		if (blist != null || blist.size() == 0) {
			banktype.putExtra("blist", (Serializable) blist);
		}
		// Java代码 putExtras(key, (Serializable)list)
		// 方法传递过去，接受的时候用
		// Java代码 (List<YourObject>) getIntent().getSerializable(key)
		activity.startActivityForResult(banktype, 1);
		activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		decode = false;
	}
	
	public String[] backToShow(Intent data, TextView tv_bank_name, ImageView iv_bank_logo){
		cardname = data.getStringExtra("name");
		bankkey = data.getStringExtra("key");
		bankid = data.getStringExtra("id");
		stream = data.getStringExtra("stream");
		tv_bank_name.setText(cardname);
		// iv_banklogo.setBackgroundResource(R.drawable.img_banklogo);
		if (stream.equals("")) {
			iv_bank_logo.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(stream);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			iv_bank_logo.setImageBitmap(bit);
		}
		return new String[]{bankkey, bankid};
	}
	
	private void doDecode() {
		String supportbank = spf.getString(Constant.SUPPORTBANK, "");
		System.out.println("doDecode()--supportbank 解析储蓄卡列表 == " + supportbank);
		List<UsablebankBean> buylist = JSON.parseArray(supportbank, UsablebankBean.class);
		blist = buylist;
	}
	
}
