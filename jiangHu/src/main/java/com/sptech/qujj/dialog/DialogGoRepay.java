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

import com.sptech.qujj.R;
import com.sptech.qujj.view.EventHandleListener;

/**
 * 还款提醒-- 弹出框
 * 
 * @author yebr
 * 
 */

public class DialogGoRepay {

	private Context context;
	private Dialog dialog;

	private Button btn_next;
	private ImageView im_close;
	EventHandleListener eventHandleListener;

	public DialogGoRepay(Context context, EventHandleListener eventHandleListener) {
		this.context = context;
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_gorepay, null);

		btn_next = (Button) view.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				eventHandleListener.eventLeftHandlerListener();
			}
		});

		im_close = (ImageView) view.findViewById(R.id.im_close);
		im_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
					// closeDialog();
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
