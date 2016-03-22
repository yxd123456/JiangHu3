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
import com.sptech.qujj.model.BuyPronumber;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

public class LVBuyNumberAdapter extends BaseAdapter {

	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<BuyPronumber> bList = new ArrayList<BuyPronumber>();
	private ListView listview;

	public LVBuyNumberAdapter(Activity activity, List<BuyPronumber> bList) {
		this.myActivity = activity;
		this.bList.addAll(bList);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<BuyPronumber> list) {
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
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final BuyPronumber buyPronumber = bList.get(position);

		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_buynumberlist, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money);

			view.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		String userPhoneString = buyPronumber.getUser_phone();
		// hideMobile
		String mitelphone = DataFormatUtil.hideMobile(userPhoneString);

		viewHolder.tv_phone.setText(mitelphone);

		// SimpleDateFormat format = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//
		// Long time = new Long(buyPronumber.getAddtime());
		//
		// String d = format.format(time);
		// Date date;
		// try {
		// date = format.parse(d);
		// viewHolder.tv_date.setText(date + "");
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		String dateString = DateManage.StringToDatehms(buyPronumber.getAddtime() + "");
		viewHolder.tv_date.setText(dateString);

		// try {
		// SimpleDateFormat fm1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		// SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// String date = "";
		// Long time = new Long(buyPronumber.getAddtime()) * 1000;
		// date = fm1.format(time);
		// date = fm2.format(new Date(date));
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

		float money = buyPronumber.getNumber_has() * buyPronumber.getBuy_money();
		System.out.println("money ==" + money);
		viewHolder.tv_money.setText("¥" + DataFormatUtil.floatsaveTwo(money));

		return view;
	}

	public class ViewHolder {
		public TextView tv_phone, tv_date, tv_money;
	}

}
