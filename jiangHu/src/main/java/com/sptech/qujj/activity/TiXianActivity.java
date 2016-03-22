package com.sptech.qujj.activity;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogSetPwd;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财中心--账户提现
 * 
 * @author yebr
 * 
 */

public class TiXianActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {
	public static int resultOk = 2;
	private int requestcode = 1;
	private TitleBar titleBar;
	DialogHelper dialogHelper;// 加载进度条
	private SharedPreferences spf;

	private List<BankcardBean> curBanklist = new ArrayList<BankcardBean>();
	private float user_money; // 当前卡 可提现金额

	private RelativeLayout rela_card; // 选择储蓄卡
	private BankcardBean defaultcard;// 默认的银行卡
	private int position_select = 0; // 上次选中的 银行卡

	private EditText ed_money; // 提现金额
	private Button btn_next;// 下一步
	private ImageView img_card, img_cardtype;// 银行卡图标，卡类型
	private TextView tv_daozhang, tv_bank, tv_realname, tv_cardno, tv_tixing;// 到账日期，银行名称，真实姓名，卡号

	HashMap<String, String> cardMap = new HashMap<String, String>();
	private float fmoney; // 充值money
	private DialogSetPwd dsp;
	private ImageView img_clear; // input 清除按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_tixian);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("账户提现", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);

		rela_card = (RelativeLayout) findViewById(R.id.rela_card);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		rela_card.setOnClickListener(this);

		btn_next = (Button) findViewById(R.id.btn_next); // 下一步
		ed_money = (EditText) findViewById(R.id.ed_money); // 充值金额

		img_card = (ImageView) findViewById(R.id.img_card); // 银行卡图标
		img_cardtype = (ImageView) findViewById(R.id.img_cardtype); // 卡类型

