package com.sptech.qujj;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.sptech.qujj.activity.LoginActivity;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.util.ActivityJumpManager;

public class GuideActivity extends BaseActivity implements OnClickListener {
	private ViewPager mViewPager;

	private ImageView imagePage0;
	private ImageView imagePage1;
	private ImageView imagePage2;
	private List<View> views;
	private Button goButton;// 立即体验
	private String from;
	private int currIndex = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_yt_leadview);
		initView();
		SPHelper.setGuideid(false);
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.ViewPager);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		from = getIntent().getStringExtra("from");
		imagePage0 = (ImageView) findViewById(R.id.page0);
		imagePage1 = (ImageView) findViewById(R.id.page1);
		imagePage2 = (ImageView) findViewById(R.id.page2);

		// 将要分页显示的View装入数组中
		LayoutInflater inflater = LayoutInflater.from(this);
		View view1 = inflater.inflate(R.layout.view1, null);
		View view2 = inflater.inflate(R.layout.view2, null);
		View view3 = inflater.inflate(R.layout.view3, null);

		goButton = (Button) view3.findViewById(R.id.btn_go);

		goButton.setOnClickListener(this);

		// 每个页面的view数据
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return views.size();
			}

			//
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}

		};
		mViewPager.setAdapter(mPagerAdapter);

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub

			switch (arg0) {
			case 0:
				imagePage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				imagePage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				imagePage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			case 1:
				imagePage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				imagePage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				imagePage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			case 2:
				imagePage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				imagePage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			default:
				break;

			}

			currIndex = arg0;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (from.equals("normal")) {
			ActivityJumpManager.jump(this, LoginActivity.class, 1, 0);
		} else {
			this.finish();
			ActivityJumpManager.finishActivity(this, 1);
		}

	}

}
