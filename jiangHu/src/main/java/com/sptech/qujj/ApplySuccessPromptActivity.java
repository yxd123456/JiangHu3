package com.sptech.qujj;

import com.sptech.qujj.Hourglass.OnTimeOverListener;
import com.sptech.qujj.activity.ProgressActivity;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.view.TitleBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 申请成功提示
 * @author 叶旭东
 *
 */
public class ApplySuccessPromptActivity extends BaseActivity {
	
	private TextView tv_back;//回到首页
	private TextView tv_time;//倒计时
	private TitleBar titleBar;
	private Hourglass hourglass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sqcgts);
		
		init();
		
		initListener();
		
		hourglass.time();
			
	}
	
	private void initListener() {
		// TODO Auto-generated method stub
		hourglass.setOnTimeOverListener(new OnTimeOverListener() {
			
			@Override
			public void onTimeOver() {
				// TODO Auto-generated method stub
				startActivity(new Intent(ApplySuccessPromptActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		tv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ApplySuccessPromptActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		tv_back = (TextView) findViewById(R.id.back_to_first);
		tv_back.setText(Html.fromHtml("<u>"+"回到首页"+"</u>"));
		
		tv_time = (TextView) findViewById(R.id.daojishi);
		
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("申请成功提示", 0, 0, "", "");
		
		hourglass = new Hourglass(tv_time, 10, "秒之后，页面将自动");
	}

	public void check(View v){
		startActivity(new Intent(ApplySuccessPromptActivity.this, ProgressActivity.class));
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
