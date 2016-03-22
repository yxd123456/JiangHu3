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
import com.sptech.qujj.model.BalanceDetail;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/*
 * 
 * 账户明细
 * 
 */

public class LVAccountDetailAdapter extends BaseAdapter {

	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<BalanceDetail> bList = new ArrayList<BalanceDetail>();
	private ListView listview;

	public LVAccountDetailAdapter(Activity activity, List<BalanceDetail> list) {
		this.myActivity = activity;
		this.bList.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<BalanceDetail> list) {
		this.bList.clear();
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
		final BalanceDetail balanceDetail = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_account_detaillist, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_type = (TextView) view.findViewById(R.id.tv_type);
			viewHolder.tv_balance = (TextView) view.findViewById(R.id.tv_balance);
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money);
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		if (balanceDetail.getDescription() != null) {
			viewHolder.tv_type.setText(balanceDetail.getDescription() + "");
		}

		// if (myBillBean.getIs_type() == 1) {
		// viewHolder.tv_type.setText("充值");
		// } else if (myBillBean.getIs_type() == 2) {
		// viewHolder.tv_type.setText("提现");
		// } else if (myBillBean.getIs_type() == 3) {
		// viewHolder.tv_type.setText("投资");
		// } else if (myBillBean.getIs_type() == 4) {
		// viewHolder.tv_type.setText("收益");
		// } else if (myBillBean.getIs_type() == 5) {
		// viewHolder.tv_type.setText("借款");
		// } else if (myBillBean.getIs_type() == 6) {
		// viewHolder.tv_type.setText("还款");
		// }

		if (balanceDetail.getIn_out() == 1) {
			viewHolder.tv_money.setText("+" + DataFormatUtil.floatsaveTwo(balanceDetail.getMoney()));
		} else if (balanceDetail.getIn_out() == 2) {
			viewHolder.tv_money.setText("-" + DataFormatUtil.floatsaveTwo(balanceDetail.getMoney()));
		}
		viewHolder.tv_balance.setText("余额 : " + DataFormatUtil.floatsaveTwo(balanceDetail.getBalance()));
		viewHolder.tv_date.setText(DateManage.StringToDateymd("" + balanceDetail.getAddtime()));
		return view;
	}

	public class ViewHolder {
		public TextView tv_type, tv_balance, tv_money, tv_date;
	}

}
