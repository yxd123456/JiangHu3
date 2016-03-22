package com.sptech.qujj.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.view.EventHandleListener;

/**
 * 网络状态提醒-- 弹出框
 * 
 * @author yebr
 * 
 */

public class DialogNetwork {
	private Context context;
	private Dialog dialog;

	private TextView tv_hint;
	private ImageView im_close, img_popup;
	EventHandleListener eventHandleListener;
	private Button dialog_im_left, dialog_im_right;

	private int changestatu;

	public DialogNetwork(Context context, int changestatu, EventHandleListener eventHandleListener) {
		this.context = context;
		this.changestatu = changestatu;
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_network, null);
		tv_hint = (TextView) view.findViewById(R.id.tv_hint);
		// img_popup = (ImageView) view.findViewById(R.id.img_popup);
		dialog_im_left = (Button) view.findViewById(R.id.dialog_im_left);
		dialog_im_right = (Button) view.findViewById(R.id.dialog_im_right);

		if (changestatu == 1) { // 关闭提醒
			tv_hint.setText("关闭提醒后,在2G/3G/4G网络下下载信用卡账单将不再提醒。");
			dialog_im_right.setText("仍然关闭");
		} else if (changestatu == 0) {// 账单连接提醒
			tv_hint.setText("当前使用2G/3G/4G网络，继续下载可能会被运营商收取流量费。");
			dialog_im_right.setText("继续下载");
		}

		im_close = (ImageView) view.findViewById(R.id.im_close);
		im_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});

		// 取消
		dialog_im_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				eventHandleListener.eventLeftHandlerListener();
				closeDialog();
			}
		});

		// 是
		dialog_im_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				eventHandleListener.eventRifhtHandlerListener();
				closeDialog();
			}
		});

		dialog = new Dialog(context, R.style.dialog);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					closeDialog();
					return true;
				}
				return false;
			}
		});
		dialog.show();
	}

	public void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
