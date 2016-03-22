package com.sptech.qujj.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.InvestActivity;
import com.sptech.qujj.activity.RedeemActivity;
import com.sptech.qujj.activity.TransactionDetailsActivity;
import com.sptech.qujj.model.Product;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/*
 * 
 * 理财资产
 * 
 */

public class LVFinancialAssetsAdapter extends BaseAdapter {
	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<Product> curfinancialAssets = new ArrayList<Product>();
	private ListView listview;
	private int dateType;

	// 用于记录每个RadioButton的状态，并保证只可选一个
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();

	public LVFinancialAssetsAdapter(Activity activity, List<Product> list) {
		this.myActivity = activity;
		this.curfinancialAssets.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<Product> list) {
		this.curfinancialAssets.clear();
		this.curfinancialAssets.addAll(list);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return curfinancialAssets.size();
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
		final Product financialAssets = curfinancialAssets.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_financiallist, null);
			viewHolder = new ViewHolder();
			viewHolder.rela_proinfo = (RelativeLayout) view.findViewById(R.id.rela_proinfo);
			viewHolder.btn_shuhui = (Button) view.findViewById(R.id.btn_shuhui);
			viewHolder.btn_buy = (Button) view.findViewById(R.id.btn_buy);
			viewHolder.tv_proname = (TextView) view.findViewById(R.id.tv_proname);
			viewHolder.tv_adddate = (TextView) view.findViewById(R.id.tv_adddate);
			viewHolder.tv_limitvalue = (TextView) view.findViewById(R.id.tv_limitvalue); // 期限
			viewHolder.tv_interest = (TextView) view.findViewById(R.id.tv_interest);
			viewHolder.tv_benjinvalue = (TextView) view.findViewById(R.id.tv_benjinvalue); // 持有本金
			viewHolder.tv_weivalue = (TextView) view.findViewById(R.id.tv_weivalue); // 未结算收益
			viewHolder.tv_record = (TextView) view.findViewById(R.id.tv_record);// 收益日期
			viewHolder.tv_recordvalue = (TextView) view.findViewById(R.id.tv_recordvalue); // 收益金额

			viewHolder.btn_shuhui = (Button) view.findViewById(R.id.btn_shuhui);
			viewHolder.btn_buy = (Button) view.findViewById(R.id.btn_buy);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 赎回
		viewHolder.btn_shuhui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(myActivity, RedeemActivity.class);
				// intent.putExtra("orderid", financialAssets.getId());
				// intent.putExtra("target_id", financialAssets.getTarget_id());
				intent.putExtra("financialassets", financialAssets);
				myActivity.startActivity(intent);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		// 申购
		viewHolder.btn_buy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(myActivity, InvestActivity.class);
				intent.putExtra("product", financialAssets);
				intent.putExtra("formflag", true);
				myActivity.startActivity(intent);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		// 赋值
		viewHolder.tv_proname.setText(financialAssets.getSubject());
		viewHolder.tv_adddate.setText(DateManage.StringToDateymd(financialAssets.getAddtime() + ""));

		int limit = financialAssets.getLimit();
		int type = limit / 100000;
		int limitdate = limit % 100000;
		System.out.println("type= " + type + "date= " + limitdate);
		if (type == 1) {
			dateType = 1;
			viewHolder.tv_limitvalue.setText(limitdate + "天");
		} else if (type == 2) {
			dateType = 2;
			viewHolder.tv_limitvalue.setText(limitdate + "个月");
		} else if (type == 3) {
			dateType = 3;
			viewHolder.tv_limitvalue.setText(limitdate + "年");
		}

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		int addtime = financialAssets.getAddtime();
		Date add_Date = DateManage.StringToDateTwo(addtime + "");
		System.out.println("下单时间==  " + df.format(add_Date));
		//
		// Calendar rightNow = Calendar.getInstance();
		// rightNow.setTime(add_Date);
		// Date aatime = new Date();

