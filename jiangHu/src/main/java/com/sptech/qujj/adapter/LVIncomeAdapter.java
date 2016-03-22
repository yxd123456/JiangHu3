package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.model.Income;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/**
 * 理财收益 list
 * 
 * @author yebr
 * 
 */

public class LVIncomeAdapter extends BaseAdapter {

	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<Income> incomes = new ArrayList<Income>();
	private String month = "";

	public LVIncomeAdapter(Activity activity, List<Income> bList) {
		this.myActivity = activity;
		month = "";
		this.incomes.clear();
		this.incomes = bList;
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<Income> incomes) {
		month = "";
		// this.incomes.clear();
		this.incomes = incomes;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		System.out.println("size --== " + incomes.size());
		return incomes.size();

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
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Income income = incomes.get(position);
		String curMonth = DateManage.getUXMonth(income.getAddtime() + "") + "月";
		System.out.println("当前月份 == " + curMonth);
		ViewHolder viewHolder;
		if (view == null) {
			System.out.println("view == null---position= " + position);
			view = layoutInflater.inflate(R.layout.item_incomelist, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_proname = (TextView) view.findViewById(R.id.tv_proname);
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money);
			viewHolder.tv_month = (TextView) view.findViewById(R.id.tv_month);
			viewHolder.rela_month = (RelativeLayout) view.findViewById(R.id.rela_month);
			view.setTag(viewHolder);
		} else {
			System.out.println("view !=null---position= " + position);
			viewHolder = (ViewHolder) view.getTag();
		}

		// 判断 是否与上次显示的月份相同，是则不显示，否则显示月份；
		if (curMonth.equals(month)) {
			viewHolder.rela_month.setVisibility(View.GONE);
			viewHolder.rela_month.setTag(View.GONE);
		} else {
			month = curMonth;
			viewHolder.tv_month.setText(month);
			viewHolder.rela_month.setVisibility(View.VISIBLE);
			viewHolder.rela_month.setTag(View.VISIBLE);
		}

		// if (viewHolder.rela_month.getTag() != null) {
		// viewHolder.rela_month.getTag();
		// }

		if (income.getSubject() != null) {
			viewHolder.tv_proname.setText(income.getSubject());
		}

		if (income.getAddtime() != 0) {
			System.out.println("时间== " + DateManage.StringToDateymd(income.getAddtime() + ""));
			viewHolder.tv_date.setText(DateManage.StringToDateymd(income.getAddtime() + ""));
			System.out.println("end month !! ==== " + month);
		}
		// if (income.getProfit_actual() != 0) {
		viewHolder.tv_money.setText("+" + DataFormatUtil.floatsaveTwo(income.getProfit_actual()));

		return view;
	}

	public class ViewHolder {
		public TextView tv_proname, tv_date, tv_money, tv_month;
		public RelativeLayout rela_month;
	}

}
