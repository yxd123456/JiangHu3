package com.sptech.qujj.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/*
 *  
 * 关于我们
 */

public class AboutUsActivity extends BaseActivity implements OnClickListener,
		OnClickTitleListener {

	private TitleBar titleBar;
	private TextView tv_version, tv_telnum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_set_aboutus);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("关于我们", R.drawable.jh_back_selector, 0, "",
				"");
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("版本：v" + getVersion());
		tv_telnum = (TextView) findViewById(R.id.tv_tellnum);
		titleBar.setOnClickTitleListener(this);
		tv_telnum.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit: // 提交
			// Intent intent = new Intent(this, AlertSettingActivity.class);
			// startActivity(intent);
			break;
		case R.id.tv_tellnum:
			Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ tv_telnum.getText().toString()));
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);
			break;
		default:
			break;
		}

	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	public String getVersion() {
		String version = null;
		PackageManager manager = AboutUsActivity.this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version;

	}
}
