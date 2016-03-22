package com.sptech.qujj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class RealnameSuccessActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private Button btn_importbill, btn_noimportbill;
	private int nextflag = 0; // 默认为 跳到首页
	private int pro_id = 0; // 产品id

	// nextflag ! 点击下次先 ，分情况: 0 表示 实名认证的时候 -- 回到首页;
	// 1 表示 交易的时候-- 返回到产品介绍页;
	// 2 表示 充值 提现的时候,跳到账户余额 页面
	// 3 在账户安全 点击实名认证，完成后跳回个人中心
	// 4在手动申请界面，完成实名认证后跳转到手动申请界面
	// 5在添加银行卡界面，完成实名认证后跳转到添加银行卡界面
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realnameok_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		nextflag = getIntent().getIntExtra("nextflag", 0);

		if (nextflag == 1) {
			pro_id = getIntent().getExtras().getInt("id");
		}
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btn_importbill = (Button) findViewById(R.id.btn_importbill);
		btn_noimportbill = (Button) findViewById(R.id.btn_noimportbill);
		titleBar.bindTitleContent("实名认证完成", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_importbill.setOnClickListener(this);
		btn_noimportbill.setOnClickListener(this);

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
		case R.id.btn_importbill:
			// ToastManage.showToast("导入邮箱账单");
			// Intent add = new Intent(this, AddBankcardActivity.class);
			Intent add = new Intent(this, AddMailActivity.class);
			startActivity(add);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_noimportbill:
			// 下次先 ，分情况: 0 表示 实名认证的时候 -- 回到首页;
			// 1 表示 交易的时候-- 返回到产品介绍页
			// 2 表示 充值 提现的时候,跳到账户余额 页面
			// 3 在账户安全 点击实名认证，完成后跳回个人中心
			// 4在手动申请界面，完成实名认证后跳转到手动申请界面
			// 5在添加银行卡界面，完成实名认证后跳转到添加银行卡界面
			Intent intent;
			if (nextflag == 0) {
				intent = new Intent(RealnameSuccessActivity.this, MainActivity.class);
				intent.putExtra("isShow", false);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else if (nextflag == 1) {
				// intent = new Intent(RealnameSuccessActivity.this,
				// LiCaiActivity.class);
				intent = new Intent(RealnameSuccessActivity.this, ProductInfoActivity.class);
				intent.putExtra("id", pro_id);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else if (nextflag == 2) {
				intent = new Intent(RealnameSuccessActivity.this, AccountBalanceActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else if (nextflag == 3) {
				intent = new Intent(RealnameSuccessActivity.this, PersonInfoActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			if (nextflag == 4) {
				intent = new Intent(RealnameSuccessActivity.this, HandapplyActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else if (nextflag == 5) {
				intent = new Intent(RealnameSuccessActivity.this, AddBankcardActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				break;
			}

			break;

		default:
			break;
		}
	}

}
