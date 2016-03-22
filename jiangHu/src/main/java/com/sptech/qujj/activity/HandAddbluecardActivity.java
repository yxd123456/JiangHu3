package com.sptech.qujj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.CircleImageView;
import com.sptech.qujj.view.EventDataListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 手动添加信用卡
 * 
 * @author gusonglei
 * 
 */
public class HandAddbluecardActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private EditText et_cardno, et_usedata, et_usecvv;
	private TextView et_name, et_banktype;
	private SharedPreferences spf;
	private String name, bankkey, bankid, cardno, username;
	private Button btn_next;
	private CircleImageView iv_banklogo;
	private ImageView iv_cvvanswer;
	private String stream, usecvv, usedata;
	private RelativeLayout rl_banktype;
	public static int resultOk = 3;
	private int requestcode = 1;
	private int account_id;
	private float applynum;
	private boolean Addflag; // 手动申请还款（true） 和 添加行用卡(false)的 区分
	private String curUsedata = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbluecard_layout);
		initView();
		Tools.addActivityList(HandAddbluecardActivity.this);
	}

	private void initView() {
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		et_name = (TextView) findViewById(R.id.et_name);
		rl_banktype = (RelativeLayout) findViewById(R.id.rl_banktype);
		et_cardno = (EditText) findViewById(R.id.et_cardno);
		et_usedata = (EditText) findViewById(R.id.et_usedata);
		et_usecvv = (EditText) findViewById(R.id.et_usecvv);

		// account_id = getIntent().getIntExtra("account_id", 0);
		applynum = getIntent().getFloatExtra("applynum", 0);
		Addflag = getIntent().getBooleanExtra("addflag", false);
		System.out.println("HandAddbluecardActivity--Addflag ==  " + Addflag);

		System.out.println("applynum== " + applynum);

		et_usedata.setOnClickListener(this);
		iv_banklogo = (CircleImageView) findViewById(R.id.iv_banklogo);
		iv_cvvanswer = (ImageView) findViewById(R.id.iv_cvvanswer);
		iv_cvvanswer.setOnClickListener(this);
		et_banktype = (TextView) findViewById(R.id.et_banktype);
		btn_next = (Button) findViewById(R.id.btn_next);
		titleBar.bindTitleContent("绑定信用卡", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_next.setOnClickListener(this);
		rl_banktype.setOnClickListener(this);
		et_name.setText(spf.getString(Constant.USER_NAME, "未知") + "");
	}

	@Override
	public void onLeftButtonClick(View view) {
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			cardno = et_cardno.getText().toString().trim();
			usecvv = et_usecvv.getText().toString().trim();
			// usedata = et_usedata.getText().toString().trim();

			if (cardno == null || "".equals(cardno)) {
				ToastManage.showToast("卡号不能为空");
				return;
			}
			// if (usedata == null || "".equals(usedata)) {
			// ToastManage.showToast("请选择卡片有效期");
			// return;
			// }
			if ("".equals(curUsedata)) {
				ToastManage.showToast("请选择卡片有效期");
				return;
			}
			if (usecvv == null || "".equals(usecvv)) {
				ToastManage.showToast("请输入安全码");
				return;
			}
			if (name == null || "".equals(name)) {
				ToastManage.showToast("请选择开户行");
				return;
			}
			Intent next = new Intent(HandAddbluecardActivity.this, HandAddbluecardsubActivity.class);

			next.putExtra("addflag", Addflag);
			next.putExtra("name", name);
			next.putExtra("bankkey", bankkey);
			next.putExtra("bankid", bankid);
			next.putExtra("cardno", cardno);
			next.putExtra("bankcvv", usecvv);
			// next.putExtra("bankdata", usedata);
			next.putExtra("bankdata", curUsedata);
			// next.putExtra("acount_id", account_id);
			next.putExtra("applynum", applynum);
			// System.out.println("applynum==" + applynum);
			startActivity(next);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_banktype:
			Intent banktype = new Intent(HandAddbluecardActivity.this, UsableHandBankActivity.class);
			startActivityForResult(banktype, requestcode);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.iv_cvvanswer:
			Intent answer = new Intent(HandAddbluecardActivity.this, BluecardJieshaoActivity.class);
			startActivity(answer);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.et_usedata:
			DialogData dr = new DialogData(HandAddbluecardActivity.this, new EventDataListener() {

				@Override
				public void eventDataHandlerListener(String data, String showdata) {
					et_usedata.setText(showdata + "");
					curUsedata = data;
				}
			});
			dr.createMyDialog();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			// TODO
			if (resultCode == resultOk) {
				name = data.getStringExtra("name");
				bankkey = data.getStringExtra("key");
				bankid = data.getStringExtra("id");
				stream = data.getStringExtra("stream");
				et_banktype.setText(name + "");
				if (stream.equals("")) {
					// iv_banklogo.setImageResource(R.drawable.img_nobank);
					iv_banklogo.setVisibility(View.GONE);
				} else {
					iv_banklogo.setVisibility(View.GONE);
					byte[] b = Base64.decode(stream);
					Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
					iv_banklogo.setImageBitmap(bit);
				}
			}
			break;
		default:
			break;
		}
	}

}
