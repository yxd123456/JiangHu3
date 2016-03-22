package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.SelectCardActivity;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;

/*
 * 
 * 银行卡列表（用户选择银行卡 ）
 * 
 */

public class LVBankCardAdapter extends BaseAdapter {

	private SelectCardActivity myActivity;
	private LayoutInflater layoutInflater;
	private List<BankcardBean> bList = new ArrayList<BankcardBean>();
	private ListView listview;
	HashMap<String, String> cardMap = new HashMap<String, String>(); // 银行卡的图标map

	// 用于记录每个RadioButton的状态，并保证只可选一个
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	private int backflag;

	public LVBankCardAdapter(SelectCardActivity activity, int backflag, HashMap<String, String> cardMap, List<BankcardBean> list) {
		this.myActivity = activity;
		this.bList = list;
		this.cardMap = cardMap;
		this.backflag = backflag;
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<BankcardBean> list) {
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
		return this.bList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final BankcardBean bankcardBean = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_bank_cardlist, null);
			viewHolder = new ViewHolder();
			viewHolder.rb_right = (RadioButton) view.findViewById(R.id.rb_right);
			// System.out.println("positon===" + position);
			viewHolder.img_card = (ImageView) view.findViewById(R.id.img_card);
			viewHolder.img_cardtype = (ImageView) view.findViewById(R.id.img_cardtype);

			viewHolder.tv_bank = (TextView) view.findViewById(R.id.tv_bank);
			viewHolder.tv_realname = (TextView) view.findViewById(R.id.tv_realname);
			viewHolder.tv_cardno = (TextView) view.findViewById(R.id.tv_cardno);
			viewHolder.tv_ketixian = (TextView) view.findViewById(R.id.tv_ketixian);

			// if (position == 0) {
			// viewHolder.rb_right.setChecked(true);
			// // states.put(String.valueOf(0), true);
			// }

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 赋值
		viewHolder.tv_bank.setText(bankcardBean.getCard_bank());
		viewHolder.tv_realname.setText(DataFormatUtil.hideFirstname(bankcardBean.getCard_realname()));
		viewHolder.tv_cardno.setText(DataFormatUtil.bankcardsaveFour(bankcardBean.getCard_no()));
		viewHolder.img_cardtype.setBackgroundResource(R.drawable.jh_licai_bank);

		if (backflag == 2) {
			float keti = bankcardBean.getOut_money_actual();
			if (keti == 0) {
				viewHolder.tv_ketixian.setText("无可提现金额");
				// viewHolder.rb_right.setVisibility(View.GONE);
			} else {
				// viewHolder.rb_right.setVisibility(View.VISIBLE);
				viewHolder.tv_ketixian.setText("(可提现 " + DataFormatUtil.floatsaveTwo(keti) + ")");
			}
			viewHolder.tv_ketixian.setVisibility(View.VISIBLE);
		}
		String cardname = bankcardBean.getCard_bank();
		String cardStream = cardMap.get(cardname);
		// System.out.println("card--name==" + cardname);
		// System.out.println("card--img==" + cardStream);

		if (cardStream == null || cardStream.equals("")) {
			viewHolder.img_card.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(cardStream);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			viewHolder.img_card.setImageBitmap(bit);
		}

		// 单选按钮
		final RadioButton radio = (RadioButton) view.findViewById(R.id.rb_right);
		viewHolder.rb_right = radio;
		viewHolder.rb_right.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 重置，确保最多只有一项被选中
				for (String key : states.keySet()) {
					states.put(key, false);
				}
				states.put(String.valueOf(position), radio.isChecked());
				LVBankCardAdapter.this.notifyDataSetChanged();
				myActivity.back(position);
			}
		});

		boolean res = false;
		if (states.get(String.valueOf(position)) == null || states.get(String.valueOf(position)) == false) {
			res = false;
			states.put(String.valueOf(position), false);
			viewHolder.rb_right.setBackgroundResource(R.drawable.btn_bankcard_unselect);
		} else {
			res = true;
			viewHolder.rb_right.setBackgroundResource(R.drawable.btn_bankcard_select);
		}
		viewHolder.rb_right.setChecked(res);
		// System.out.println("positon ==" + position);
		return view;
	}

	public void setDefalut(int position_select) {
		states.put(String.valueOf(position_select), true);
	}

	public class ViewHolder {
		public RadioButton rb_right;// 选中与否

		public ImageView img_card, img_cardtype;// 银行卡图标，卡类型
		public TextView tv_bank, tv_realname, tv_cardno, tv_ketixian;// 银行名称，真实姓名，卡号,//可提现金额
	}

}
