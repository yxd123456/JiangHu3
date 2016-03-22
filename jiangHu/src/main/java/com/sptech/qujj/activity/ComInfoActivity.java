package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.List;

import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class ComInfoActivity extends FragmentActivity implements OnClickTitleListener{
	
	private ViewPager mViewPager;
	private MyFragmentAdapter mAdapter;
	private List<ImageView> imgs = new ArrayList<ImageView>();
	private ImageView iv1, iv2/*, iv3, iv4, iv5*/;
	private TitleBar mTitleBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_com_info);
		
		iv1 = (ImageView) findViewById(R.id.indicator1);
		iv2 = (ImageView) findViewById(R.id.indicator2);
		/*iv3 = (ImageView) findViewById(R.id.indicator3);
		iv4 = (ImageView) findViewById(R.id.indicator4);
		iv5 = (ImageView) findViewById(R.id.indicator5);*/
		imgs.add(iv1);
		imgs.add(iv2);
		/*imgs.add(iv3);
		imgs.add(iv4);
		imgs.add(iv5);	*/
		
		mTitleBar = (TitleBar) findViewById(R.id.infoTitleBar);
		mTitleBar.bindTitleContent("完善信息", 0, 0, "取消", "");
		mTitleBar.setOnClickTitleListener(this);
		
		mAdapter = new MyFragmentAdapter(imgs, getSupportFragmentManager());
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager_com_info);
		
		if(mAdapter != null)
			mViewPager.setAdapter(mAdapter);	
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < imgs.size(); i++) {	
					imgs.get(i).setImageResource(R.drawable.indicator_pointer_grey);
				}
				switch (arg0) {
				case 0:
					mTitleBar.bindTitleContent("完善信息", 0, 0, "取消", "");
					imgs.get(0).setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 1:
					mTitleBar.bindTitleContent("完善信息", 0, 0, "取消", "回到首页");
					imgs.get(1).setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 2:
					mTitleBar.bindTitleContent("完善信息", 0, 0, "取消", "下一步");
					imgs.get(2).setImageResource(R.drawable.indicator_pointer_fg);
					break;
				/*case 3:
					mTitleBar.bindTitleContent("完善信息", 0, 0, "取消", "下一步");
					imgs.get(3).setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 4:
					mTitleBar.bindTitleContent("完善信息", 0, 0, "取消", "下一步");
					imgs.get(4).setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 5:
					mTitleBar.bindTitleContent("完善信息", 0, 0, "取消", "下一步");
					imgs.get(5).setImageResource(R.drawable.indicator_pointer_fg);
					break;*/
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		if(mViewPager.getCurrentItem() != 0){
			mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
		} else if(mViewPager.getCurrentItem() == 0){
			ActivityJumpManager.finishActivity(ComInfoActivity.this, 1);
		}
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		if(((Button)view).getText().toString().equals("回到首页")){
			startActivity(new Intent(this, MainActivity.class));
		}
		if(mViewPager.getCurrentItem() != 1){
			mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
		}
	}
	
	
	
}
