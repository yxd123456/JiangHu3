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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.view.EventHandleListener;

/**
 * 估算 - 弹出框
 * 
 * @author yebr
 * 
 */

public class DialogGuSuan {

	private Context context;
	private Dialog dialog;

	private Button btn_jisuan;
	private ImageView img_close;
	private EditText ed_tou;
	private TextView tv_date, tv_gusuan;
	EventHandleListener eventHandleListener;

	// 传到 估算弹出框中的数据
	private int dateType;
	private int date;
	private float min_money;
	private String dateString = "";// 项目期限
	private float yearvalue;

	public DialogGuSuan(Context context, int dateType, int date, float min_money, float yearvalue, String dateString, EventHandleListener eventHandleListener) {
		this.context = context;
		this.dateString = dateString;
		this.date = date;
		this.dateType = dateType;
		this.yearvalue = yearvalue;
		this.min_money = min_money;

		System.out.println("dateType= " + dateType);
		System.out.println("yearvalue= " + yearvalue);
		System.out.println("min_money= " + min_money);
		System.out.println("dateString= " + dateString);
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_gusuan, null);

		ed_tou = (EditText) view.findViewById(R.id.ed_tou);
		tv_date = (TextView) view.findViewById(R.id.tv_date);
		tv_gusuan = (TextView) view.findViewById(R.id.tv_gusuan);

		tv_date.setText("期限:" + dateString);
		// ed_tou.setHint("投资金额(" + min_money + "元的倍数)");

		btn_jisuan = (Button) view.findViewById(R.id.btn_jisuan);
		btn_jisuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tv_gusuan.setText("");
				// closeDialog();
				// eventHandleListener.eventRifhtHandlerListener();
				// String dateString = ed_date.getText().toString();
				String money = ed_tou.getText().toString();
				float gusuan;
				if (money.equals("")) {
					ToastManage.showToast("请输入投入金额");
					return;
				}
				double d = Double.parseDouble(money);
				System.out.println("投资金额==" + d);
				double yu = d % min_money;
				System.out.println("余数 ==" + yu);
				if (yu != 0.0) {
					ToastManage.showToast("投资金额需为" + min_money + "元的倍数");
					return;
				} else {
					if (dateType == 1) { // 天
						gusuan = ((float) d * yearvalue * ((float) date / 360) / 100);
						tv_gusuan.setText(DataFormatUtil.floatsaveTwo(gusuan) + "元");
					} else if (dateType == 2) { // 月
						gusuan = (float) (d * yearvalue * ((float) date / 12) / 100);
						tv_gusuan.setText(DataFormatUtil.floatsaveTwo(gusuan) + "元");
					} else if (dateType == 3) { // 年
						gusuan = (float) (d * yearvalue * ((float) date / 100));
						tv_gusuan.setText(DataFormatUtil.floatsaveTwo(gusuan) + "元");
					}

				}

			}
		});

		img_close = (ImageView) view.findViewById(R.id.img_close);
		img_close.setOnClickListener(new OnClickListener() {
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
