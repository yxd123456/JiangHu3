package com.sptech.qujj.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonAdapter extends BaseAdapter {
	
	private Context context;
	private List datas;
	private LayoutInflater inflater;
	private OnGetViewHolderListener listener;
	private int layoutId;
	private ViewHolder holder;
	
	public CommonAdapter(Context context, List datas, int layoutId, OnGetViewHolderListener listener) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.datas = datas;
		inflater = LayoutInflater.from(context);
		this.listener = listener;
		this.layoutId = layoutId;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder = ViewHolder.get(context, convertView, parent, position, layoutId);
		listener.setConvertView(holder, position);
		return holder.getConvertView();
	}
	
	public interface OnGetViewHolderListener{
		void setConvertView(ViewHolder holder, int position);
	}
	
	

}
