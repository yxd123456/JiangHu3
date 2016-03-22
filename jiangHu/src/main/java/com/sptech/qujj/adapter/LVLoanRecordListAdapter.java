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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.ProgressDetailActivity;
import com.sptech.qujj.model.LoanRecord;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/**
 * 借款记录 列表 Adapter
 * 
 * @author yebr
 * 
 */
public class LVLoanRecordListAdapter extends BaseAdapter {

	private String saleflag;
	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<LoanRecord> loanRecords = new ArrayList<LoanRecord>();
	private ListView listview;

	public LVLoanRecordListAdapter(Activity activity, List<LoanRecord> list) {
		this.myActivity = activity;
		this.loanRecords.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<LoanRecord> list) {
		this.loanRecords.clear();
		this.loanRecords.addAll(list);
	}

	public int getCount() {
		return loanRecords.size();
		// return 5;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		final LoanRecord loanRecord = loanRecords.get(position);
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_loanrecord_list, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_month = (TextView) view.findViewById(R.id.tv_month);
			viewHolder.tv_money = (TextView) view.findViewById(R.id.tv_money);
			viewHolder.tv_zhangdate = (TextView) view.findViewById(R.id.tv_zhangdate);
			// viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);

			viewHolder.img_status = (ImageView) view.findViewById(R.id.img_status);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		// viewHolder.tv_month.setText(DateManage.StringToMM(String.valueOf(loanRecord.getBill_month()))
		// + "月");
		// viewHolder.tv_date.setText(DateManage.StringToMMDD(String.valueOf(loanRecord.getBill_month()))
		// + "");

		int addtime = loanRecord.getAddtime();
		if (addtime == 0) {
			viewHolder.tv_month.setText("未知");
		} else {
			viewHolder.tv_month.setText(DateManage.StringToMMDD(String.valueOf(addtime)) + "");
		}

		viewHolder.tv_money.setText("¥" + DataFormatUtil.floatsaveTwo(loanRecord.getHelp_money()));
		int bills = loanRecord.getBill_cycle_set();
		int bille = loanRecord.getBill_cycle_end();
		if (bills == 0 || bille == 0) {
			viewHolder.tv_zhangdate.setText("账单周期: 未知");
		} else {
			String billset = DateManage.StringToMMDD(String.valueOf(bills));
			String billend = DateManage.StringToMMDD(String.valueOf(bille));
			String newbillSet = billset.replace("-", "/");
			String newbillEnd = billend.replace("-", "/");
			String zhangdanString = newbillSet + " - " + newbillEnd;
			viewHolder.tv_zhangdate.setText("账单周期: " + zhangdanString);
		}

		int status = loanRecord.getIs_status();
		// if (status == 0 || status == 1 || status == 3) { // 1/3 审核中
		// viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_examine);
		// } else if (status == 2) {// 未通过
		// viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_nopart);
		// } else if (status == 5) { // 已放款
		// viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_dakuan);
		// } else if (status == 7) {// 已还款
		// viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_finish);
		// }

		if (status == 0 || status == 1) { // 1/3 审核中
			viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_examine);
		} else if (status == 3) {// 审核通过
			viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_pass);
		} else if (status == 2) {// 未通过
			viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_nopart);
		} else if (status == 4) { // 已放款
			viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_dakuan);
		} else if (status == 5) {// 已还款
			viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_finish);
		}

		// else if (status == 6) {// 还款中
		// viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_seal_examine);
		// }else if (status == 8) { // 还款失败
		// //
		// viewHolder.img_status.setBackgroundResource(R.drawable.img_progress_);
		// }

		// DateManage.StringToMMDD(String.valueOf(mb.getAddtime())) ;

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Intent rela_progress = new Intent(myActivity,
				// ProgressActivity.class);
				// myActivity.startActivity(rela_progress);
				// myActivity.overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);

				Intent intent2 = new Intent(myActivity, ProgressDetailActivity.class);
				intent2.putExtra("target_id", loanRecord.getId());
				// intent2.putExtra("status", loanRecord.getIs_status());
				intent2.putExtra("descript", DateManage.StringToMM(String.valueOf(loanRecord.getAddtime())) + "月代还款申请");

				System.out.println("target_id==" + loanRecord.getId());
				System.out.println("status==" + loanRecord.getIs_status());
				System.out.println("descript==" + DateManage.StringToMM(String.valueOf(loanRecord.getAddtime())) + "月代还款申请");
				myActivity.startActivity(intent2);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		return view;
	}

	public class ViewHolder {
		public ImageView img_status;
		public TextView tv_month, tv_money, tv_zhangdate, tv_date; // 月份，金额，账单期，日期
	}

}