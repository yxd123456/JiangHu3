package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.AddMaildoingActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogNetwork;
import com.sptech.qujj.model.EmailBean;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.EventHandleListener;

/**
 * 邮箱 列表 Adapter
 * 
 * @author gusonglei
 * 
 */
public class LVMailListAdapter extends BaseAdapter {

	private String saleflag;
	// private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<EmailBean> bList = new ArrayList<EmailBean>();
	private ListView listview;
	private Context mConstant;
	private SharedPreferences spf;

	private boolean check = false;

	public LVMailListAdapter(Context constant, Boolean check, List<EmailBean> list) {
		this.mConstant = constant;
		this.bList.addAll(list);
		this.check = check;
		layoutInflater = LayoutInflater.from(mConstant);
		spf = mConstant.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
	}

	public void reset(List<EmailBean> list) {
		this.bList.removeAll(bList);
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
		final EmailBean eb = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_mail_list, null);
			viewHolder = new ViewHolder();
			// viewHolder.tv_right = find
			viewHolder.tv_mainname = (TextView) view.findViewById(R.id.tv_mainname);
			viewHolder.tv_importtime = (TextView) view.findViewById(R.id.tv_importtime);
			viewHolder.tv_mainname.setText(eb.getAccount() + "");
			// viewHolder.tv_importtime.setText("上次导入：" +
			// DateManage.StringToDate(eb.getAddtime()) + "");
			if (spf.getString(eb.getAccount(), "").equals("")) {
				viewHolder.tv_importtime.setText("");
				viewHolder.tv_importtime.setVisibility(View.GONE);
			} else {
				viewHolder.tv_importtime.setText("上次导入：" + DateManage.getNowDate() + "");
				viewHolder.tv_importtime.setVisibility(View.VISIBLE);
			}
			// viewHolder.tv_importtime.setText("上次导入：" +
			// DateManage.getNowDate() + "");
			viewHolder.tv_right = (TextView) view.findViewById(R.id.tv_right);
			viewHolder.tv_right.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 判断当前网络
					String netType = "";
					netType = HttpUtil.getNetWorkType(mConstant);
					System.out.println("当前网络 == " + netType);
					if (check && !"wifi".equals(netType)) { // 弹出提醒
						DialogNetwork dr = new DialogNetwork(mConstant, 0, new EventHandleListener() {
							@Override
							public void eventRifhtHandlerListener() {
								// spf.edit().putBoolean(Constant.IS_NETWORK,
								// false).commit();
								Intent intent = new Intent(mConstant, AddMaildoingActivity.class);
								intent.putExtra("emailname", eb.getAccount());
								intent.putExtra("mailId", eb.getId());
								mConstant.startActivity(intent);
								((Activity) mConstant).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
								spf.edit().putString(eb.getAccount(), DateManage.getNowDate()).commit();
							}

							@Override
							public void eventLeftHandlerListener() {
							}
						});
						dr.createMyDialog();
					} else {
						Intent intent = new Intent(mConstant, AddMaildoingActivity.class);
						intent.putExtra("emailname", eb.getAccount());
						intent.putExtra("mailId", eb.getId());
						mConstant.startActivity(intent);
						((Activity) mConstant).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						spf.edit().putString(eb.getAccount(), DateManage.getNowDate()).commit();
					}

				}
			});
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		return view;
	}

	public class ViewHolder {

		public RelativeLayout rela_top;
		public TextView tv_right, tv_mainname, tv_importtime;
		// public ImageView image, img_order, imagetejia;
		// public TextView text_ytname, tv_city, tv_fen, tv_distance, tv_ytfee;
	}

}