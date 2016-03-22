package com.sptech.qujj.toast;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

public class ToastManage {
	private static final String MSG_KEY_DATASTRING = "text";
	public static Context ctx;
	private static Toast mToast;
	private static Toast toast; // wode
	private static Toast mToastNow;

	public static void init(Context context) {
		ctx = context;
	}

	public static Handler errorHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 0:
				String str = message.getData().getString(MSG_KEY_DATASTRING);
				if (ToastManage.mToast == null) {
					ToastManage.mToast = Toast
							.makeText(ToastManage.ctx, str, 0);
				} else {
					ToastManage.mToast.setText(str);
				}
				ToastManage.mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); // 设置toast中间显示
				ToastManage.mToast.show();
				break;
			default:
				return;
			}
		}
	};

	public static void showToast(String msg) {
		checkHasInit();
		Message message = errorHandler.obtainMessage(0);
		Bundle bundle = new Bundle();
		bundle.putString(MSG_KEY_DATASTRING, msg);
		message.setData(bundle);
		errorHandler.sendMessage(message);
	}

	public static void showToastNow(String msg) {
		checkHasInit();
		if (mToastNow == null)
			mToastNow = Toast.makeText(ctx, msg, 0);
		else {
			mToastNow.setText(msg);
		}
		mToastNow.show();
	}

	public static void checkHasInit() {
		if (ctx == null) {
			throw new NullPointerException(
					"not init ToastManager��please call init() in app oncreate() first!");
		}
	}

	// public static void ToastShow(Context context, String tvString) {
	// View layout = LayoutInflater.from(context).inflate(
	// R.layout.jh_mytoast_parent, null);
	// TextView text = (TextView) layout.findViewById(R.id.text);
	// // ImageView mImageView = (ImageView) layout.findViewById(R.id.iv);
	// // mImageView.setBackgroundResource(R.drawable.ic_launcher);
	// text.setText(tvString);
	// toast = new Toast(context);
	// toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	// toast.setDuration(Toast.LENGTH_LONG);
	// toast.setView(layout);
	// toast.show();
	//
	// }

}