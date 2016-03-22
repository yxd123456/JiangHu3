package com.sptech.qujj.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.MyLoanActivity;
import com.sptech.qujj.activity.WebViewActivity;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.LoanRecord;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/**
 * 我的借款 列表 Adapter
 * 
 * @author yebr
 * 
 */
public class LVMyLoanListAdapter extends BaseAdapter {

	private String saleflag;
	private MyLoanActivity myActivity;
	private LayoutInflater layoutInflater;
	private List<LoanRecord> loanRecords = new ArrayList<LoanRecord>();
	HashMap<String, String> cardMap = new HashMap<String, String>();
	private ListView listview;

	public LVMyLoanListAdapter(MyLoanActivity activity, HashMap<String, String> cardMap, List<LoanRecord> list) {
		this.myActivity = activity;
		this.loanRecords.addAll(list);
		this.cardMap = cardMap;
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<LoanRecord> list) {
		this.loanRecords.clear();
		this.loanRecords.addAll(list);
	}

	public int getCount() {
		return loanRecords.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder viewHolder;
		final LoanRecord loanRecord = loanRecords.get(position);
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_myloan_list, null);
			viewHolder = new ViewHolder();
			viewHolder.img_status = (RelativeLayout) view.findViewById(R.id.img_status); // 借款状态
			viewHolder.img_card = (ImageView) view.findViewById(R.id.img_card); // 卡图标

