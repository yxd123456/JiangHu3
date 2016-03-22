package com.sptech.qujj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;
/**
 *  申请成功页面
 * @author LiLin
 *
 */
public class ApplyLoadSucessActivity extends BaseActivity implements
		OnClickTitleListener, OnClickListener {

	TitleBar titleBar;
	Button btn_check_schedule;
	TextView tv_gohome, tv_sucess_detail,tv_time;
	private MyCount mc;
	String cardDetail;
	float money;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applyload_sucess);
		Tools.addActivityList(this);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btn_check_schedule = (Button) findViewById(R.id.btn_check_schedule);//查看进度
		tv_gohome = (TextView) findViewById(R.id.tv_gohome);//回到首页
		tv_sucess_detail = (TextView) findViewById(R.id.tv_sucess_detail);//信息
		tv_time = (TextView) findViewById(R.id.tv_time);
		mc = new MyCount(5000, 1000);
		 mc.start(); 
	}

	private void initListener() {
		// TODO Auto-generated method stub
		titleBar.bindTitleContent("申请成功提示", R.drawable.jh_back_selector, 0, "",
				"");
		titleBar.setOnClickTitleListener(this);
		btn_check_schedule.setOnClickListener(this);
		tv_gohome.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		cardDetail = getIntent().getStringExtra("cardDetail");
		money = getIntent().getFloatExtra("money", 0);
		tv_sucess_detail.setText("您的"+cardDetail+"申请"+DataFormatUtil.floatsaveTwo(money)+"元,"+"审核将在2-3个工作日完成,请随时查看进度中心审核进度");
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		mc.cancel();
		ApplyLoadSucessActivity.this.finish();
		ActivityJumpManager.finishActivity(this, 1);
		
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_check_schedule:
            Intent schedule = new Intent(ApplyLoadSucessActivity.this,ProgressActivity.class);
            mc.cancel();
            ApplyLoadSucessActivity.this.finish();
            startActivity(schedule);
			break;
		case R.id.tv_gohome:
			Intent intent = new Intent(ApplyLoadSucessActivity.this,MainActivity.class);
			mc.cancel();
        	startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	/*定义一个倒计时的内部类*/  
    class MyCount extends CountDownTimer {     
        public MyCount(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);     
        }     
        @Override     
        public void onFinish() {     
            //tv.setText("finish"); 
        	Intent intent = new Intent(ApplyLoadSucessActivity.this,MainActivity.class);
        	startActivity(intent);
        }     
        @Override     
        public void onTick(long millisUntilFinished) {     
            tv_time.setText(millisUntilFinished / 1000 + "之后，页面将自动");     
            //Toast.makeText(ApplyLoadSucessActivity.this, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延迟       
        } 
}}
