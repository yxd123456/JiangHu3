package com.sptech.qujj.basic;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class SlideBasicActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	public void closeActivity() {
		float version;
		version = Float.parseFloat(android.os.Build.VERSION.RELEASE.substring(0, 3));
		if (version > 2.1) {
			for (int i = 0; i < ActivityCollect.list.size(); i++) {
				if (ActivityCollect.list.get(i) != null)
					((Activity) ActivityCollect.list.get(i)).finish();
			}
		} else {
			ActivityManager actMgr = (ActivityManager) SlideBasicActivity.this.getSystemService(ACTIVITY_SERVICE);
			actMgr.restartPackage(getPackageName());
		}
		finish();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}