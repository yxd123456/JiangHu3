package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.model.RechargeDetail;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/*
 * 
 * 充值明细
 * 
 */

public class LVRechargeDetailAdapter extends BaseAdapter {

	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<RechargeDetail> bList = new ArrayList<RechargeDetail>();
	private ListView listview;

	public LVRechargeDetailAdapter(Activity activity, List<RechargeDetail> list) {
		this.myActivity = activity;
		this.bList.addAll(bList);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<RechargeDetail> list) {
		this.bList.removeAll(bList);
		this.bList.addAll(list);
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
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final RechargeDetail rechargeDetail = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_rechare_detaillist, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_balance = (TextView) view.findViewById(R.id.tv_balance);// 余额
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money); // 充值金额
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date); // 时间
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_balance.setText("余额 : " + DataFormatUtil.floatsaveTwo(rechargeDetail.getBalance()));
		viewHolder.tv_money.setText(DataFormatUtil.floatsaveTwo(rechargeDetail.getMoney()));
		viewHolder.tv_date.setText(DateManage.StringToDateymd(rechargeDetail.getAddtime() + ""));
		return view;
	}

	public class ViewHolder {
		public TextView tv_balance, tv_money, tv_date;
	}

}
