package com.sptech.qujj.view;

import com.sptech.qujj.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {

	private static MyToast toastCommom;

	private Toast toast;

	private MyToast() {
	}

	public static MyToast createToastConfig() {
		if (toastCommom == null) {
			toastCommom = new MyToast();
		}
		return toastCommom;
	}

	/**
	 * 显示Toast
	 * 
	 * @param context
	 * @param root
	 * @param tvString
	 */

	public void ToastShow(Context context, ViewGroup root, String tvString) {
		View layout = LayoutInflater.from(context).inflate(R.layout.jh_mytoast,
				root);
		TextView text = (TextView) layout.findViewById(R.id.text);
		// ImageView mImageView = (ImageView) layout.findViewById(R.id.iv);
		// mImageView.setBackgroundResource(R.drawable.btn_redeem_noselect);
		text.setText(tvString);
		text.setTextColor(Color.parseColor("#ffffff"));
		toast = new Toast(context);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}
}
