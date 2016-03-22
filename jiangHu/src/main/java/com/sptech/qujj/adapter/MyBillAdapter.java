package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.BillDetailActivity;
import com.sptech.qujj.model.MyBillBean;
import com.sptech.qujj.util.DateManage;

/*
 * 
 * 我的账单
 * 
 */

public class MyBillAdapter extends BaseAdapter {

	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<MyBillBean> bList = new ArrayList<MyBillBean>();
	private ListView listview;

	public MyBillAdapter(Activity activity, List<MyBillBean> list) {
		this.myActivity = activity;
		this.bList.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<MyBillBean> list) {
		// this.bList.clear();
		this.bList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder;
		if (convertView == null) {
			View view = layoutInflater.inflate(R.layout.item_mybill_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money);
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			viewHolder.tv_status = (TextView) view.findViewById(R.id.tv_status);
			view.setTag(viewHolder);
			convertView = view;
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final MyBillBean mb = bList.get(position);
		viewHolder.tv_name.setText(mb.getDescription() + "");
		viewHolder.tv_money.setText(mb.getMoney() + "");
		// viewHolder.tv_date.setText(DateManage.StringToDate(String.valueOf(mb.getAddtime()))
		// + "");
		String date = "";
		date = DateManage.StringToDateymd(String.valueOf(mb.getAddtime()));
		date = date.replaceAll("-", "/");
		viewHolder.tv_date.setText(date + "");
		if (mb.getIs_status() == 0) {
			viewHolder.tv_status.setText("交易中");
		} else if (mb.getIs_status() == 1) {
			viewHolder.tv_status.setText("交易成功");
		} else {
			viewHolder.tv_status.setText("交易失败");
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ToastManage.showToast(mb.getBills_id()+"");
				Intent billdetail = new Intent(myActivity, BillDetailActivity.class);
				billdetail.putExtra("targetid", mb.getTarget_id());
				billdetail.putExtra("istype", mb.getIs_type());
				billdetail.putExtra("description", mb.getDescription());
				billdetail.putExtra("money", mb.getMoney());
				myActivity.startActivity(billdetail);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name, tv_date, tv_money, tv_status;
	}

}
