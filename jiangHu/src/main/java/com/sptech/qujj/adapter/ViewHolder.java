package com.sptech.qujj.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	
	private SparseArray<View> mViews;
	private int mPosition;
	private View mconvertView;
	
	public ViewHolder(Context context, ViewGroup parent, int layoutId, int position){
		mPosition = position;
		mViews = new SparseArray<View>();
		mconvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		mconvertView.setTag(this);
	}
	
	public static ViewHolder get(Context context, View convertView, ViewGroup parent, int position, int layoutId){
		if(convertView == null){
			return new ViewHolder(context, parent, layoutId, position);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}
	
	/**
	 * ����viewId��ȡ�ؼ�
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId){
		View view = mViews.get(viewId);
		if(view == null){
			view = mconvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}
	
	public View getConvertView(){
		return mconvertView;
	}
	
	public void setText(int viewId, String content){
		((TextView)this.getView(viewId)).setText(content);
	}
	
	public void setOnClick(int viewId, OnClickListener listener){
		((TextView)this.getView(viewId)).setOnClickListener(listener);
	}
	
	public void setOnCheck(int viewId, OnCheckedChangeListener listener){
		((CheckBox)this.getView(viewId)).setOnCheckedChangeListener(listener);
	}
	
	public void setImageResource(int viewId, int imgId){
		((ImageView)this.getView(viewId)).setImageResource(imgId);
	}
	
	
}
