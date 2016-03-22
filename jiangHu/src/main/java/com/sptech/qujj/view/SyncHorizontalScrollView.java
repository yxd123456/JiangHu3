package com.sptech.qujj.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

public class SyncHorizontalScrollView extends HorizontalScrollView {

	public SyncHorizontalScrollView(Context context) {
		super(context);
	}

	public SyncHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SyncHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	private View view;
	// private ImageView leftImage;// 左标图片
	// private ImageView rightImage;// 右标图片
	private int windowWitdh = 0;// 屏幕宽度
	private Activity mContext;

	public void setSomeParam(View view, Activity context, int widthPixels) {
		this.view = view;
		this.mContext = context;
		// this.leftImage = iv_nav_left;
		// this.rightImage = iv_nav_right;
		this.windowWitdh = widthPixels;
	}

	// 显示和隐藏左右两边的箭头
	public void showAndHideArrow() {
		// if (!mContext.isFinishing() && view != null) {
		// this.measure(0, 0);
		// if (windowWitdh >= this.getMeasuredWidth()) {
		// leftImage.setVisibility(View.GONE);
		// rightImage.setVisibility(View.GONE);
		// } else {
		// if (this.getLeft() == 0) {
		// leftImage.setVisibility(View.GONE);
		// rightImage.setVisibility(View.VISIBLE);
		// } else if (this.getRight() == this.getMeasuredWidth()
		// - windowWitdh) {
		// leftImage.setVisibility(View.VISIBLE);
		// rightImage.setVisibility(View.GONE);
		// } else {
		// leftImage.setVisibility(View.VISIBLE);
		// rightImage.setVisibility(View.VISIBLE);
		// }
		// }
		// }
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		// if (!mContext.isFinishing() && view != null && rightImage != null
		// && leftImage != null) {
		// if (view.getWidth() <= windowWitdh) {
		// leftImage.setVisibility(View.GONE);
		// rightImage.setVisibility(View.GONE);
		// } else {
		// if (l == 0) {
		// leftImage.setVisibility(View.GONE);
		// rightImage.setVisibility(View.VISIBLE);
		// } else if (view.getWidth() - l == windowWitdh) {
		// leftImage.setVisibility(View.VISIBLE);
		// rightImage.setVisibility(View.GONE);
		// } else {
		// leftImage.setVisibility(View.VISIBLE);
		// rightImage.setVisibility(View.VISIBLE);
		// }
		// }
		// }
	}

}
