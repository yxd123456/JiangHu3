package com.sptech.qujj;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.widget.TextView;

/**
 * ɳ©��������ʾ����ʱ�ķ�װ��
 * @author YeXuDong
 *
 */
public class Hourglass {
	
	private Timer timer = new Timer();
	private TextView textView;
	private int number;
	private String string;
	private OnTimeOverListener listener;
	
	public void setOnTimeOverListener(OnTimeOverListener listener){
		this.listener = listener;
	}
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x123:
				textView.setText(number-- + string);
				break;

			case 0x124:
				if(listener != null){
					listener.onTimeOver();
				}
				break;
			}
		};
		
	};
	
	/**
	 * 
	 * @param tv ����TextView�ؼ�
	 * @param num ���뵹��ʱ��������
	 * @param str ����ʱ�����ֺ��������
	 */
	public Hourglass(TextView tv, int num, String str){
		textView = tv;
		number = num;
		string = str;
	}
	
	/**
	 * ִ�е���ʱ����
	 */
	public void time(){		
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(number != -1){
					handler.sendEmptyMessage(0x123);
				} else {
					timer.cancel();
					handler.sendEmptyMessage(0x124);
				}
			}
		}, 0, 1000);
		
	}
	
	public interface OnTimeOverListener{
		void onTimeOver();
	}
	
}
