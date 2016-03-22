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
import com.sptech.qujj.model.LoanRecord;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/*
 * 
 * 还款明细
 * 
 */

public class LVMyRepamentDetailAdapter extends BaseAdapter {

	private Activity myActivity;
	private LayoutInflater layoutInflater;
	List<LoanRecord> curloanRecords = new ArrayList<LoanRecord>();
	private ListView listview;

	public LVMyRepamentDetailAdapter(Activity activity, List<LoanRecord> list) {
		this.myActivity = activity;
		this.curloanRecords.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<LoanRecord> list) {
		this.curloanRecords.clear();
		this.curloanRecords.addAll(list);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return curloanRecords.size();
		// return 1;
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
		final LoanRecord loanRecord = curloanRecords.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_rechare_detaillist, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_out = (TextView) view.findViewById(R.id.tv_out);// 信用卡还款(9527)
			viewHolder.tv_balance = (TextView) view.findViewById(R.id.tv_balance);// 还款时间2015-11-26
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money); // 还款金额-3000
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date); // 还款成功
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_out.setText("信用卡还款(" + DataFormatUtil.bankcardsaveFour(loanRecord.getCard_no()) + ")");
		if (loanRecord.getRepayment_time() != 0) {
			viewHolder.tv_balance.setText(DateManage.StringToDateymd(String.valueOf(loanRecord.getRepayment_time())));
		}
		System.out.println("getRepayment_money == " + loanRecord.getRepayment_money());
		viewHolder.tv_money.setText(DataFormatUtil.floatsaveTwo(loanRecord.getRepayment_money()));

		int status = loanRecord.getIs_status();
		if (status == 6) {
			viewHolder.tv_date.setText("还款中");
		} else if (status == 7) {
			viewHolder.tv_date.setText("还款成功");
		} else if (status == 8) {
			viewHolder.tv_date.setText("还款失败");
		}

		return view;
	}

	public class ViewHolder {
		public TextView tv_out, tv_balance, tv_money, tv_date;
	}

}
