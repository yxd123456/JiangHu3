package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.FieldNamingStrategy;
import com.sptech.qujj.R;
import com.sptech.qujj.activity.SelectBlueCardActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;

/**
 * 信用卡和储蓄卡列表 Adapter 
 * 
 * @author 叶旭东
 * 
 */
public class CardListAdapter extends BaseAdapter {

	private String saleflag;
	// private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<CardInfo> bList = new ArrayList<CardInfo>();
	private ListView listview;
	private Activity mConstant;
	private ImageView iv_gou;
	HashMap<String, String> cardMap = new HashMap<String, String>();
	private Bitmap bit;
	private String cardname;

	public CardListAdapter(Activity constant, HashMap<String, String> cardMap, List<CardInfo> list,
			ListView lv) {
		this.mConstant =  constant;
		this.bList = list;
		this.cardMap = cardMap;
		layoutInflater = LayoutInflater.from(constant);
		listview = lv;
	}

	public void reset(List<CardInfo> list) {
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

	public View getView(final int position, View view, ViewGroup parent) {
		final CardInfo eb = bList.get(position);
		final ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_bank_list2, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_cardtype = (TextView) view.findViewById(R.id.tv_bankname);
			viewHolder.tv_carnameno = (TextView) view.findViewById(R.id.tv_carnameno);
			viewHolder.iv_banktype = (ImageView) view.findViewById(R.id.iv_banktype);
			viewHolder.img_card = (ImageView) view.findViewById(R.id.img_card);
			viewHolder.iv_gou = (ImageView) view.findViewById(R.id.iv_gou);					
			
/*<<<<<<< .mine
		    viewHolder.iv_gou.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!Constant.flag_iv){
						viewHolder.iv_gou.setImageResource(R.drawable.btn_bankcard_select);
						Constant.flag_iv = true;
						for (int i = 0; i < listview.getChildCount(); i++) {
							if(i != position){
								LinearLayout ll = (LinearLayout) listview.getChildAt(i);
								ImageView iv = (ImageView) ll.findViewById(R.id.iv_gou);
								iv.setImageResource(R.drawable.btn_bankcard_unselect);
							}
						}
						Intent i = new Intent();
						Bundle bundle = new Bundle();
						bundle.putParcelable("img_bank", bit);
						bundle.putString("tv_bank", cardname);
						bundle.putString("card_no", eb.getCard_no());
						i.putExtras(bundle);
						mConstant.setResult(mConstant.RESULT_OK, i);
						mConstant.finish();
					} else {
						viewHolder.iv_gou.setImageResource(R.drawable.btn_bankcard_unselect);
						Constant.flag_iv = false;
					}
				}
			});

			
=======
>>>>>>> .r4672*/
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_cardtype.setText(eb.getCard_bank() + "");
		cardname = eb.getCard_bank();
		String cardStream = cardMap.get(cardname);
		if (cardStream == null || cardStream.equals("")) {
			viewHolder.img_card.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(cardStream);
			bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			viewHolder.img_card.setImageBitmap(bit);
		}
		// String cardno = eb.getCard_no();
		// String card = cardno.substring(cardno.length() - 4, cardno.length());
		if (eb.getCard_no().equals("")) {
			viewHolder.tv_carnameno.setText(DataFormatUtil.hideFirstname(eb.getCard_realname()) + "　");
		} else {
			viewHolder.tv_carnameno.setText(DataFormatUtil.hideFirstname(eb.getCard_realname()) + "　" + DataFormatUtil.bankcardsaveFour(eb.getCard_no()) + "");
		}
		// if (eb.getCard_type() == 1) {
		// viewHolder.iv_banktype.setBackgroundResource(R.drawable.jh_licai_bank);
		// } else {
		viewHolder.iv_banktype.setBackgroundResource(R.drawable.img_credit);
		// }
		
		return view;
	}

	public class ViewHolder {

		public RelativeLayout rela_top;
			
		public TextView tv_cardtype, tv_carnameno;
		public ImageView iv_banktype, img_card, iv_gou;
		// public ImageView image, img_order, imagetejia;
		// public TextView text_ytname, tv_city, tv_fen, tv_distance, tv_ytfee;
	}

}