		// int limit_min = financialAssets.getLimit_min();
		// int min_type = limit_min / 100000;
		// int limit_mindate = limit_min % 100000;
		// System.out.println("type= " + min_type + "date= " + limit_mindate);

		// df.format(rightNow.getTime());

		// if (min_type == 1) {
		// int day = limitdate;
		// rightNow.add(Calendar.DAY_OF_YEAR, day);// 日期加天
		// aatime = rightNow.getTime();
		// System.out.println("下单时间 +加day ==  "
		// + df.format(rightNow.getTime()));
		// System.out.println("new 时间 加天=" + aatime);
		// } else if (min_type == 2) {
		// int month = limitdate;
		// rightNow.add(Calendar.MONTH, month);// 日期加月
		// aatime = rightNow.getTime();
		// System.out.println("new 时间=" + aatime);
		// } else if (min_type == 3) {
		// int year = limitdate;
		// rightNow.add(Calendar.YEAR, year);// 日期加年
		// aatime = rightNow.getTime();
		// System.out.println("new 时间=" + aatime);
		// }

		// 判断是否可赎回
		// 1.最短赎回期限 + 购买时间 > 当前时间
		// 2.number_profit 可赎回份数 > 0
		// 可赎回份额 = 购买数量 - 已结算数量
		int number_profit = financialAssets.getBuy_number() - financialAssets.getNumber_profit();
		System.out.println("可赎回份额 == " + number_profit);
		Date nowdate = new Date();
		// System.currentTimeMillis();
		// System.out.println("当前时间=" + System.currentTimeMillis());
		System.out.println("nowTime==" + nowdate.getTime() / 1000);
		// System.out.println("aatime==" + aatime.getTime());
		// buy_endtime
		// nowdate.getTime() > aatime.getTime() &&
		// buy_endtime -- 用户可赎回的时间
		long buy_endtime = financialAssets.getBuy_endtime();
		System.out.println("buy_endtime" + buy_endtime);
		if (financialAssets.getIs_raply() == 0) { // 可赎回类型
			if (nowdate.getTime() / 1000 > buy_endtime && number_profit > 0) {
				System.out.println("可赎回asdfasdfasdaaaaaaaaaaaaa");
				viewHolder.btn_shuhui.setBackgroundResource(R.drawable.jh_licai_tixain_selector);
				viewHolder.btn_shuhui.setText("赎回");
				viewHolder.btn_shuhui.setEnabled(true);
				viewHolder.btn_shuhui.setTextColor(Color.parseColor("#e9502d"));

			}
			// else if (nowdate.getTime() / 1000 < buy_endtime && number_profit
			// > 0) {
			// viewHolder.btn_shuhui.setBackgroundResource(R.drawable.licai_recharge_noselect);
			// viewHolder.btn_shuhui.setEnabled(false);
			// viewHolder.btn_shuhui.setTextColor(Color.parseColor("#ffffff"));
			// viewHolder.btn_shuhui.setText("赎回");
			// System.out.println("最短赎回期限未达到，不可赎回");
			// }
			else {
				viewHolder.btn_shuhui.setBackgroundResource(R.drawable.licai_recharge_noselect);
				viewHolder.btn_shuhui.setEnabled(false);
				viewHolder.btn_shuhui.setTextColor(Color.parseColor("#ffffff"));
				viewHolder.btn_shuhui.setText("赎回");
				System.out.println("最短赎回期限未达到，不可赎回");
			}

		} else if (financialAssets.getIs_raply() == 1) { // 不可赎回类型
			viewHolder.btn_shuhui.setBackgroundResource(R.drawable.licai_recharge_noselect);
			viewHolder.btn_shuhui.setEnabled(false);
			viewHolder.btn_shuhui.setTextColor(Color.parseColor("#ffffff"));
			viewHolder.btn_shuhui.setText("赎回");
			System.out.println("getIs_raply==1 不可赎回类型");
		}
		// is_profit;// 1 已结算，否则未结算
		if (financialAssets.getIs_profit() == 1) {
			viewHolder.btn_shuhui.setBackgroundResource(R.drawable.licai_recharge_noselect);
			viewHolder.btn_shuhui.setEnabled(false);
			viewHolder.btn_shuhui.setTextColor(Color.parseColor("#ffffff"));
			viewHolder.btn_shuhui.setText("已结算");
			System.out.println("已结算，不可赎回");
		}
		// 判断是否能申购
		// 1.is_Status= 0 发布中，1已上架 ，2已下架，3已售罄，4 待上架；
		// 2.number= number_has (总数 = 购买数量)
		if (financialAssets.getIs_status() == 3 || financialAssets.getNumber() == financialAssets.getNumber_has()) {
			viewHolder.btn_buy.setBackgroundResource(R.drawable.licai_recharge_noselect);
			viewHolder.btn_buy.setTextColor(Color.parseColor("#ffffff"));
			viewHolder.btn_buy.setText("售罄");
			viewHolder.btn_buy.setEnabled(false);
		} else if (financialAssets.getIs_buy() == 1) {
			viewHolder.btn_buy.setBackgroundResource(R.drawable.licai_recharge_noselect);
			viewHolder.btn_buy.setText("仅限新手");
			viewHolder.btn_buy.setEnabled(false);
		} else {
			viewHolder.btn_buy.setBackgroundResource(R.drawable.jh_licai_recharge_selector);
			viewHolder.btn_buy.setText("申购");
			viewHolder.btn_buy.setEnabled(true);
		}

