package com.sptech.qujj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/*
 *  
 * 手动申请
 */

public class HandapplyActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private Button btn_next;
	private SharedPreferences spf;
	private EditText et_applynum, et_usedata;
	private String applynum, usedata;
	private TextView tv_handmoney, tv_repaydate, tv_tellnum;
	private DialogHelper dialogHelper;
	private ImageView img_clear; // input 清除按钮
	/** 输入框小数的位数 */
	private static final int DECIMAL_DIGITS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handapply_layout);
		initView();
		Tools.addActivityList(HandapplyActivity.this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("手动申请", R.drawable.jh_back_selector, 0, "", "常见问题");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		btn_next = (Button) findViewById(R.id.btn_next);
		tv_handmoney = (TextView) findViewById(R.id.tv_handmoney);
		tv_repaydate = (TextView) findViewById(R.id.tv_repaydate);
		tv_tellnum = (TextView) findViewById(R.id.tv_tellnum);
		et_applynum = (EditText) findViewById(R.id.et_applynum);

		et_usedata = (EditText) findViewById(R.id.et_usedata);
		btn_next.setOnClickListener(this);
		tv_tellnum.setOnClickListener(this);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear.setOnClickListener(this);

		et_applynum.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {

				if (text.toString().contains(".")) {
					if (text.length() - 1 - text.toString().indexOf(".") > 2) {
						text = text.toString().subSequence(0, text.toString().indexOf(".") + 3);
						et_applynum.setText(text);
						et_applynum.setSelection(text.length());
					}
				}
				if (text.toString().trim().substring(0).equals(".")) {
					text = "0" + text;
					et_applynum.setText(text);
					et_applynum.setSelection(2);
				}

				if (text.toString().startsWith("0") && text.toString().trim().length() > 1) {
					if (!text.toString().substring(1, 2).equals(".")) {
						et_applynum.setText(text.subSequence(0, 1));
						et_applynum.setSelection(1);
						return;
					}
				}

				// 计算 手续费
				if (start == 0) {
					tv_handmoney.setText("");
					tv_repaydate.setText("");
				} else {
					String text1 = text + "";
					float tf = Float.parseFloat(text1);
					String money = DataFormatUtil.floatsaveTwo(tf);
					float nianhua = 0.22F;
					float nian = 12f;
					float num = 15f;
					float gong = tf * nianhua / nian + num;
					tv_handmoney.setText("·手续费：" + DataFormatUtil.floatsaveTwo(gong) + "元");
					tv_repaydate.setText("·您需在" + DateManage.getDate() + "前还款" + DataFormatUtil.floatsaveTwo((gong + tf)) + "元（还款金额=本金+手续费）");
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (et_applynum.getText().toString() != null && !et_applynum.getText().toString().equals("")) {
					img_clear.setVisibility(View.VISIBLE);
				} else {
					img_clear.setVisibility(View.INVISIBLE);
				}
			}
		});

		// et_applynum.setFilters(new InputFilter[] { new MyInputFilter(text1)
		// });
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			nextsub();
			break;
		case R.id.tv_tellnum:
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_tellnum.getText().toString()));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.img_clear:
			et_applynum.setText("");
			break;
		default:
			break;
		}
	}

	private void nextsub() {
		applynum = et_applynum.getText().toString().trim();
		if (applynum == null || "".equals(applynum)) {
			ToastManage.showToast("申请额度不能为空");
			return;
		}
		float apply = Float.parseFloat(applynum);
		if (apply > 10000 || apply < 1000) {
			ToastManage.showToast("申请额度应为1000-10000");
			return;
		}
		Intent in = new Intent(HandapplyActivity.this, HandAddbluecardActivity.class);
		in.putExtra("applynum", apply);// applynum
		in.putExtra("addflag", true);
		startActivity(in);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		Intent rela_service = new Intent(HandapplyActivity.this, WebViewActivity.class);
		rela_service.putExtra("url", JsonConfig.HTML + "/index/service");
		rela_service.putExtra("title", "服务中心");
		startActivity(rela_service);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

	}

}
