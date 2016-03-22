package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.ProgressDetailActivity;
import com.sptech.qujj.model.MyBillBean;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/**
 * 进度 列表 Adapter
 * 
 * @author yebr
 * 
 */
@SuppressLint("ResourceAsColor")
public class LVProgressListAdapter extends BaseAdapter {

	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<MyBillBean> bList = new ArrayList<MyBillBean>();
	private ListView listview;

	public LVProgressListAdapter(Activity activity, List<MyBillBean> list) {
		this.myActivity = activity;
		this.bList.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<MyBillBean> list) {
		this.bList.clear();
		this.bList.addAll(list);
	}

	public int getCount() {
		System.out.println(" list size == " + bList.size());
		return bList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final MyBillBean mb = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_progress_list, null);
			viewHolder = new ViewHolder();
			viewHolder.rela_top = (RelativeLayout) view.findViewById(R.id.rela_top);
			viewHolder.img_apply = (ImageView) view.findViewById(R.id.img_apply);
			viewHolder.img_examine = (ImageView) view.findViewById(R.id.img_examine);
			viewHolder.img_finish = (ImageView) view.findViewById(R.id.img_finish);

			viewHolder.tv_apply = (TextView) view.findViewById(R.id.tv_apply);
			viewHolder.tv_examine = (TextView) view.findViewById(R.id.tv_examine);
			viewHolder.tv_descript = (TextView) view.findViewById(R.id.tv_descript);
			viewHolder.tv_finish = (TextView) view.findViewById(R.id.tv_finish);
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money);
			viewHolder.tv_month = (TextView) view.findViewById(R.id.tv_month);
			viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		System.out.println("title ===  " + mb.getDescription());
		viewHolder.tv_descript.setText(mb.getDescription());
		viewHolder.tv_money.setText("¥" + DataFormatUtil.floatsaveTwo(mb.getMoney()));
		viewHolder.tv_month.setText(DateManage.StringToMM(String.valueOf(mb.getAddtime())) + "月");
		viewHolder.tv_date.setText(DateManage.StringToMMDD(String.valueOf(mb.getAddtime())) + "");
		int status = mb.getIs_status();
		// int status = 2;
		// if (status == 0 ) {// 提交申请
		// viewHolder.img_apply.setBackgroundResource(R.drawable.ic_progress_apply_select);
		// viewHolder.img_examine.setBackgroundResource(R.drawable.ic_progress_examine_unselect);
		// viewHolder.img_finish.setBackgroundResource(R.drawable.ic_progress_finish_unselect);
		//
		// viewHolder.tv_apply.setTextColor(myActivity.getResources().getColor(R.color.text_blue));
		// viewHolder.tv_examine.setTextColor(myActivity.getResources().getColor(R.color.text_main));
		// viewHolder.tv_finish.setTextColor(myActivity.getResources().getColor(R.color.text_main));
		// // viewHolder.tv_examine.setTextColor(R.color.text_main);
		// // viewHolder.tv_finish.setTextColor(R.color.text_main);
		// viewHolder.tv_examine.setTextColor(myActivity.getResources().getColor(R.color.text_main));
		// viewHolder.tv_examine.setText("审核中");
		// System.out.println("提交申请中---== ");
		//
		// } else
		if (status == 0 || status == 1) {// 审核中
			viewHolder.img_apply.setBackgroundResource(R.drawable.ic_progress_apply_noselect);
			viewHolder.img_examine.setBackgroundResource(R.drawable.ic_progress_examine_select);
			viewHolder.img_finish.setBackgroundResource(R.drawable.ic_progress_finish_unselect);
			viewHolder.tv_apply.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			viewHolder.tv_finish.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			viewHolder.tv_examine.setTextColor(myActivity.getResources().getColor(R.color.text_blue));
			viewHolder.tv_examine.setText("审核中");
			System.out.println("审核中---== ");
		} else if (status == 2) {// 审核失败
			viewHolder.img_apply.setBackgroundResource(R.drawable.ic_progress_apply_noselect);
			viewHolder.img_examine.setBackgroundResource(R.drawable.ic_progress_examine_nopast_select);
			viewHolder.img_finish.setBackgroundResource(R.drawable.ic_progress_finish_unselect);
			// viewHolder.tv_finish.setTextColor(R.color.text_main);
			viewHolder.tv_examine.setTextColor(myActivity.getResources().getColor(R.color.text_red));
			viewHolder.tv_examine.setText("审核失败");
			viewHolder.tv_apply.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			viewHolder.tv_finish.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			System.out.println("审核失败---== ");
		} else if (status == 4) {// 已打款
			viewHolder.img_apply.setBackgroundResource(R.drawable.ic_progress_apply_noselect);
			viewHolder.img_examine.setBackgroundResource(R.drawable.ic_progress_examine_noselect);
			viewHolder.img_finish.setBackgroundResource(R.drawable.ic_progress_finish_select);
			viewHolder.tv_apply.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			viewHolder.tv_examine.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			viewHolder.tv_finish.setTextColor(myActivity.getResources().getColor(R.color.text_blue));
			System.out.println(" 已打款---== ");
			viewHolder.tv_examine.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			viewHolder.tv_examine.setText("审核通过");
		} else if (status == 3) {// 审核通过
			viewHolder.img_apply.setBackgroundResource(R.drawable.ic_progress_apply_noselect);
			viewHolder.img_examine.setBackgroundResource(R.drawable.ic_progress_examine_select);
			viewHolder.img_finish.setBackgroundResource(R.drawable.ic_progress_finish_unselect);
			// viewHolder.tv_finish.setTextColor(R.color.text_main);
			viewHolder.tv_examine.setTextColor(myActivity.getResources().getColor(R.color.text_blue));
			viewHolder.tv_examine.setText("审核通过");
			viewHolder.tv_apply.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			viewHolder.tv_finish.setTextColor(myActivity.getResources().getColor(R.color.text_main));
			System.out.println("审核失败---== ");
		}
		viewHolder.rela_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent2 = new Intent(myActivity, ProgressDetailActivity.class);
				intent2.putExtra("target_id", mb.getTarget_id());
				// intent2.putExtra("status", mb.getIs_status());
				intent2.putExtra("descript", DateManage.StringToMM(String.valueOf(mb.getAddtime())) + "月" + mb.getDescription());
				myActivity.startActivity(intent2);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				// myActivity.overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
			}
		});
		return view;
	}

	public class ViewHolder {

		public RelativeLayout rela_top;
		public ImageView img_apply, img_examine, img_finish;
		public TextView tv_money, tv_month, tv_date, tv_descript, tv_apply, tv_examine, tv_finish;
		// public ImageView image, img_order, imagetejia;
		// public TextView text_ytname, tv_city, tv_fen, tv_distance, tv_ytfee;
	}

}