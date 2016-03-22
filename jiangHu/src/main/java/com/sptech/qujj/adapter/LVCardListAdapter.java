package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;

/**
 * 信用卡 列表 Adapter
 * 
 * @author yebr
 * 
 */
public class LVCardListAdapter extends BaseAdapter {

	private String saleflag;
	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<CardInfo> curCardInfos = new ArrayList<CardInfo>();
	private ListView listview;
	HashMap<String, String> cardMap = new HashMap<String, String>();
	private SharedPreferences spf;

	public LVCardListAdapter(Activity activity, List<CardInfo> list) {
		this.myActivity = activity;
		this.curCardInfos.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
		spf = activity.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		cardMap = DataFormatUtil.getCardMap(spf);
	}

	public void reset(List<CardInfo> list) {
		this.curCardInfos.clear();
		this.curCardInfos.addAll(list);
	}

	public int getCount() {
		return curCardInfos.size();
		// return 2;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		final CardInfo cardInfo = curCardInfos.get(position);
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_card, null);
			viewHolder = new ViewHolder();
			viewHolder.img_card = (ImageView) view.findViewById(R.id.img_card);
			viewHolder.btn_repay = (Button) view.findViewById(R.id.btn_repay);
			viewHolder.tv_cardbank = (TextView) view.findViewById(R.id.tv_cardbank);
			viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			viewHolder.tv_cardno = (TextView) view.findViewById(R.id.tv_cardno);
			viewHolder.tv_huanmoney = (TextView) view.findViewById(R.id.tv_huanmoney); // 还款金额

			viewHolder.tv_huanvalue = (TextView) view.findViewById(R.id.tv_huanvalue); // 还款日期

			viewHolder.tv_mian = (TextView) view.findViewById(R.id.tv_mian); // 离还款期
			                                                                 // 逾期，或出账；
			viewHolder.tv_mianvalue = (TextView) view.findViewById(R.id.tv_mianvalue); // 天数

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// tv_amount_of.setText("¥"
		// + DataFormatUtil.floatsaveTwo(cardInfo.getAmount_of()));

		viewHolder.tv_huanmoney.setText("¥" + DataFormatUtil.floatsaveTwo(cardInfo.getAmount_of()));
		System.out.println("还款金额" + DataFormatUtil.floatsaveTwo(cardInfo.getAmount_of()));

		viewHolder.tv_cardbank.setText(cardInfo.getCard_bank());

		viewHolder.tv_name.setText(DataFormatUtil.hideFirstname(cardInfo.getCard_realname()));

		if (!cardInfo.getCard_no().equals("")) {
			viewHolder.tv_cardno.setText(DataFormatUtil.bankcardsaveFour(cardInfo.getCard_no()));
		}

		// tv_cardno.setText("3818");
		String cardname = cardInfo.getCard_bank();
		String cardStream = cardMap.get(cardname);
		if (cardStream == null || cardStream.equals("")) {
			viewHolder.img_card.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(cardStream);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			viewHolder.img_card.setImageBitmap(bit);
		}

		// 时间
		System.out.println("到期日== " + cardInfo.getRepayment_date());
		System.out.println("当前时间==  " + System.currentTimeMillis() / 1000 + "");
		Long between = (cardInfo.getRepayment_date() - System.currentTimeMillis() / 1000);
		System.out.println("时间差" + between);
		int bill_month = cardInfo.getBill_month();
		String billMonth = DateManage.getYearMonth(bill_month + "");
		System.out.println("billMonth === " + billMonth);
		String nowMonth = DateManage.getYearMonth(System.currentTimeMillis() / 1000 + "");
		System.out.println("nowmonth === " + nowMonth);

		// 账单期距离当前时间差
		Long between2 = (cardInfo.getRepayment_date() - System.currentTimeMillis() / 1000);
		// 先判断该账单是否是当前月的
		if (billMonth.equals(nowMonth)) {
			viewHolder.tv_mian.setText("离出账");
			viewHolder.tv_mianvalue.setText(DateManage.getDayFormUX(between2) + "天");
		} else if (between > 0) {
			viewHolder.tv_mian.setText("离还款期");
			viewHolder.tv_mianvalue.setText(DateManage.getDayFormUX(between) + "天");
		} else if (between < 0) {
			viewHolder.tv_mian.setText("逾期");
			viewHolder.tv_mianvalue.setText(DateManage.getDayFormUX(-between) + "天");
		}

		String huanString = DateManage.getMonthDay(cardInfo.getRepayment_date() + "");
		viewHolder.tv_huanvalue.setText(huanString);
		System.out.println("还款期" + huanString);

		// String zhangString = DateManage.getMonthDay(cardInfo
		// .getBill_cycle_end() + "");
		// viewHolder.tv_zhangdanvalue.setText(zhangString);
		// System.out.println("账单期" + zhangString);

		viewHolder.btn_repay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 一键还款
				// Intent apply_repayment = new Intent(myActivity,
				// HtmlActivity.class);
				// apply_repayment.putExtra("act", "apply_repayment");
				// apply_repayment.putExtra("id", cardInfo.getId());
				// myActivity.startActivity(apply_repayment);
				// myActivity.overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
			}
		});

		return view;
	}

	public class ViewHolder {
		public Button btn_huan, btn_repay;// 一键还款按钮，到期时间(到期日-当前时间),
		public ImageView img_card;// 银行卡图标，
		public TextView tv_cardbank, tv_name, tv_cardno, tv_huanmoney, tv_huanvalue, tv_mian, tv_mianvalue; // 银行卡名称,持卡人,卡号,还款金额，还款日期，本期应还;
		// public ImageView image, img_order, imagetejia;
		// public TextView text_ytname, tv_city, tv_fen, tv_distance, tv_ytfee;
	}

}