		// 已下架
		if (financialAssets.getIs_status() == 2) { // 已下架
			viewHolder.btn_buy.setBackgroundResource(R.drawable.licai_recharge_noselect);
			viewHolder.btn_buy.setEnabled(false);
			viewHolder.btn_buy.setTextColor(Color.parseColor("#ffffff"));
			viewHolder.btn_buy.setText("已下架");
			System.out.println("已下架，不可赎回");
		}

		float xx = financialAssets.getInterest();
		int yy = (int) xx;
		if (0 == (xx - (float) yy)) {
			viewHolder.tv_interest.setText(yy + "%");
		} else {
			viewHolder.tv_interest.setText(xx + "%");
		}

		float buyMoney = financialAssets.getBuy_money(); // 每份金额
		// 持有本金= 购买数量 * 每份金额
		viewHolder.tv_benjinvalue.setText(DataFormatUtil.floatsaveTwo(financialAssets.getBuy_number() * buyMoney));
		// 计算未结算收益
		viewHolder.tv_weivalue.setText(DataFormatUtil.floatsaveTwo(financialAssets.getProfit_no()));
		// 收益日期
		// viewHolder.tv_record.setText(DateManage.StringToDateymd(financialAssets
		// .getEndtime() + "")
		// + "收益");
		viewHolder.tv_record.setText("预期收益");
		// 预期收益金额: 已计算收益 +未结算收益
		// viewHolder.tv_recordvalue.setText(DataFormatUtil
		// .floatsaveTwo(financialAssets.getProfit_actual()
		// + financialAssets.getProfit_no()));
		// total_profit

		viewHolder.tv_recordvalue.setText(DataFormatUtil.floatsaveTwo(financialAssets.getTotal_profit()));

		// 点交易详情
		viewHolder.rela_proinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(myActivity, TransactionDetailsActivity.class);
				intent.putExtra("product", financialAssets);
				// intent.putExtra("subject", financialAssets.getSubject());
				// if (financialAssets.getIs_payment() == 1) {
				// intent.putExtra("order_no", financialAssets.getOrder_no());
				// }
				myActivity.startActivity(intent);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		return view;
	}

	public class ViewHolder {
		private RelativeLayout rela_proinfo; // 理财项目--点击进入交易详情
		private Button btn_shuhui, btn_buy; // 赎回，申购（或者 已结算 售罄）
		private TextView tv_proname, tv_adddate, tv_limitvalue, tv_interest, tv_benjinvalue, tv_weivalue, tv_record, tv_recordvalue;
	}

}
