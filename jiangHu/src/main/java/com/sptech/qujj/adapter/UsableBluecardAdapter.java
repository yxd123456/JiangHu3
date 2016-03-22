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
import com.sptech.qujj.activity.UsableBlueCardActivity;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.util.Base64;

/*
 * 
 * 银行卡列表
 * 
 */

public class UsableBluecardAdapter extends BaseAdapter {

	private UsableBlueCardActivity myActivity;
	private LayoutInflater layoutInflater;
	private List<UsablebankBean> bList = new ArrayList<UsablebankBean>();
	private ListView listview;

	// 用于记录每个RadioButton的状态，并保证只可选一个
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();

	public UsableBluecardAdapter(UsableBlueCardActivity activity, List<UsablebankBean> list) {
		this.myActivity = activity;
		this.bList.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<UsablebankBean> list) {
		this.bList.removeAll(bList);
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
		final UsablebankBean bp = bList.get(position);
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_usablebank_cardlist, null);
			viewHolder = new ViewHolder();
			viewHolder.rb_right = (RadioButton) view.findViewById(R.id.rb_right);
			// System.out.println("positon===" + position);
			viewHolder.img_card = (ImageView) view.findViewById(R.id.img_card);
			viewHolder.tv_bankname = (TextView) view.findViewById(R.id.tv_bankname);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_bankname.setText(bp.getName() + "");
		String userface = bp.getStream();
		if (userface.equals("")) {
			viewHolder.img_card.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(userface);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			viewHolder.img_card.setImageBitmap(bit);
		}

		final RadioButton radio = (RadioButton) view.findViewById(R.id.rb_right);
		viewHolder.rb_right = radio;
		viewHolder.rb_right.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 重置，确保最多只有一项被选中
				for (String key : states.keySet()) {
					states.put(key, false);
				}
				System.out.println("positon===" + position);
				states.put(String.valueOf(position), radio.isChecked());
				UsableBluecardAdapter.this.notifyDataSetChanged();

				myActivity.test(position);
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

		return view;
	}

	public class ViewHolder {
		public ImageView image_card;
		public TextView tv_cardname, tv_info, tv_bankname;
		public RadioButton rb_right;// 选中与否
		public ImageView img_card;
	}

}
