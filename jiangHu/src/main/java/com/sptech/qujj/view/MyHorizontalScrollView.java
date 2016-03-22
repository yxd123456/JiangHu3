package com.sptech.qujj.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sptech.qujj.R;
import com.sptech.qujj.util.ScreenUtils;

public class MyHorizontalScrollView extends HorizontalScrollView {

	// 频道
	public static String[] CHANELS = { "全部", "基金", "保险", "信托", "银行理财" };

	private Context context;

	private RadioGroup rg;

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		// 初始化标题栏
		initColumns();
	}

	private void initColumns() {
		rg = new RadioGroup(context);
		// 宽高
		rg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		// 水平方向
		rg.setOrientation(RadioGroup.HORIZONTAL);

		// 去掉边缘阴影
		rg.setFadingEdgeLength(0);
		int[] metrics = ScreenUtils.getScreenWH(context);
		int screenWidth = metrics[0];

		// 宽度等于屏幕宽度的1/5
		int rbWidth = screenWidth / 5;
		int rbHeight = 0;
		RadioButton rb = null;
		for (String str : CHANELS) {

			rb = new RadioButton(context);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(rbWidth,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			rb.setLayoutParams(params);

			// rb.setBackgroundResource(R.drawable.licai_selector_tuijian);
			// rb.setTextColor(getResources().getColor(
			// R.drawable.licai_selector_tuijian));

			// if (str == "全部") {
			// rb.setTextColor(getResources().getColor(
			// R.color.background_color));
			// }
			// 设置样式
			rb.setButtonDrawable(new BitmapDrawable());
			// 去掉默认填充
			rb.setPadding(0, 0, 0, 0);
			// 文字居中
			rb.setGravity(Gravity.CENTER);
			// 文字颜色
			rb.setTextColor(getResources().getColor(R.color.text_assist));
			// 文字大小
			rb.setTextSize(20);
			// 文字
			rb.setText(str);
			// 添加到RadioGroup
			rg.addView(rb);

			rb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((RadioButton) v).setTextColor(getResources().getColor(
							R.color.main_color));
				}
			});

			// 记录下高度
			Log.d("tz", rb.getWidth() + "," + rb.getHeight());
		}
		this.addView(rg);

	}

	public RadioGroup getRadioGroup() {
		return rg;
	}

	public void setRadioButtonColor(RadioButton rb) {

		rb.setTextColor(getResources().getColor(R.color.background_color));

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
