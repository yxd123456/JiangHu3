package com.sptech.qujj.view;

import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class DragViewGroup extends ScrollView{
	
	private ViewDragHelper helper;
	private ViewGroup mainView;
	private boolean flag = false;
	private int height;
	
	private Callback callback = new Callback() {
		
		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// TODO Auto-generated method stub
			return mainView == arg0;
		}
		
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			return 0;
		};
		
		public int clampViewPositionVertical(View child, int top, int dy) {
			/*if(top <= -(mainView.getChildAt(0).getHeight()+mainView.getChildAt(1).getHeight())){
				return -(mainView.getChildAt(0).getHeight()+mainView.getChildAt(1).getHeight());//这里不能返回0， 否则会发生抽搐
			}*/
			
			if(top > 0){
				return 0;//禁止下拉
			} else if (top <= -(mainView.getChildAt(0).getHeight()+mainView.getChildAt(1).getHeight())){
				//return -(mainView.getChildAt(0).getHeight()+mainView.getChildAt(1).getHeight());
				return top;
			}
			return top;
		};
		
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			ViewGroup vg = (ViewGroup) getParent();
			if(!flag){
				helper.smoothSlideViewTo(mainView, 0, -(mainView.getChildAt(0).getHeight()+mainView.getChildAt(1).getHeight()));
				//helper.smoothSlideViewTo(mainView, 0, mainView.getChildAt(0).getHeight());
				ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
				flag = true;
			} else {
				helper.smoothSlideViewTo(mainView, ((ViewGroup)getParent()).getLeft(), ((ViewGroup)getParent()).getTop());
				ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
				flag = false;
			}
			
		};
		
	};
	
	protected void onFinishInflate() {
		mainView = (ViewGroup) getChildAt(0);		
		
	};
	
	public DragViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		helper = ViewDragHelper.create(this, callback );
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return helper.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		 helper.processTouchEvent(event);
		 return true;
	}
	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if(helper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

}
