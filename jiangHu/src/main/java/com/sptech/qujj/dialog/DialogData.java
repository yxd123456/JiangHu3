package com.sptech.qujj.dialog;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.DatePicker;
import android.widget.ImageView;

import com.sptech.qujj.R;
import com.sptech.qujj.util.PickerView;
import com.sptech.qujj.util.PickerView.onSelectListener;
import com.sptech.qujj.view.EventDataListener;

/**
 * 信用卡有限期 -选择弹出框
 * 
 * @author gusonglei
 * 
 */

public class DialogData {

	private Context context;
	private Dialog dialog;
	private DatePicker dp_data;
	private Button dialog_im_left, dialog_im_right;
	private ImageView im_close;
	private String years = "2015", month = "01";
	EventDataListener eventHandleListener;
	private PickerView minute_pv;
	private PickerView year_pv;

	public DialogData(Context context, EventDataListener eventHandleListener) {
		this.context = context;
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint({ "CutPasteId", "ResourceAsColor" })
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_data, null);
		minute_pv = (PickerView) view.findViewById(R.id.minute_pv);
		year_pv = (PickerView) view.findViewById(R.id.year_pv);
		List<String> data = new ArrayList<String>();
		List<String> seconds = new ArrayList<String>();
		List<String> year = new ArrayList<String>();
		for (int i = 2015; i < 2030; i++) {
			year.add(i + "");
		}
		for (int i = 1; i < 13; i++) {
			data.add(i < 10 ? "0" + i : "" + i);
		}

		year_pv.setData(year);
		year_pv.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				years = text;

			}
		});
		minute_pv.setData(data);
		minute_pv.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				month = text;
			}
		});

		dialog_im_right = (Button) view.findViewById(R.id.dialog_im_right);
		dialog_im_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeDialog();
				String yearss = String.valueOf(years).substring(2, 4);

				String data = yearss + month;
				String showdata = years + "年" + month + "月";
				eventHandleListener.eventDataHandlerListener(data, showdata);

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
