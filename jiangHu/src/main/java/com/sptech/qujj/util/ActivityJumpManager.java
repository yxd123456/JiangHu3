package com.sptech.qujj.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.sptech.qujj.R;
import com.umeng.socialize.utils.Log;

public class ActivityJumpManager {

	private static int AC_NOTFINISH = 0;
	private static int AC_FINISH = 1;

	public static void jump(Activity srcActivity, Class<?> cls, int switchFlag, int animFlag) {
		/*Log.d("Test", "ActivityJumpManager的jump方法被调用了");
		Toast.makeText(srcActivity, "ActivityJumpManager的jump方法被调用了", 0).show();*/
		Intent intent = new Intent(srcActivity, cls);
		intent.putExtra("from", "normal");
		intent.putExtra("isShow", false);
		srcActivity.startActivity(intent);
		switch (switchFlag) {
		case 0:
			break;
		case 1:
			srcActivity.finish();
			break;
		}

		switch (animFlag) {
		case 0:
			srcActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		case 1:
			srcActivity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case 2:
			srcActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case 3:
			srcActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case 4:
			break;
		case 5:
			srcActivity.overridePendingTransition(R.anim.ac_up_open, R.anim.ac_down_close);
			break;
		}
	}

	public static void jumpDelay(final Activity srcActivity, final Class<?> cls, final int switchFlag, final int animFlag, int second) {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(srcActivity, cls);
				intent.putExtra("from", "normal");
				intent.putExtra("isShow", false);
				srcActivity.startActivity(intent);
				switch (switchFlag) {
				case 0:
					break;
				case 1:
					srcActivity.finish();
					break;
				}

				switch (animFlag) {
				case 0:
					srcActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				case 1:
					srcActivity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
					break;
				case 2:
					srcActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					break;
				case 3:
					srcActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					break;
				case 4:
					break;
				case 5:
					srcActivity.overridePendingTransition(R.anim.ac_up_open, R.anim.ac_down_close);
					break;
				}
			}
		}, second);

	}

	public static void finishActivity(final Activity srcActivity, final int animFlag) {
		srcActivity.finish();
		switch (animFlag) {
		case 0:
			srcActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		case 1:
			srcActivity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case 2:
			srcActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case 3:
			srcActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case 4:
			break;
		case 5:
			srcActivity.overridePendingTransition(R.anim.ac_up_open, R.anim.ac_down_close);
			break;
		}

	}

	public static void jumpToCity(final Activity srcActivity, final Class<?> cls) {
		jump(srcActivity, cls, AC_NOTFINISH, 3);
	}

	public static void jumpToMain(final Activity srcActivity, final Class<?> cls) {
		jump(srcActivity, cls, AC_FINISH, 1);
	}

	public static void jumpToPwdGue(final Activity srcActivity, final Class<?> cls) {
		jump(srcActivity, cls, AC_NOTFINISH, 5);
	}

	public static void jumpToAddEvent(final Activity srcActivity, final Class<?> cls) {
		jump(srcActivity, cls, AC_NOTFINISH, 2);
	}

	public static void jumpToEventDetail(final Activity srcActivity, final Class<?> cls) {
		jump(srcActivity, cls, AC_NOTFINISH, 2);
	}

	public static void jumpToLogin(final Activity srcActivity, final Class<?> cls) {
		jump(srcActivity, cls, AC_FINISH, 1);
	}

	public static void jumpToPicQuality(final Activity srcActivity, final Class<?> cls) {
		jump(srcActivity, cls, AC_NOTFINISH, 2);
	}

}
