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
import com.sptech.qujj.view.EventAlertListener;

/**
 * 提醒设置--还款设置和代还款设置弹出框
 * 
 * @author gusonglei
 * 
 */

public class DialogAlert {

	private Context context;
	private Dialog dialog;
	private DatePicker dp_data;
	private Button dialog_im_left, dialog_im_right;
	private ImageView im_close;
	private String day = "15", hour = "06", min = "30";
	EventAlertListener eventAlerteListener;
	private PickerView minute_pv;
	private PickerView second_pv;
	private PickerView year_pv;
	private int dd, hh, mm;

	public DialogAlert(Context context, int dd, int hh, int mm, EventAlertListener eventAlerteListener) {
		this.context = context;
		this.dd = dd;
		this.hh = hh;
		this.mm = mm;
		this.eventAlerteListener = eventAlerteListener;
	}

	@SuppressLint({ "CutPasteId", "ResourceAsColor" })
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_alert, null);
		if (dd < 10) {
			day = "0" + String.valueOf(dd);
		} else {
			day = String.valueOf(dd);
		}
		if (hh < 10) {
			hour = "0" + String.valueOf(hh);
		} else {
			hour = String.valueOf(hh);
		}
		if (mm < 10) {
			min = "0" + String.valueOf(mm);
		} else {
			min = String.valueOf(mm);
		}

		minute_pv = (PickerView) view.findViewById(R.id.minute_pv);
		second_pv = (PickerView) view.findViewById(R.id.second_pv);
		year_pv = (PickerView) view.findViewById(R.id.year_pv);
		List<String> data = new ArrayList<String>();
		List<String> seconds = new ArrayList<String>();
		List<String> year = new ArrayList<String>();

		for (int i = 0; i < 31; i++) {
			year.add(i < 10 ? "0" + i : "" + i);
		}
		for (int i = 0; i < 24; i++) {
			data.add(i < 10 ? "0" + i : "" + i);
		}
		for (int i = 0; i < 61; i++) {
			seconds.add(i < 10 ? "0" + i : "" + i);
		}
		year_pv.setData(year);
		year_pv.setSelected(dd);
		year_pv.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				day = text;

			}
		});
		minute_pv.setData(data);
		minute_pv.setSelected(hh);
		minute_pv.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				hour = text;
			}
		});
		second_pv.setData(seconds);
		second_pv.setSelected(mm);
		second_pv.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				min = text;
			}
		});
		dialog_im_right = (Button) view.findViewById(R.id.dialog_im_right);
		dialog_im_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeDialog();
				String data = day + "天" + hour + ":" + min;
				int miao = ((Integer.valueOf(min)) * 60) + ((Integer.valueOf(hour)) * 60 * 60) + ((Integer.valueOf(day) * 24 * 60 * 60));
				eventAlerteListener.eventDataHandlerListener(miao, data);
				;

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
