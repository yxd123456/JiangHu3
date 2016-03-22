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
import com.sptech.qujj.activity.ProductDetailInfoActivity;
import com.sptech.qujj.model.Product;
import com.sptech.qujj.view.CircleProgressBar;

/**
 * 推荐项目 列表 Adapter
 * 
 * @author yebr
 * 
 */
public class LVFMListAdapter extends BaseAdapter {

	private String saleflag;
	private Activity myActivity;
	private LayoutInflater layoutInflater;
	private List<Product> productList = new ArrayList<Product>();// 产品
	private ListView listview;

	public LVFMListAdapter(Activity activity, List<Product> list) {
		this.myActivity = activity;
		this.productList.addAll(list);
		layoutInflater = LayoutInflater.from(activity);
	}

	public void reset(List<Product> productList) {
		this.productList.clear();
		this.productList.addAll(productList);
	}

	public int getCount() {
		return productList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		final Product product = productList.get(position);
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_tuijian, null);
			viewHolder = new ViewHolder();
			viewHolder.image_new = (ImageView) view.findViewById(R.id.image_new);
			viewHolder.circleProgressBar = (CircleProgressBar) view.findViewById(R.id.circleprogressbar);
			viewHolder.tv_subject = (TextView) view.findViewById(R.id.tv_proname);
			viewHolder.tv_interest = (TextView) view.findViewById(R.id.tv_yearvalue);
			viewHolder.tv_limit = (TextView) view.findViewById(R.id.tv_datevalue);
			viewHolder.tv_buy_money_min = (TextView) view.findViewById(R.id.tv_startvalue);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// if (position == 0) {
		// viewHolder.image_new.setVisibility(View.VISIBLE);
		// }

		int hasBuy = 0;
		if (product.getNumber() != 0) {
			int has = product.getNumber_has();
			int count = product.getNumber();
			hasBuy = (int) Math.round(((double) has / count * 100));
		}

		// System.out.println("进度==" + hasBuy);
		viewHolder.circleProgressBar.setProgress(hasBuy);

		if (product.getSubject().length() > 15) {
			String title = product.getSubject().substring(0, 15);
			viewHolder.tv_subject.setText(title + "...");
		} else {
			viewHolder.tv_subject.setText(product.getSubject());
		}

		float xx = product.getInterest();
		int yy = (int) xx;
		if (0 == (xx - (float) yy)) {
			viewHolder.tv_interest.setText(yy + "%");
		} else {
			viewHolder.tv_interest.setText(xx + "%");
		}
		// viewHolder.tv_interest.setText(product.getInterest() + "%");

		float min = product.getBuy_money_min();
		int kk = (int) min;
		if (0 == (min - (float) kk)) {
			viewHolder.tv_buy_money_min.setText(kk + "元");
		} else {
			viewHolder.tv_buy_money_min.setText(min + "元");
		}
		// viewHolder.tv_buy_money_min.setText(product.getBuy_money_min() +
		// "元");

		int limit = product.getLimit();
		int type = limit / 100000;
		int limitdate = limit % 100000;
		// System.out.println("type= " + type + "date= " + limitdate);
		if (type == 1) {
			viewHolder.tv_limit.setText(limitdate + "天");
		} else if (type == 2) {
			viewHolder.tv_limit.setText(limitdate + "个月");
		} else if (type == 3) {
			viewHolder.tv_limit.setText(limitdate + "年");
		}

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Intent intent2 = new Intent(myActivity,
				// ProductInfoActivity.class);
				Intent intent2 = new Intent(myActivity, ProductDetailInfoActivity.class);
				intent2.putExtra("id", product.getId());
				intent2.putExtra("pro_name", product.getSubject());
				// intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				myActivity.startActivity(intent2);
				myActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		return view;
	}

	public class ViewHolder {
		private ImageView image_new;
		public CircleProgressBar circleProgressBar;
		public TextView tv_buy_money_min, tv_subject, tv_interest, tv_limit;
		// public ImageView image, img_order, imagetejia;
		// public TextView text_ytname, tv_city, tv_fen, tv_distance, tv_ytfee;
	}

}