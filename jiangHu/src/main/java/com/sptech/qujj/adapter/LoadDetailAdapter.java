package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.model.BMListBean;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;
/**
 *  借款详情Adapter
 * @author 	LiLin
 *
 */
public class LoadDetailAdapter extends BaseAdapter {

	LayoutInflater inflater;
	List<BMListBean> list = new ArrayList<BMListBean>();

	public LoadDetailAdapter(Activity context,List<BMListBean> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
		Log.e("shuangpeng", list+"list:  "+this.list+":this");
		inflater = LayoutInflater.from(context);
	}
	
	public void reset(List<BMListBean> list1) {
		//this.list.removeAll(list);
		this.list.clear();
		this.list.addAll(list1);
		Log.e("shuangpeng", list1+"list:  "+this.list+":this:");
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder  ;
		if (v == null) {
			holder = new ViewHolder();
			v = inflater.inflate(R.layout.lv_this_month_load_item, null);
			holder.tv_detail_loadmoney = (TextView) v.findViewById(R.id.tv_detail_loadmoney);
			holder.tv_lengmoneytoyou = (TextView) v.findViewById(R.id.tv_lengmoneytoyou);
			holder.tv_sendmoney_time = (TextView) v.findViewById(R.id.tv_sendmoney_time);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.tv_detail_loadmoney .setText(DataFormatUtil.floatsaveTwo(list.get(position).getHelp_money())+"");//借款金额
		holder.tv_lengmoneytoyou.setText(list.get(position).getTitle());//申请标题
		
		String time = DateManage.StringToDate(list.get(position).getTakeeffect_time()+"");
		holder.tv_sendmoney_time.setText(time);//打款时间
		return v;
	}

	static class ViewHolder {
		TextView tv_lengmoneytoyou, tv_sendmoney_time, tv_detail_loadmoney;
	}

}
