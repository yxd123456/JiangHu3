package com.sptech.qujj.jsonUtil;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;

/**
 * author: carey<br>
 * ����ʱ
 */
public class ToolTimeDown {
	private Timer timerSMS;
	private TimerTask timerTaskSMS;
	private int indexTime = 59;
	private Button timeButton;
	private Context context;

	/**
	 * 倒计时
	 */
	public ToolTimeDown(Context context, Button timeButton) {
		this.context = context;
		this.timeButton = timeButton;

		timerSMS = new Timer();
		timerTaskSMS = new TimerTask() {
			public void run() {
				handler.sendEmptyMessage(0);
			}
		};
		timerSMS.schedule(timerTaskSMS, 0, 1000);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// TODO
			super.handleMessage(msg);
			if (indexTime > 0) {
				timeButton.setText(String.valueOf(indexTime--) + "秒后重发");
				timeButton.setEnabled(false);
			} else {
				timeButton.setEnabled(true);
				timeButton.setText("重新获取");
				closeTimer();
			}
		}
	};

	public void closeTimer() {
		if (timerSMS != null) {
			timerSMS.cancel();
			timerSMS = null;
		}
		if (timerTaskSMS != null) {
			timerTaskSMS.cancel();
			timerTaskSMS = null;
		}
	}
}