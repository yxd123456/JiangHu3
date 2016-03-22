package com.sptech.qujj.activity;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 添加银行卡
 * 
 * @author gusonglei
 * 
 */
public class AddBankcardActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private EditText et_cardno;
	private TextView et_name, et_banktype;
	private SharedPreferences spf;
	private String name, bankkey, bankid, cardno, stream;
	private Button btn_next;
	private RelativeLayout rl_banktype;
	public static int resultOk = 5;
	private int requestcode = 1;
	private ImageView iv_banklogo;
	private boolean decode = true;
	private List<UsablebankBean> blist = new ArrayList<UsablebankBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbankcard_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		et_name = (TextView) findViewById(R.id.et_name);
		rl_banktype = (RelativeLayout) findViewById(R.id.rl_banktype);
		et_cardno = (EditText) findViewById(R.id.et_cardno);
		iv_banklogo = (ImageView) findViewById(R.id.iv_banklogo);
		et_banktype = (TextView) findViewById(R.id.et_banktype);
		btn_next = (Button) findViewById(R.id.btn_next);
		titleBar.bindTitleContent("添加银行卡", R.drawable.jh_back_selector, 0, "", "");
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
			if (name == null || "".equals(name)) {
				ToastManage.showToast("姓名不能为空");
				return;
			}
			if (cardno == null || "".equals(cardno)) {
				ToastManage.showToast("卡号不能为空");
				return;
			}
			Intent next = new Intent(AddBankcardActivity.this, AddbankcardSubActivity.class);
			next.putExtra("name", name);
			next.putExtra("bankkey", bankkey);
			next.putExtra("bankid", bankid);
			next.putExtra("cardno", cardno);
			startActivity(next);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_banktype:
			Intent banktype = new Intent(AddBankcardActivity.this, UsableBankActivity.class);
			if (decode) {
				doDecode();
			}
			if (blist != null || blist.size() == 0) {
				banktype.putExtra("blist", (Serializable) blist);
			}
			// Java代码 putExtras(key, (Serializable)list)
			// 方法传递过去，接受的时候用
			// Java代码 (List<YourObject>) getIntent().getSerializable(key)
			startActivityForResult(banktype, requestcode);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			decode = false;
			break;
		default:
			break;
		}

	}

	// 解析bankcard
	private void doDecode() {
		String supportbank = spf.getString(Constant.SUPPORTBANK, "");
		System.out.println("doDecode()--supportbank 解析储蓄卡列表 == " + supportbank);
		List<UsablebankBean> buylist = JSON.parseArray(supportbank, UsablebankBean.class);
		blist = buylist;
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
				et_banktype.setText(name);
				// iv_banklogo.setBackgroundResource(R.drawable.img_banklogo);
				if (stream.equals("")) {
					iv_banklogo.setImageResource(R.drawable.img_nobank);
				} else {
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
