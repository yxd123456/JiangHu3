package com.sptech.qujj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * 通用的自定义布局的dialog
 * 
 * @author 谷松磊
 * @since 2014-12-30
 */
public class CustomDialog extends Dialog {
	private int mLayoutId = 0;

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme, int layoutId) {
		super(context, theme);
		mLayoutId = layoutId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mLayoutId);
		setCanceledOnTouchOutside(false);
	}

}