		tv_bank = (TextView) findViewById(R.id.tv_bank);// 银行卡
		tv_realname = (TextView) findViewById(R.id.tv_realname);// 真实姓名
		tv_cardno = (TextView) findViewById(R.id.tv_cardno);// 卡号
		tv_daozhang = (TextView) findViewById(R.id.tv_daozhang);
		tv_tixing = (TextView) findViewById(R.id.tv_tixing);
		cardMap = DataFormatUtil.getCardMap(spf);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear.setOnClickListener(this);
		ed_money.addTextChangedListener(textWatcher);
		initBankCard();
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence text, int start, int before, int count) {
			// TODO Auto-generated method stub
			if (text.toString().contains(".")) {
				if (text.length() - 1 - text.toString().indexOf(".") > 2) {
					text = text.toString().subSequence(0, text.toString().indexOf(".") + 3);
					ed_money.setText(text);
					ed_money.setSelection(text.length());
				}
			}
			if (text.toString().trim().substring(0).equals(".")) {
				text = "0" + text;
				ed_money.setText(text);
				ed_money.setSelection(2);
			}

			if (text.toString().startsWith("0") && text.toString().trim().length() > 1) {
				if (!text.toString().substring(1, 2).equals(".")) {
					ed_money.setText(text.subSequence(0, 1));
					ed_money.setSelection(1);
					return;
				}
			}

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if (ed_money.getText().toString() != null && !ed_money.getText().toString().equals("")) {
				img_clear.setVisibility(View.VISIBLE);
			} else {
				img_clear.setVisibility(View.INVISIBLE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_clear:
			ed_money.setText("");
			break;
		case R.id.rela_card:
			Intent intent = new Intent(this, SelectCardActivity.class);
			intent.putExtra("backflag", 2);
			intent.putExtra("position_select", position_select);
			System.out.println("position_select== " + position_select);
			intent.putExtra("bankcardlist", (Serializable) curBanklist);
			startActivityForResult(intent, requestcode);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_next:
			// bindid 获取校验码
			// 验证充值金额
			String money = ed_money.getText().toString();
			if (user_money == 0) {
				ToastManage.showToast("该卡可提现金额为0");
				return;
			}

			if (!money.equals("")) {
				fmoney = Float.parseFloat(money);// 输入的支付金额；
				if (fmoney != user_money && user_money < 100) {
					ToastManage.showToast("100元以下需一次性提完");
					return;
				}
				if (fmoney > user_money) {
					ToastManage.showToast("超出可提取金额!");
					return;
				} else if (fmoney <= 0) {
					ToastManage.showToast("提取金额必须大于0!");
					return;
				}
				userCashout();
			} else {
				ToastManage.showToast("请输入提取金额!");
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			// TODO
			if (resultCode == resultOk) {
				defaultcard = new BankcardBean();
				System.out.println("bankcard == ");
				defaultcard = (BankcardBean) data.getSerializableExtra("bankcard");
				position_select = data.getIntExtra("position", 0);
				System.out.println("传过来没？？？=== " + position_select);
				initViewData();
			}
			break;
		default:
			break;
		}
	}

	// 初始化默认的充值的银行卡
	private void initViewData() {
		if (defaultcard != null) {
			tv_bank.setText(defaultcard.getCard_bank());
			tv_realname.setText(DataFormatUtil.hideFirstname(defaultcard.getCard_realname()));
			tv_cardno.setText(DataFormatUtil.bankcardsaveFour(defaultcard.getCard_no()));
			img_cardtype.setBackgroundResource(R.drawable.jh_licai_bank);
			long day = 1;
			try {
				tv_daozhang.setText("到账日期 " + DateManage.addDay(day));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String cardname = defaultcard.getCard_bank();
			String cardStream = cardMap.get(cardname);
			System.out.println("card--name==" + cardname);
			System.out.println("card--img==" + cardStream);

			if (cardStream == null || cardStream.equals("")) {
				img_card.setImageResource(R.drawable.img_nobank);
			} else {
				byte[] b = Base64.decode(cardStream);
				Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
				img_card.setImageBitmap(bit);
			}

			float keti = defaultcard.getOut_money_actual();
			// keti = (float) 99.8;
			ed_money.setHint("可提现" + DataFormatUtil.floatsaveTwo(keti));
			user_money = keti;
		} else {
			ToastManage.showToast("您还没有绑定银行卡");
		}
	}

	// 初始化默认的银行卡
	private void initBankCard() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		// HashMap<String, Object> data = new HashMap<String, Object>();
		// data.put("card_type", 1);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		// params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(TiXianActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.START_CASHOUT, params, BaseData.class, null, cardsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> cardsuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						curBanklist.clear();
						// JSONArray s = JSON.parseArray(new String(b));
						JSONObject s = JSON.parseObject(new String(b));
						String conString = s.getString("bank_list");
						System.out.println("返回数据" + conString);
						List<BankcardBean> banklist = JSON.parseArray(conString, BankcardBean.class);
						curBanklist.addAll(banklist);
						if (banklist.size() != 0) {
							// 遍历这个banklist
							for (int i = 0; i < banklist.size(); i++) {
								if (banklist.get(i).getIs_cashout() == 1) {
									defaultcard = banklist.get(i);
								}
							}
							if (defaultcard == null) {
								defaultcard = banklist.get(0);
							}
						}
						initViewData();
					}
				} else {
					dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}

		};
	}

	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	// 用户提现 -提交
	@SuppressWarnings("unused")
	private void userCashout() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("money", fmoney);
		if (defaultcard != null) {
			data.put("bankcard_id", defaultcard.getBankcard_id());
		}
		System.out.println("参数 --= " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(TiXianActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_CASHOUT, params, BaseData.class, null, usercashoutsuccessListener(), cashouterrorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usercashoutsuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				// 再判断是否有设置交易密码
				if (response.code.equals("1038")) { // 1038
					// 弹出设置交易密码框
					int goflag = 2;
					dsp = new DialogSetPwd(TiXianActivity.this, goflag, 0, 0);
					dsp.createMyDialog();

				} else if (response.code.equals("0")) {

					byte[] b = Base64.decode(response.data);
					JSONObject s = JSON.parseObject(new String(b));
					System.out.println("用户充值 --" + s);
					String bindid = s.getString("bindid");
					if (!bindid.equals("")) {
						// 输入密码
						Intent intent = new Intent(TiXianActivity.this, EnterPwdActivity.class);
						intent.putExtra("payflag", 2);
						intent.putExtra("bankcard_id", defaultcard.getBankcard_id());
						intent.putExtra("money", fmoney);
						intent.putExtra("bindid", bindid);
						System.out.println("充值传值---bindid= " + bindid);
						// System.out.println("充值传值---bindid= " +bindid );
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
	private Response.ErrorListener cashouterrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	// // 账户余额
	// private void getHttpInfo() {
	// dialogHelper.startProgressDialog();
	// dialogHelper.setDialogMessage("请稍候...");
	// HashMap<String, String> params = new HashMap<String, String>();
	// params.put("uid", spf.getString(Constant.UID, ""));
	// params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, ""),
	// spf.getString(Constant.KEY, "")));
	// HttpVolleyRequest<BaseData> request = new
	// HttpVolleyRequest<BaseData>(this, false);
	// request.HttpVolleyRequestPost(JsonConfig.ACCOUNTMONEY, params,
	// BaseData.class, null, userbalancesuccessListener(),
	// balanceerrorListener());
	// }
	//
	// @SuppressWarnings("rawtypes")
	// private Listener<BaseData> userbalancesuccessListener() {
	// return new Listener<BaseData>() {
	//
	// @Override
	// public void onResponse(BaseData response) {
	// System.out.println("余额 返回code == " + response.code);
	// dialogHelper.stopProgressDialog();
	// if (response.code.equals("0")) {
	// byte[] b = Base64.decode(response.data);
	// if (b != null && !b.equals("")) {
	// JSONObject s = JSON.parseObject(new String(b));
	// // user_money = s.getFloat("user_money");
	// System.out.println("ACCOUNTMONEY 数据=" + s);
	// ed_money.setHint("可提取" + DataFormatUtil.floatsaveTwo(user_money));
	// }
	// } else {
	// ToastManage.showToast(response.desc);
	// }
	// }
	// };
	// }

	/**
	 * 调用web服务失败返回数据
	 * 
	 * @return
	 */
	@SuppressLint("ShowToast")
	private Response.ErrorListener balanceerrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

}
