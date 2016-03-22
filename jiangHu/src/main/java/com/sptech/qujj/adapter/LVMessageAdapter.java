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
import com.sptech.qujj.activity.MessageDetailActivity;
import com.sptech.qujj.model.MessageBean;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.view.CircleProgressBar;

public class LVMessageAdapter extends BaseAdapter {

	private String saleflag;
	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<MessageBean> bList = new ArrayList<MessageBean>();
	private ListView listview;

	public LVMessageAdapter(Activity activity, List<MessageBean> list) {
		this.myActivity = activity;
		this.bList.clear();
		this.bList.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<MessageBean> list) {
		this.bList.removeAll(list);
		this.bList.addAll(list);
	}

	public int getCount() {
		return bList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final MessageBean mb = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_message_list, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
			viewHolder.tv_data = (TextView) view.findViewById(R.id.tv_data);
			viewHolder.message_num = (TextView) view.findViewById(R.id.message_num);
			if (mb.getRead_time() != null) {
				if (mb.getRead_time().equals("0")) {
					viewHolder.message_num.setVisibility(View.VISIBLE);
				} else {
					viewHolder.message_num.setVisibility(View.GONE);
				}
			} else {
				viewHolder.message_num.setVisibility(View.VISIBLE);
			}

			viewHolder.tv_data.setText(DateManage.StringToDateymd(String.valueOf(mb.getAddtime())));
			viewHolder.tv_title.setText(mb.getTitle());
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ToastManage.showToast(mb.getBills_id()+"");
				Intent message = new Intent(myActivity, MessageDetailActivity.class);
				message.putExtra("message_id", mb.getId());
				message.putExtra("readtime", mb.getRead_time());
				// billdetail.putExtra("istype", mb.getIs_type());
				// billdetail.putExtra("description", mb.getDescription());
				myActivity.startActivity(message);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		return view;
	}

	public class ViewHolder {

		public CircleProgressBar circleProgressBar;
		public TextView tv_title, tv_data, message_num;
		// public ImageView image, img_order, imagetejia;
		// public TextView text_ytname, tv_city, tv_fen, tv_distance, tv_ytfee;
	}

}