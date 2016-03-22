package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;

/**
 * 银行卡列表 Adapter (银行卡管理页面)
 * 
 * @author yebr
 * 
 */
public class BankListAdapter extends BaseAdapter {

	private String saleflag;
	// private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<BankcardBean> bList = new ArrayList<BankcardBean>();
	private ListView listview;
	private Context mConstant;
	HashMap<String, String> cardMap = new HashMap<String, String>();

	public BankListAdapter(Activity constant, HashMap<String, String> cardMap, List<BankcardBean> list) {
		this.mConstant = constant;
		this.bList = list;
		this.cardMap = cardMap;
		layoutInflater = LayoutInflater.from(constant);
	}

	public void reset(List<BankcardBean> list) {
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
		final BankcardBean eb = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_bank_list, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_cardtype = (TextView) view.findViewById(R.id.tv_bankname);
			viewHolder.tv_carnameno = (TextView) view.findViewById(R.id.tv_carnameno);
			viewHolder.iv_banktype = (ImageView) view.findViewById(R.id.iv_banktype);
			viewHolder.img_card = (ImageView) view.findViewById(R.id.img_card);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_cardtype.setText(eb.getCard_bank() + "");
		String cardname = eb.getCard_bank();
		String cardStream = cardMap.get(cardname);
		if (cardStream == null || cardStream.equals("")) {
			viewHolder.img_card.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(cardStream);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			viewHolder.img_card.setImageBitmap(bit);
		}
		// String cardno = eb.getCard_no();
		// String card = cardno.substring(cardno.length() - 4, cardno.length());
		viewHolder.tv_carnameno.setText(DataFormatUtil.hideFirstname(eb.getCard_realname()) + "　" + DataFormatUtil.bankcardsaveFour(eb.getCard_no()) + "");

		if (eb.getCard_type() == 1) {
			viewHolder.iv_banktype.setBackgroundResource(R.drawable.jh_licai_bank);
		} else {
			viewHolder.iv_banktype.setBackgroundResource(R.drawable.img_credit);
		}
		return view;
	}

	public class ViewHolder {
		public RelativeLayout rela_top;
		public TextView tv_cardtype, tv_carnameno;
		public ImageView iv_banktype, img_card;
		// public ImageView image, img_order, imagetejia;
		// public TextView text_ytname, tv_city, tv_fen, tv_distance, tv_ytfee;
	}

}