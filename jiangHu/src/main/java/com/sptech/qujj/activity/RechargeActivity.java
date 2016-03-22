package com.sptech.qujj.activity;

import java.io.Serializable;
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
import android.widget.CheckBox;
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
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财中心--账户充值
 * 
 * @author yebr
 * 
 */

public class RechargeActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	public static int resultOk = 1;
	private int requestcode = 0;
	private TitleBar titleBar;
	private EditText ed_money; // 充值金额
	private Button btn_next;// 下一步
	DialogHelper dialogHelper;// 加载进度条

	private RelativeLayout rela_card; // 选择储蓄卡
	public static int position_select = 0; // 上次选中的 银行卡

	private CheckBox cb_agree; // 是否同意 协议 checkbox
	private SharedPreferences spf;
	private float fmoney; // 充值money
	private BankcardBean defaultcard;// 默认的银行卡
	private List<BankcardBean> curBanklist = new ArrayList<BankcardBean>();//
	// private List<NewBankcardBean> curBanklist = new
	// ArrayList<NewBankcardBean>();//

	private ImageView img_card, img_cardtype;// 银行卡图标，卡类型
	private TextView tv_bank, tv_realname, tv_cardno, tv_agreeblue, tv_xiane;// 银行名称，真实姓名，卡号,
	                                                                         // 限额说明
	HashMap<String, String> cardMap = new HashMap<String, String>();
	private DialogSetPwd dsp;
	private ImageView img_clear; // input 清除按钮
	private TextView tv_danbi, tv_cishu, tv_edu;// 单笔上限，月交易次数，当日额度

	float money_day_total; // 单日累计充值金额
	float money_day_quota;// 单日限额
	float money_day_once; // 单笔限额
	int number_month_quota;// 月交易笔数限制
	int number_month_total;// 月累计交易笔数
	float money_later; // 当日交易剩余额度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_recharge);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		dialogHelper = new DialogHelper(this);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("账户充值", R.drawable.jh_back_selector, 0, "", "限额说明");
		titleBar.setOnClickTitleListener(this);

		rela_card = (RelativeLayout) findViewById(R.id.rela_card);
		ed_money = (EditText) findViewById(R.id.ed_money); // 充值金额
		btn_next = (Button) findViewById(R.id.btn_next); // 下一步
		cb_agree = (CheckBox) findViewById(R.id.cb_agree);

		img_card = (ImageView) findViewById(R.id.img_card); // 银行卡图标
		img_cardtype = (ImageView) findViewById(R.id.img_cardtype); // 卡类型
		img_clear = (ImageView) findViewById(R.id.img_clear);

		ed_money.addTextChangedListener(textWatcher);

		tv_bank = (TextView) findViewById(R.id.tv_bank);// 银行卡
		tv_realname = (TextView) findViewById(R.id.tv_realname);// 真实姓名
		tv_cardno = (TextView) findViewById(R.id.tv_cardno);// 卡号

		tv_agreeblue = (TextView) findViewById(R.id.tv_agreeblue);// 充值服务协议
		// tv_xiane = (TextView) findViewById(R.id.tv_xiane);// 限额说明

		tv_danbi = (TextView) findViewById(R.id.tv_danbi);
		tv_cishu = (TextView) findViewById(R.id.tv_cishu);
		tv_edu = (TextView) findViewById(R.id.tv_edu);

		// tv_xiane.setOnClickListener(this);
		tv_agreeblue.setOnClickListener(this);
		rela_card.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		img_clear.setOnClickListener(this);

		cardMap = DataFormatUtil.getCardMap(spf);
		initBankCard();
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
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

	// 初始化默认的充值的银行卡
	private void initViewData() {
		// img_card.setima
		if (defaultcard != null) {
			tv_bank.setText(defaultcard.getCard_bank());
			tv_realname.setText(DataFormatUtil.hideFirstname(defaultcard.getCard_realname()));
			tv_cardno.setText(DataFormatUtil.bankcardsaveFour(defaultcard.getCard_no()));
			img_cardtype.setBackgroundResource(R.drawable.jh_licai_bank);

			String cardname = defaultcard.getCard_bank();
			String cardStream = cardMap.get(cardname);
			System.out.println("card--name==" + cardname);
			// System.out.println("card--img==" + cardStream);

			if (cardStream == null || cardStream.equals("")) {
				img_card.setImageResource(R.drawable.img_nobank);
			} else {
				byte[] b = Base64.decode(cardStream);
				Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
				img_card.setImageBitmap(bit);
			}

			money_day_total = defaultcard.getMoney_day_total();// 单日累计充值金额
			money_day_quota = defaultcard.getMoney_day_quota();// 单日限额
			money_day_once = defaultcard.getMoney_day_once();// 单笔限额
			number_month_quota = defaultcard.getNumber_month_quota();// 月交易笔数限制
			money_later = money_day_quota - money_day_total;// 当日交易剩余额度
			number_month_total = defaultcard.getNumber_month_total();// 单月累计笔数

			System.out.println("money_day_total== " + money_day_total);
			System.out.println("money_day_once== " + money_day_once);
			System.out.println("number_month_quota== " + number_month_quota);
			System.out.println("money_later== " + money_later);
			System.out.println("number_month_total== " + number_month_total);

			if (money_day_once != 0) {
				tv_danbi.setText("该卡单笔交易上限为 " + DataFormatUtil.floatsaveTwo(money_day_once) + " 元");
			} else {
				tv_danbi.setText("该卡单笔交易无上限");
			}

			if (money_day_quota == 0) {
				tv_edu.setText("该卡单日交易额度无上限");
			} else {
				tv_edu.setText("您当日交易额度剩余 " + DataFormatUtil.floatsaveTwo(money_later) + " 元");
			}

			if (number_month_quota == 0) {
				tv_cishu.setText("该卡本月交易次数无上限");
			} else {
				// tv_cishu.setText("该卡本月交易次数上限为" + number_month_quota + "次");
				int leter = number_month_quota - number_month_total;
				tv_cishu.setText("该卡本月交易次数剩余 " + leter + " 次");
			}

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
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(RechargeActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.START_RECHARGE, params, BaseData.class, null, cardsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> cardsuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					// defaultcard = new BankcardBean();
					dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						curBanklist.clear();
						System.out.println("返回数据" + new String(b));
						List<BankcardBean> banklist = JSON.parseArray(new String(b), BankcardBean.class);
						// curBanklist = banklist;
						curBanklist.addAll(banklist);
						if (banklist.size() != 0) {
							// 遍历这个banklist
							for (int i = 0; i < banklist.size(); i++) {
								if (banklist.get(i).getIs_default() == 1) {
									defaultcard = banklist.get(i);
								}
								System.out.println("充值卡 name == " + banklist.get(i).getCard_bank());
								System.out.println("充值卡 money_once == " + banklist.get(i).getMoney_day_once());
							}
							if (defaultcard == null) {
								defaultcard = banklist.get(0);
							}
						}
						initViewData();
					} else {
						ToastManage.showToast(response.desc);
					}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_clear:
			ed_money.setText("");
			break;
		case R.id.rela_card:
			if (curBanklist != null && curBanklist.size() != 0) {
				Intent intent = new Intent(this, SelectCardActivity.class);
				intent.putExtra("backflag", 1);
				intent.putExtra("position_select", position_select);
				intent.putExtra("bankcardlist", (Serializable) curBanklist);
				startActivityForResult(intent, requestcode);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				ToastManage.showToast("获取银行卡异常");
			}
			break;
		case R.id.btn_next:
			String money = ed_money.getText().toString();
			if (!money.equals("")) {
				fmoney = Float.parseFloat(money);// 输入的支付金额；
				if (fmoney < 100) {
					ToastManage.showToast("充值金额必须大于100");
					return;
				}
			} else {
				ToastManage.showToast("请输入充值金额!");
				return;
			}
			if (!cb_agree.isChecked()) {
				ToastManage.showToast("请勾选支付协议");
				return;
			}

			// 月交易次数上限
			if ((number_month_total + 1) > number_month_quota && number_month_quota != 0) {
				ToastManage.showToast("您的月交易次数达到上限");
				return;
			}

			// 单笔限额
			if (fmoney > money_day_once && money_day_once != 0) {
				ToastManage.showToast("您的单笔交易限额达到上限");
				return;
			}
			// 单日交易金额上限
			float month_total = fmoney + money_day_total;
			if (month_total > money_day_quota && money_day_quota != 0) {
				ToastManage.showToast("您的单日交易限额达到上限");
				return;
			}
			userRecharge();
			break;
		case R.id.tv_agreeblue:
			Intent agree = new Intent(RechargeActivity.this, WebViewActivity.class);
			agree.putExtra("url", JsonConfig.HTML + "/index/pay_papers");
			agree.putExtra("title", "趣救急支付协议");
			startActivity(agree);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		// case R.id.tv_xiane:
		// Intent xiane = new Intent(RechargeActivity.this,
		// WebViewActivity.class);
		// xiane.putExtra("url", JsonConfig.HTML +
		// "/index/detail.html?id=23");//
		// http://qu.99ji.cn/index/detail.html?id=23
		// xiane.putExtra("title", "限额说明");
		// startActivity(xiane);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		// break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			// TODO
			if (resultCode == resultOk) {
				defaultcard = new BankcardBean();
				defaultcard = (BankcardBean) data.getSerializableExtra("bankcard");
				position_select = data.getIntExtra("position", 0);
				System.out.println("传过来没？？？=== " + position_select);
				// 根据name 获取图标流
				initViewData();
			}
			break;
		default:
			break;
		}
	}

	// 用户充值 -提交
	@SuppressWarnings("unused")
	private void userRecharge() {
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

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(RechargeActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_RECHARGE, params, BaseData.class, null, userrechargesuccessListener(), rechargeerrorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> userrechargesuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {

				// 再判断是否有设置交易密码
				if (response.code.equals("1038")) { // 1038
					// 弹出设置交易密码框
					dialogHelper.stopProgressDialog();
					int goflag = 2;
					dsp = new DialogSetPwd(RechargeActivity.this, goflag, 0, 0);
					dsp.createMyDialog();
				} else if (response.code.equals("0")) {
					dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("用户充值 --" + s);
						String bindid = s.getString("bindid");
						if (!bindid.equals("")) {
							// 输入密码
							Intent intent = new Intent(RechargeActivity.this, EnterPwdActivity.class);
							intent.putExtra("payflag", 1);
							intent.putExtra("bankcard_id", defaultcard.getBankcard_id());
							intent.putExtra("money", fmoney);
							intent.putExtra("bindid", bindid);
							System.out.println("充值传值---bindid= " + bindid);
							// System.out.println("充值传值---bindid= " +bindid );
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						}
					}
				} else {
					dialogHelper.stopProgressDialog();
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
	private Response.ErrorListener rechargeerrorListener() {
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
		// Intent intent = new Intent(this, RechargeDetailActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		Intent xiane = new Intent(RechargeActivity.this, WebViewActivity.class);
		xiane.putExtra("url", JsonConfig.HTML + "/index/detail.html?id=23");// http://qu.99ji.cn/index/detail.html?id=23
		xiane.putExtra("title", "限额说明");
		startActivity(xiane);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

}
