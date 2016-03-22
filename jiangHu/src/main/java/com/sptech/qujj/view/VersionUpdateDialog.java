package com.sptech.qujj.view;

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

/**
 * 版本更新-弹出框
 * 
 * @author gusonglei
 * 
 */

public class VersionUpdateDialog {

	private Context context;
	private Dialog dialog;

	private Button dialog_im_left, dialog_im_right;
	private ImageView im_close;
	EventHandleListener eventHandleListener;
	private int is_update;
	private TextView tv_hint;// 提示文字

	public VersionUpdateDialog(Context context, int is_update, EventHandleListener eventHandleListener) {
		this.context = context;
		this.is_update = is_update;
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_updateversion, null);
		tv_hint = (TextView) view.findViewById(R.id.tv_hint);

		dialog_im_left = (Button) view.findViewById(R.id.dialog_im_left);
		dialog_im_right = (Button) view.findViewById(R.id.dialog_im_right);
		im_close = (ImageView) view.findViewById(R.id.im_close);
		if (is_update == 2) {
			im_close.setVisibility(View.GONE);
			dialog_im_left.setVisibility(View.GONE);
		} else {
			dialog_im_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					closeDialog();
					eventHandleListener.eventLeftHandlerListener();
				}
			});
		}
		dialog_im_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// closeDialog();
				if (is_update != 2) {
					closeDialog();
				}
				tv_hint.setText("正在更新中...");
				eventHandleListener.eventRifhtHandlerListener();
				// dialog_im_right.setClickable(false);
				dialog_im_right.setVisibility(View.GONE);
			}
		});
		im_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				eventHandleListener.eventLeftHandlerListener();
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
					if (is_update != 2) {
						closeDialog();
					}
					eventHandleListener.eventLeftHandlerListener();
					return false;
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
