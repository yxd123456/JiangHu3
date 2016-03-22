package com.sptech.qujj.fragment;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.activity.AddMailActivity;
import com.sptech.qujj.activity.LiCaiActivity;
import com.sptech.qujj.activity.MailActivity;
import com.sptech.qujj.activity.MyHandCardActivity;
import com.sptech.qujj.activity.MyLoanActivity;
import com.sptech.qujj.activity.PersonInfoActivity;
import com.sptech.qujj.activity.ProgressActivity;
import com.sptech.qujj.activity.SettingActivity;
import com.sptech.qujj.activity.WebViewActivity;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.CircleImageView;

/**
 * 侧边栏
 * 
 * @author yebr
 * 
 */
public class MenuLeftFragment extends BaseFragment implements OnClickListener {
	private RelativeLayout rela_userinfo, rela_mail, rela_card; // user信息,邮箱，银行卡
	private RelativeLayout rela_licai, rela_borrow, rela_progress, rela_activity, rela_service, rela_setting;// 我要理财，我要借款，进度中心，活动中心，服务中心，设置；

	private CircleImageView user_image;// 圆形用户头像
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private TextView tv_name, tv_phone, tv_bankcardnum, tv_emailnum, tv_tellnum;
	private String emailnum; // 邮箱数量
	private final int GETUSERINFO = 1;

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.left_slidemenu, container, false);
	}

	@Override
	protected void initView() {
		dialogHelper = new DialogHelper(getActivity());
		spf = getActivity().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		rela_userinfo = (RelativeLayout) selfView.findViewById(R.id.rela_userinfo);
		tv_name = (TextView) selfView.findViewById(R.id.tv_name);
		tv_phone = (TextView) selfView.findViewById(R.id.tv_phone);
		rela_mail = (RelativeLayout) selfView.findViewById(R.id.rela_mail);
		rela_card = (RelativeLayout) selfView.findViewById(R.id.rela_card);
		tv_bankcardnum = (TextView) selfView.findViewById(R.id.tv_bankcardnum);
		user_image = (CircleImageView) selfView.findViewById(R.id.user_image);
		tv_tellnum = (TextView) selfView.findViewById(R.id.tv_tellnum);

		rela_licai = (RelativeLayout) selfView.findViewById(R.id.rela_licai);
		rela_borrow = (RelativeLayout) selfView.findViewById(R.id.rela_borrow);
		rela_progress = (RelativeLayout) selfView.findViewById(R.id.rela_progress);
		rela_activity = (RelativeLayout) selfView.findViewById(R.id.rela_activity);
		rela_service = (RelativeLayout) selfView.findViewById(R.id.rela_service);
		rela_setting = (RelativeLayout) selfView.findViewById(R.id.rela_setting);
		tv_emailnum = (TextView) selfView.findViewById(R.id.tv_emailnum);
		rela_userinfo.setOnClickListener(this);
		rela_mail.setOnClickListener(this);
		rela_card.setOnClickListener(this);
		rela_licai.setOnClickListener(this);
		rela_borrow.setOnClickListener(this);
		rela_progress.setOnClickListener(this);
		rela_activity.setOnClickListener(this);
		rela_service.setOnClickListener(this);
		rela_setting.setOnClickListener(this);
		tv_tellnum.setOnClickListener(this);

		if (spf.getString(Constant.USER_NAME, "").equals("")) {
			tv_name.setText("未认证");
		} else {
			tv_name.setText(DataFormatUtil.hideFirstname(spf.getString(Constant.USER_NAME, "")));
		}

		tv_phone.setText(DataFormatUtil.hideMobile(spf.getString(Constant.USER_PHONE, "")) + "");
		tv_bankcardnum.setText(spf.getString(Constant.USER_BANKCARD_NUM, "0") + "张银行卡");
		emailnum = spf.getString(Constant.USER_EMAILNUM, "0");

		tv_emailnum.setText(emailnum + "个邮箱");
		String userface = spf.getString(Constant.USER_FACE, "");
		if (userface == null || userface.equals("")) {
			user_image.setImageResource(R.drawable.img_userface);
		} else {
			byte[] b = Base64.decode(userface);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			user_image.setImageBitmap(bit);
		}
	}

	@Override
	public void onClick(View V) {
		// TODO Auto-generated method stub
		switch (V.getId()) {
		case R.id.rela_userinfo: // 个人中心
			// getSlidingMenu().showMenu();
			Intent relatie_image = new Intent(getActivity(), PersonInfoActivity.class);
			startActivity(relatie_image);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_mail:// 邮箱账单
			Intent rela_mail;
			// 判断邮箱账单 数量
			emailnum = spf.getString(Constant.USER_EMAILNUM, "0");
			if (emailnum.equals("0")) { // 0则跳到 添加邮箱页面
				rela_mail = new Intent(getActivity(), AddMailActivity.class);
				startActivity(rela_mail);
			} else {
				rela_mail = new Intent(getActivity(), MailActivity.class);
				startActivity(rela_mail);
			}
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_card:// 银行卡管理
			Intent Myhandcard = new Intent(getActivity(), MyHandCardActivity.class);
			startActivity(Myhandcard);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_licai: // 我要理财
			Intent intent = new Intent(getActivity(), LiCaiActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_borrow:// 我的借款
			// Intent my_help_repayment = new Intent(getActivity(),
			// HtmlActivity.class);
			// my_help_repayment.putExtra("act", "my_help_repayment");
			// startActivity(my_help_repayment);
			// getActivity().overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);

			Intent my_help_repayment = new Intent(getActivity(), MyLoanActivity.class);
			startActivity(my_help_repayment);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_progress:// 进度中心
			Intent rela_progress = new Intent(getActivity(), ProgressActivity.class);
			startActivity(rela_progress);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_activity:// 活动中心
			Intent rela_activity = new Intent(getActivity(), WebViewActivity.class);
			rela_activity.putExtra("url", JsonConfig.HTML + "/index/activity_center?uid=" + spf.getString(Constant.UID, ""));
			rela_activity.putExtra("title", "活动中心");
			startActivity(rela_activity);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_service:// 服务中心
			Intent rela_service = new Intent(getActivity(), WebViewActivity.class);
			rela_service.putExtra("url", JsonConfig.HTML + "/index/service");
			rela_service.putExtra("title", "服务中心");
			startActivity(rela_service);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_setting:// 设置
			Intent rela_setting = new Intent(getActivity(), SettingActivity.class);
			startActivity(rela_setting);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.tv_tellnum:// 电话拨打
			Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_tellnum.getText().toString()));
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);
			break;
		default:
			break;

		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// case ACTION_LIST:
			// getCardList();
			// getMessagenum();
			// break;
			case GETUSERINFO:
				getUserInfo();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUserInfo();
		// handler.sendEmptyMessage(GETUSERINFO);
		String userface = spf.getString(Constant.USER_FACE, "");
		if (userface == null || userface.equals("")) {
			user_image.setImageResource(R.drawable.img_userface);
		} else {
			byte[] b = Base64.decode(userface);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			user_image.setImageBitmap(bit);
		}
		System.out.println("MenuLeftFragment = onResume = ");
	}

	private void getUserInfo() {
		// dialogHelper.startProgressDialog();
		// dialogHelper.setDialogMessage("正在加载个人资料，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		String uidString = spf.getString(Constant.UID, "");
		String keyString = spf.getString(Constant.KEY, "");
		params.put("sign", HttpUtil.getSign(uidString, keyString));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.GETUSERINFO, params, BaseData.class, null, usersuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					// dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println(s);
						String userface = s.getString("user_face");
						String username = s.getString("user_realname");
						String phone = s.getString("user_phone");
						String idcard = s.getString("user_idcard");
						String bankcard = s.getString("bankcard_num");
						String emailnum = s.getString("mail_num");

						spf.edit().putString(Constant.USER_EMAILNUM, emailnum).commit();
						spf.edit().putString(Constant.USER_FACE, userface).commit();
						spf.edit().putString(Constant.USER_NAME, username).commit();
						spf.edit().putString(Constant.USER_PHONE, phone).commit();
						spf.edit().putString(Constant.USER_CARD, idcard).commit();
						spf.edit().putString(Constant.USER_BANKCARD_NUM, bankcard).commit();

						String userauth = s.getString("is_auth");
						System.out.println("用户 ？？ userauth== " + userauth);
						spf.edit().putString(Constant.USER_AUTH, userauth).commit();
						// System.out.println("userface==" + userface);
						if (userface == null || userface.equals("")) {
							user_image.setImageResource(R.drawable.img_userface);
						} else {
							byte[] face = Base64.decode(userface);
							Bitmap bit = BitmapFactory.decodeByteArray(b, 0, face.length);
							user_image.setImageBitmap(bit);
						}

						// {"user_name":"","is_auth":1,"bankcard_num":2,"user_idcard":"420982199309236437",
						// "is_status":0,"user_phone":"15268170729","is_pay_pwd":1,
						// "user_realname":"叶冰瑞","frozen_money":7000,"user_money":127650}

						if (spf.getString(Constant.USER_NAME, "").equals("")) {
							tv_name.setText("未认证");
						} else {
							tv_name.setText(DataFormatUtil.hideFirstname(spf.getString(Constant.USER_NAME, "")));
						}

						tv_phone.setText(DataFormatUtil.hideMobile(spf.getString(Constant.USER_PHONE, "")));
						tv_bankcardnum.setText(spf.getString(Constant.USER_BANKCARD_NUM, "0") + "张银行卡");

						emailnum = spf.getString(Constant.USER_EMAILNUM, "0");
						tv_emailnum.setText(emailnum + "个邮箱");
						// System.out.println("邮箱数量---emailnum== " + emailnum);
						// // // 刷新首页数据
						// MainActivity mainActivity = (MainActivity)
						// getActivity();
						// mainActivity.getCardList();
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	/**
	 * 调用web服务失败返回数据
	 * 
	 * @return
	 */
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		};
	}

}
