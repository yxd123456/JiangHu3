package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.model.UsablebankBean;

public class UsableHandcardAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<UsablebankBean> list;// /有序的list集合（现在是按拼音排序好的）
	private HashMap<String, Integer> alphaIndexer;// 保存每个索引在list中的位置【#-0，A-4，B-10】
	private String[] sections;// 每个分组的索引表【A,B,C,F...】
	private ArrayList<String> sectionList;

	// private String savefirst = "";

	public UsableHandcardAdapter(Context context, List<UsablebankBean> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list; // 该list是已经排序过的集合，有些项目中的数据必须要自己进行排序。
		this.alphaIndexer = new HashMap<String, Integer>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bankcard_item, null);
			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.chengshinane);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UsablebankBean ub = list.get(position);
		String name = ub.getName();
		String first = ub.getFirst();
		holder.name.setText(name);

		// // 当前联系人的sortKey
		// String currentStr =
		// getAlpha(PinyingUtil.stringArrayToString(PinyingUtil.getHeadByString(name)));
		// // 上一个联系人的sortKey
		String previewStr = (position - 1) >= 0 ? list.get(position - 1).getFirst() : " ";
		// /**
		// * 判断显示#、A-Z的TextView隐藏与可见
		// */
		if (!previewStr.equals(first)) { // 当前联系人的sortKey！=上一个联系人的sortKey，说明当前联系人是新组。
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(first);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * 其他项目使用时，只需要传进来一个**有序**的list即可
	 */
	private static class ViewHolder {
		TextView name;
		// A--Z分类的显示
		TextView alpha;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str1) {
		// /截取字符串，把“（”后面的内容舍去
		String msg[] = str1.split("\\(");
		String str = msg[0];
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}
		// /取出字符串中的的第一个字符
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 大写输出
		} else {
			return "#";
		}
	}

}
