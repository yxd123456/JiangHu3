package com.sptech.qujj.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 正方形的可加载网络图片的ImageView
 * 
 * @author 赵成龙
 * @since 2014-11-23
 * 
 */
public class CustomSquareImageN extends CustomNetworkImageView {

	public CustomSquareImageN(Context context) {
		this(context, null);
	}

	public CustomSquareImageN(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomSquareImageN(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		// 强制设置为正方形
		setMeasuredDimension(width, width);
	}

}
