package com.sptech.qujj.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.sptech.qujj.core.Constants;

public class SimpleAlertDialog extends AlertDialog implements
		DialogInterface.OnClickListener {
	private int id;
	private SimpleDialogHandleListener dialogListener;

	public SimpleAlertDialog(Context context, Activity activity, int id) {
		super(context);
		setOwnerActivity(activity);
		this.id = id;
	}

	public SimpleAlertDialog(Context context, Activity activity, int id,
			SimpleDialogHandleListener listener) {
		super(context);
		setOwnerActivity(activity);
		this.id = id;
		dialogListener = listener;
	}

	public interface SimpleDialogHandleListener {
		public void onDialogConfirm();

		public void onDialogCancel();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		switch (id) {
		case Constants.EXITDIALOG:
			setTitle("提示");
			setMessage("确定退出应用?");
			setCancelable(true);
			setButton(DialogInterface.BUTTON_POSITIVE, "退出", this);
			setButton(DialogInterface.BUTTON_NEGATIVE, "取消", this);
			break;
		default:
			break;
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (id) {
		case Constants.EXITDIALOG:
			if (which == DialogInterface.BUTTON_POSITIVE) {
				System.out.println("退出？");
				dialogListener.onDialogConfirm();
			}
			break;
		default:
			break;
		}
	}
}
