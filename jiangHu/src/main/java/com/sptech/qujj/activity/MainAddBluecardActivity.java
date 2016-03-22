package com.sptech.qujj.activity;

import java.util.HashMap;
import java.util.List;

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

import com.alibaba.fastjson.JSON;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogData;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.CircleImageView;
import com.sptech.qujj.view.EventDataListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 首页 -- 绑定行用卡 （开户行固定）
 * 
 * @author sp-dev-06
 * 
 */
public class MainAddBluecardActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private EditText et_cardno, et_usedata, et_usecvv;
	private TextView et_name, et_banktype;
	private SharedPreferences spf;
	private String name, bankkey, bankid, cardno;
	private Button btn_next;
	private CircleImageView iv_banklogo;
	private ImageView iv_cvvanswer;
	private String stream, usecvv, usedata;
	private RelativeLayout rl_banktype;
	// public static int resultOk = 5;
	// private int requestcode = 1;

	private int account_id;
	private String bank_name = ""; //
	private ImageView img_btnright;
	HashMap<String, String> cardMap = new HashMap<String, String>();
	private UsablebankBean curbankBean = new UsablebankBean();
	private String curUsedata = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbluecard_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		bank_name = getIntent().getStringExtra("bank_name");
		account_id = getIntent().getIntExtra("account_id", 0);
		System.out.println("bankName= " + bank_name + "   account_id ==" + account_id);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		HashMap<String, String> cardMap = new HashMap<String, String>();
		List<UsablebankBean> usablebanklist = JSON.parseArray(spf.getString(Constant.SUPPORTBLUEBANK, ""), UsablebankBean.class);
		// 将解析的银行卡List 转换为Map (银行卡名称，图标流)
		if (usablebanklist.size() != 0) {
			for (int i = 0; i < usablebanklist.size(); i++) {
				// cardMap.put(usablebanklist.get(i).getName(), usablebanklist
				// .get(i).getStream());
				if (usablebanklist.get(i).getName().equals(bank_name)) {
					curbankBean = usablebanklist.get(i);
				}
			}
		}

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		et_name = (TextView) findViewById(R.id.et_name);
		rl_banktype = (RelativeLayout) findViewById(R.id.rl_banktype);
		et_cardno = (EditText) findViewById(R.id.et_cardno);
		et_usedata = (EditText) findViewById(R.id.et_usedata);
		et_usecvv = (EditText) findViewById(R.id.et_usecvv);

		et_usedata.setOnClickListener(this);
		iv_banklogo = (CircleImageView) findViewById(R.id.iv_banklogo);
		iv_cvvanswer = (ImageView) findViewById(R.id.iv_cvvanswer);
		iv_cvvanswer.setOnClickListener(this);
		et_banktype = (TextView) findViewById(R.id.et_banktype);
		btn_next = (Button) findViewById(R.id.btn_next);
		titleBar.bindTitleContent("绑定信用卡", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_next.setOnClickListener(this);
		// rl_banktype.setOnClickListener(this);
		et_name.setText(spf.getString(Constant.USER_NAME, "未知") + "");

		img_btnright = (ImageView) findViewById(R.id.img_btnright); // 隐藏 右箭头
		img_btnright.setVisibility(View.INVISIBLE);

		cardMap = DataFormatUtil.getBlueCardMap(spf);

		// 获取银行头像
		String cardStream = cardMap.get(bank_name);
		System.out.println("card--name==" + bank_name);
		System.out.println("card--img==" + cardStream);

		et_banktype.setText(bank_name + "");
		iv_banklogo.setVisibility(View.VISIBLE);
		if (cardStream == null || cardStream.equals("")) {
			iv_banklogo.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(cardStream);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			iv_banklogo.setImageBitmap(bit);
		}

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

			// if (name.length() == 0) {
			// ToastManage.showToast("姓名不能为空");
			// return;
			// }
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
			Intent next = new Intent(MainAddBluecardActivity.this, AddbluecardSubActivity.class);
			next.putExtra("name", bank_name);

			if (curbankBean.getKey() != null) {
				next.putExtra("bankkey", curbankBean.getKey());// bankkey
				System.out.println(" bankKey== " + curbankBean.getKey());
			}
			if (curbankBean.getId() != null) {
				next.putExtra("bankid", curbankBean.getId());
				System.out.println(" bankid== " + curbankBean.getId());
			}

			next.putExtra("cardno", cardno);
			next.putExtra("bankcvv", usecvv);
			// next.putExtra("bankdata", usedata);
			next.putExtra("bankdata", curUsedata);
			next.putExtra("account_id", account_id);

			// next.putExtra("help_id", helpid);
			startActivity(next);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		// case R.id.rl_banktype:
		// Intent banktype = new Intent(MainAddBluecardActivity.this,
		// UsableBlueCardActivity.class);
		// startActivityForResult(banktype, requestcode);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		// break;
		case R.id.iv_cvvanswer:
			Intent answer = new Intent(MainAddBluecardActivity.this, BluecardJieshaoActivity.class);
			startActivity(answer);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.et_usedata:
			DialogData dr = new DialogData(MainAddBluecardActivity.this, new EventDataListener() {

				@Override
				public void eventDataHandlerListener(String data, String showdata) {
					// et_usedata.setText(data + "");
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

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// switch (requestCode) {
	// case 1:
	// // TODO
	// if (resultCode == resultOk) {
	// name = data.getStringExtra("name");
	// bankkey = data.getStringExtra("key");
	// bankid = data.getStringExtra("id");
	// stream = data.getStringExtra("stream");
	// et_banktype.setText(name + "");
	// if (stream.equals("")) {
	// iv_banklogo.setImageResource(R.drawable.img_nobank);
	// } else {
	// byte[] b = Base64.decode(stream);
	// Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
	// iv_banklogo.setImageBitmap(bit);
	// }
	// }
	// break;
	// default:
	// break;
	// }
	// }

}
