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
 * 手动修改账单状态-- 弹出框
 * 
 * @author yebr
 * 
 */

public class DialogChangeStatus {

	private Context context;
	private Dialog dialog;

	private TextView tv_hint;
	private ImageView im_close, img_popup;
	EventHandleListener eventHandleListener;
	private Button dialog_im_left, dialog_im_right;
	private TextView tv_title;

	private int changestatu;

	public DialogChangeStatus(Context context, int changestatu, EventHandleListener eventHandleListener) {
		this.context = context;
		this.changestatu = changestatu;
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_changestatus, null);
		tv_hint = (TextView) view.findViewById(R.id.tv_hint);
		tv_title = (TextView) view.findViewById(R.id.dialog_name);
		img_popup = (ImageView) view.findViewById(R.id.img_popup);

		if (changestatu == 1) { // 已还清
			tv_hint.setText("您是否确定已经还清额度，确定还清后将不可更改。");
			img_popup.setBackgroundResource(R.drawable.img_popup_card_yes);
		} else if (changestatu == 3) {
			tv_hint.setText("您是否确定已还部分金额？");
			img_popup.setBackgroundResource(R.drawable.img_popup_card_some);
		} else if(changestatu == 4){
			tv_hint.setText("选择删除后将不再显示该银行卡的信息");
			tv_title.setText("删除银行卡");
		}

		im_close = (ImageView) view.findViewById(R.id.im_close);
		im_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});

		// 是
		dialog_im_left = (Button) view.findViewById(R.id.dialog_im_left);
		if(changestatu == 4) dialog_im_left.setText("永久删除");
		dialog_im_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				eventHandleListener.eventLeftHandlerListener();
			}
		});

		// 否
		dialog_im_right = (Button) view.findViewById(R.id.dialog_im_right);
		if(changestatu == 4) dialog_im_right.setText("暂时删除");
		dialog_im_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeDialog();
				eventHandleListener.eventRifhtHandlerListener();

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
