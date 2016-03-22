package com.sptech.qujj.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.sptech.qujj.basic.BaseActivity;

/**
 * 按两次返回，退出应用
 * 
 * @author yebr
 * 
 */
public class DoubleClickExitHelper extends BaseActivity {

	private final Activity mActivity;
	private boolean isOnKeyBacking;
	private Handler mHandler;
	private Toast mBackToast;

	public DoubleClickExitHelper(Activity activity) {
		mActivity = activity;
		mHandler = new Handler(Looper.getMainLooper());
		// mHandler = new Handler();
	}

	/**
	 * Activity onKeyDown事件
	 * */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK) {
			return false;
		}
		if (isOnKeyBacking) {
			mHandler.removeCallbacks(onBackTimeRunnable);
			if (mBackToast != null) {
				mBackToast.cancel();
			}
			// BaseActivity.closeActivity;
			BaseActivity baseActivity = new BaseActivity();
			baseActivity.closeActivity();

			// mActivity.finish();
			return true;
		} else {
			isOnKeyBacking = true;
			if (mBackToast == null) {
				mBackToast = Toast.makeText(mActivity, "再按一次退出程序", 2000);
			}
			mBackToast.show();
			// ToastManage.showToast("进来???");
			// 延迟两秒的时间，把Runable发出去
			mHandler.postDelayed(onBackTimeRunnable, 2000);
			return true;
		}
	}

	private Runnable onBackTimeRunnable = new Runnable() {

		@Override
		public void run() {
			isOnKeyBacking = false;
			if (mBackToast != null) {
				mBackToast.cancel();
			}
		}
	};

}