			viewHolder.img_open = (CheckBox) view.findViewById(R.id.img_open);//
			viewHolder.rela_content = (RelativeLayout) view.findViewById(R.id.rela_content);
			viewHolder.tv_hetong = (TextView) view.findViewById(R.id.tv_hetong);
			viewHolder.tv_cardbank = (TextView) view.findViewById(R.id.tv_cardbank); // 银行名字
			viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name); // 持卡人

			viewHolder.tv_cardno = (TextView) view.findViewById(R.id.tv_cardno); // 卡号
			viewHolder.tv_loanmoney = (TextView) view.findViewById(R.id.tv_loanmoney); // 欠款金额

			viewHolder.tv_qianbeizhu = (TextView) view.findViewById(R.id.tv_qianbeizhu); // 欠款计算公式
			// 欠款金额 = 本金 + 手续费 (本金*1%*28天)
			viewHolder.tv_loantime = (TextView) view.findViewById(R.id.tv_loantime); // 借款时间
			viewHolder.tv_huantime = (TextView) view.findViewById(R.id.tv_huantime); // 还款周期
			viewHolder.tv_jiemoney = (TextView) view.findViewById(R.id.tv_jiemoney); // 借款金额
			viewHolder.tv_jiewhy = (TextView) view.findViewById(R.id.tv_jiewhy); // 借款用途

			viewHolder.tv_yhmoney = (TextView) view.findViewById(R.id.tv_yhmoney); // 应还金额
			viewHolder.tv_dateValue = (TextView) view.findViewById(R.id.tv_dateValue); // 还款日期

			viewHolder.tv_sy = (TextView) view.findViewById(R.id.tv_sy); // 还款/逾期
			viewHolder.tv_day = (TextView) view.findViewById(R.id.tv_day); // 天数
			viewHolder.btn_huan = (Button) view.findViewById(R.id.btn_huan);// 还款按钮
			// 借款利息 逾期利息
			viewHolder.tv_lixi = (TextView) view.findViewById(R.id.tv_lixi);
			viewHolder.tv_yuqilixi = (TextView) view.findViewById(R.id.tv_yuqilixi);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 赋值
		String cardname = "";
		if (loanRecord.getCard_bank() != null) {
			cardname = loanRecord.getCard_bank();
			viewHolder.tv_cardbank.setText(cardname);
		}
		String cardStream = "";
		cardStream = cardMap.get(cardname);
		if (cardStream == null || "".equals(cardStream)) {
			viewHolder.img_card.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(cardStream);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			viewHolder.img_card.setImageBitmap(bit);
		}
		//

		if (loanRecord.getUser_realname() != null) {
			viewHolder.tv_name.setText(DataFormatUtil.hideFirstname(loanRecord.getUser_realname()));
		}

		if (loanRecord.getCard_no() != null) {
			viewHolder.tv_cardno.setText(DataFormatUtil.bankcardsaveFour(loanRecord.getCard_no()));
		}
		viewHolder.tv_loanmoney.setText("¥" + DataFormatUtil.floatsaveTwo(loanRecord.getLoan_money()));
		// tv_qianbeizhu 欠款金额 = 本金 + 手续费 (本金*1%*28天)
		// tv_loantime 借款时间: 2015年06月28天
		// tv_huantime 还款周期: 28天
		// tv_jiemoney 借款金额: ¥5000.00
		// tv_jiewhy借款用途: 信用卡还款(中国建设银行 9527 )
		viewHolder.tv_qianbeizhu.setText("欠款金额 = 本金+手续费(本金*" + loanRecord.getHelp_limit() + "*22%/365+15)");
		if (loanRecord.getTakeeffect_time() == 0) {
			viewHolder.tv_loantime.setText("借款时间: 未知");
		} else {
			viewHolder.tv_loantime.setText("借款时间: " + DateManage.getYearMonthDay(String.valueOf(loanRecord.getTakeeffect_time())));
		}
		viewHolder.tv_huantime.setText("还款周期: " + loanRecord.getHelp_limit() + "天");
		viewHolder.tv_jiemoney.setText("借款金额: ¥" + DataFormatUtil.floatsaveTwo(loanRecord.getHelp_money()));
		viewHolder.tv_jiewhy.setText("借款用途: 信用卡还款(" + loanRecord.getCard_bank() + " " + DataFormatUtil.bankcardsaveFour(loanRecord.getCard_no()) + ")");
		viewHolder.tv_lixi.setText("    手续费: ¥" + DataFormatUtil.floatsaveTwo(loanRecord.getHelp_service_fee()));

		// takeeffect_time + help_limit * 24 * 3600
		// 判断借款时间 状态
		long huandate = (loanRecord.getHelp_limit() - 1) * 24 * 3600 + loanRecord.getTakeeffect_time();// 还款日期
		long between = (huandate - System.currentTimeMillis() / 1000);// 还款日期与当前时间差

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dstr = sdf.format(System.currentTimeMillis());
		Date curDate = null;
		try {
			curDate = sdf.parse(dstr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("curDate== " + dstr);
		String huanstr = sdf.format(huandate * 1000);
		Date huanDate = null;
		try {
			huanDate = sdf.parse(huanstr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("huanDate== " + huanstr);
		long days = (huanDate.getTime() - curDate.getTime()) / (1000 * 60 * 60 * 24);

		// System.out.println("huandate==== " + huandate);
		// System.out.println("between==== " + between);
		System.out.println("逾期 days==== " + days);
		// 逾期利息
		float yuqi = 0;
		if (days > 0) {
			viewHolder.tv_yuqilixi.setVisibility(View.GONE);
			viewHolder.img_status.setBackgroundResource(R.drawable.img_label_surplus);// 剩余
			// 转化计算为天
			viewHolder.tv_sy.setText("剩余");
			viewHolder.tv_day.setText(days + " 天");
		} else {
			// int day = Math.round((-between) / 24 / 3600);
			// System.out.println("剩余 day == " + (float) ((-between) / 24 /
			// 3600));
			// System.out.println("逾期 day == " + day);
			viewHolder.img_status.setBackgroundResource(R.drawable.img_label_expired);// 逾期
			// 转化计算为天
			viewHolder.tv_sy.setText("逾期");

			if ((-days) > 99) {
				viewHolder.tv_day.setText("99+天 ");
			} else {
				// viewHolder.tv_day.setText(DateManage.getDayFormUX(-between) +
				// " 天");
				viewHolder.tv_day.setText((-days) + " 天");
			}
			viewHolder.tv_yuqilixi.setVisibility(View.VISIBLE);
			yuqi = (float) (loanRecord.getHelp_money() * 0.0006 * (Integer.parseInt(DateManage.getDayFormUX(-between))));// 逾期金额（本金的万分之六）
			viewHolder.tv_yuqilixi.setText("逾期利息: ¥" + DataFormatUtil.floatsaveTwo(yuqi));
		}
		// 应还金额=借款金额+手续费+逾期利息
		float yinhuan = loanRecord.getHelp_money() + loanRecord.getHelp_service_fee() + yuqi;
		viewHolder.tv_yhmoney.setText("¥" + DataFormatUtil.floatsaveTwo(yinhuan));
		// 还款日期
		viewHolder.tv_dateValue.setText(DateManage.getAddDate(String.valueOf(loanRecord.getTakeeffect_time()), loanRecord.getHelp_limit() - 1));
		viewHolder.img_open.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (viewHolder.img_open.isChecked()) {
					viewHolder.rela_content.setVisibility(View.VISIBLE);
				} else {
					viewHolder.rela_content.setVisibility(View.GONE);
				}
			}
		});

		// 合同协议
		viewHolder.tv_hetong.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		viewHolder.tv_hetong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent rela_service = new Intent(myActivity, WebViewActivity.class);
				rela_service.putExtra("url", JsonConfig.HTML + "/index/service");
				rela_service.putExtra("title", "服务中心");
				myActivity.startActivity(rela_service);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		// 还款
		viewHolder.btn_huan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				myActivity.dialogPay(loanRecord);
			}
		});

		return view;
	}

	public class ViewHolder {
		private CheckBox img_open;
		private RelativeLayout rela_content, img_status;

		private ImageView img_card;
		public TextView tv_hetong, tv_sy, tv_day, tv_cardbank, tv_name, tv_cardno, tv_loanmoney, tv_qianbeizhu, tv_loantime, tv_huantime, tv_jiemoney, tv_jiewhy, tv_yhmoney, tv_dateValue, tv_lixi,
		        tv_yuqilixi;
		public Button btn_huan;
	}

}