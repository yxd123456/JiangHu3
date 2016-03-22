package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.model.BorrowMoneyBean;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.model.MonthBean;
import com.sptech.qujj.util.DateManage;
/**
 *   借你钱页面Adapter
 * @author Administrator
 *
 */
public class ReimbursementAdapter  extends BaseAdapter{
	List<MonthBean> borrow_list;
	LayoutInflater inflater;
	public ReimbursementAdapter(Context context) {
		// TODO Auto-generated constructor stub
		borrow_list = new ArrayList<>();
		inflater = LayoutInflater.from(context);
	}
	
	public void reset(List<MonthBean> list) {
		if(list==null||list.equals("")){
			list = new ArrayList<>();
		}
		this.borrow_list = list;
		this.borrow_list.clear();
		this.borrow_list.addAll(list);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return borrow_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(v == null) {
			holder = new ViewHolder();
			v = inflater.inflate(R.layout.akey_reimbursement_item, null);
			holder.btn_select_one = (CheckBox) v.findViewById(R.id.btn_select_one);
			holder.lym_time = (TextView) v.findViewById(R.id.lym_time);
			holder.tv_borrow_sucess = (TextView) v.findViewById(R.id.tv_borrow_sucess);
			holder.tv_borrow_time = (TextView) v.findViewById(R.id.tv_borrow_time);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.lym_time.setText(DateManage.getAddDate("到期时间"+borrow_list.get(position).getTakeeffect_time()+"", position-1));
		holder.tv_borrow_time.setText(borrow_list.get(position).getTakeeffect_time());
		holder.tv_borrow_sucess.setText(borrow_list.get(position).getHelp_money()+"");
		return v;
	}

	class ViewHolder{
		CheckBox btn_select_one;
		TextView tv_borrow_time,lym_time,tv_borrow_sucess;
	}
	
}
