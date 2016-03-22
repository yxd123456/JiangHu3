package com.sptech.qujj.activity;

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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVLoanRecordListAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.model.LoanRecord;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 申请还款
 * 
 * @author yebr
 * 
 */
public class ApplyRepayActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;

	private ImageView img_card;// 银行图标
	private TextView tv_bankcard, tv_realname, tv_cardno, tv_yinhuan, tv_yihuan, tv_weihuan, tv_agreeblue, tv_shoukuan, tv_warning;// 银行，真实姓名，卡号，应还，已还，未还,还款协议

	private RelativeLayout rale_apply, rela_warning, rela_norecord;// 申请页面，申请成功页面，申请失败页面,输入信息错误提示
	private int pro_id = 0; // id

	private DialogHelper dialogHelper;
	private SharedPreferences spf;
	HashMap<String, String> cardMap = new HashMap<String, String>();
	List<LoanRecord> curloanRecords = new ArrayList<LoanRecord>();
	private ListView lv_loanrecord;
	private LVLoanRecordListAdapter loanRecordListAdapter;
	CardInfo curCardInfo;

	private CheckBox cb_agree;// 协议
	private Button btn_sure; // 确定
	private EditText ed_money;// 金额
	float weihuan;// 未还金额

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_home_applyrepay);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("申请代还款", R.drawable.jh_back_selector, 0, "", "注意事项");
		titleBar.setOnClickTitleListener(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		cardMap = DataFormatUtil.getBlueCardMap(spf);

		lv_loanrecord = (ListView) findViewById(R.id.lv_loanrecord);

		pro_id = getIntent().getExtras().getInt("id");
		System.out.println("proid==" + pro_id);

		// rale_apply = (RelativeLayout) findViewById(R.id.rale_apply);
		// rale_applysuccess = (RelativeLayout)
		// findViewById(R.id.rale_applysuccess);
		// rale_nonetwork = (RelativeLayout) findViewById(R.id.rale_nonetwork);
		rela_warning = (RelativeLayout) findViewById(R.id.rela_warning);
		rela_norecord = (RelativeLayout) findViewById(R.id.rela_norecord);

		tv_warning = (TextView) findViewById(R.id.tv_warning);
		img_card = (ImageView) findViewById(R.id.img_card);
		tv_bankcard = (TextView) findViewById(R.id.tv_bankcard);
		tv_realname = (TextView) findViewById(R.id.tv_realname);
		tv_cardno = (TextView) findViewById(R.id.tv_cardno);
		tv_yinhuan = (TextView) findViewById(R.id.tv_yinhuan);
		tv_yihuan = (TextView) findViewById(R.id.tv_yihuan);
		tv_weihuan = (TextView) findViewById(R.id.tv_weihuan);
		tv_agreeblue = (TextView) findViewById(R.id.tv_agreeblue);
		tv_shoukuan = (TextView) findViewById(R.id.tv_shoukuan);

		loanRecordListAdapter = new LVLoanRecordListAdapter(ApplyRepayActivity.this, curloanRecords);
		lv_loanrecord.setAdapter(loanRecordListAdapter);

		tv_agreeblue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent agree = new Intent(ApplyRepayActivity.this, WebViewActivity.class);
				agree.putExtra("url", JsonConfig.HTML + "/index/loan_papers");
				agree.putExtra("title", "趣救急代还款协议");
				startActivity(agree);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		btn_sure = (Button) findViewById(R.id.btn_sure);
		cb_agree = (CheckBox) findViewById(R.id.cb_agree);
		ed_money = (EditText) findViewById(R.id.ed_money);
		// cb_agree.setChecked(true);

		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				View cv = getWindow().getDecorView();
				hideKeyboard(cv);
				if (cb_agree.isChecked()) {
					String edmoney = ed_money.getText().toString().trim();
					if (edmoney != null && !"".equals(edmoney)) {
						float money = Float.parseFloat(edmoney);
						System.out.println("可申请 == " + weihuan);
						System.out.println("输入金额 == " + money);
						if (weihuan == 0) {
							ToastManage.showToast("可申请金额为0");
							return;
						}
						if (money <= 9000) {
							if (1000 <= money && money <= weihuan) {
								rela_warning.setVisibility(View.GONE);
								// 跳到 输入交易密码界面
								Intent repayintent = new Intent(ApplyRepayActivity.this, EnterPwdActivity.class);

								repayintent.putExtra("payflag", 3);
								repayintent.putExtra("id", curCardInfo.getId());
								repayintent.putExtra("bankcard_id", curCardInfo.getBankcard_id());
								repayintent.putExtra("bill_month", curCardInfo.getBill_month());
								repayintent.putExtra("money", money);
								startActivity(repayintent);
								overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							} else if (1000 > money) {
								tv_warning.setText("请输入正确的金额格式和金额范围(1000-9000)");
								rela_warning.setVisibility(View.VISIBLE);
							} else {
								tv_warning.setText("请勿超出实际应还额度");
								rela_warning.setVisibility(View.VISIBLE);
							}

						} else {
							if (1000 <= money && money <= 9000) {
								rela_warning.setVisibility(View.GONE);
							} else {
								tv_warning.setText("请输入正确的金额格式和还款范围(1000-9000)");
								rela_warning.setVisibility(View.VISIBLE);
							}
						}

					} else {
						ToastManage.showToast("请输入金额");
					}

				} else {
					ToastManage.showToast("请勾选代还款协议");
				}
			}
		});

		ed_money.addTextChangedListener(new TextWatcher() {

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

				// 计算 平台收款
				if (start == 0) {
					tv_shoukuan.setText("平台收款 = 0.00");
				} else {
					String text1 = text + "";
					float input_money = Float.parseFloat(text1);
					System.out.println("amount_wei= " + input_money);
					float interest = ((input_money * curCardInfo.getLimit() * (curCardInfo.getInterest() / 100) / curCardInfo.getYeartoday()) * 100) / 100;
					System.out.println("interest= " + interest);
					float service_fee = ((input_money * curCardInfo.getLimit() * (curCardInfo.getService_fee() / 100) / curCardInfo.getYeartoday()) * 100) / 100;
					System.out.println("service_fee= " + service_fee);
					float money = input_money + interest + service_fee + curCardInfo.getService_app();
					System.out.println("money= " + money);
					tv_shoukuan.setText("平台收款 = " + DataFormatUtil.floatsaveTwo(money));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

		applyRepayment();

	}

	private void hideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

		}
	}

	private void initData() {
		String cardname = "";
		cardname = curCardInfo.getCard_bank();
		String cardStream = "";
		cardStream = cardMap.get(cardname);
		if (cardStream == null || "".equals(cardStream)) {
			img_card.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(cardStream);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			img_card.setImageBitmap(bit);
		}

		tv_bankcard.setText(curCardInfo.getCard_bank());
		if (!"".equals(curCardInfo.getCard_realname())) {
			tv_realname.setText(DataFormatUtil.hideFirstname(curCardInfo.getCard_realname()));
		} else {
			tv_realname.setText("");
		}

		if (curCardInfo.getCard_no() != null && !"".equals(curCardInfo.getCard_no())) {
			tv_cardno.setText("  " + DataFormatUtil.bankcardsaveFour(curCardInfo.getCard_no()));
		}
		tv_yinhuan.setText(DataFormatUtil.floatsaveTwo(curCardInfo.getAmount_of()));// 应还
		tv_yihuan.setText(DataFormatUtil.floatsaveTwo(curCardInfo.getAmount_has()));// 已还

		// weihuan = curCardInfo.getAmount_of() - curCardInfo.getAmount_has();
		weihuan = curCardInfo.getAmount_apply();// 可申请金额
		String ea = DataFormatUtil.floatsaveTwo(weihuan) + "";
		ed_money.setText(ea);
		ed_money.setSelection(ea.length());

		// tv_weihuan.setText(DataFormatUtil.floatsaveTwo(curCardInfo.getAmount_of()
		// - curCardInfo.getAmount_has()));// 未还
		tv_weihuan.setText(DataFormatUtil.floatsaveTwo(weihuan));// 可申请金额

		//
		// float input_money = Float.parseFloat(weihuan + "");
		System.out.println("amount_wei= " + weihuan);
		float interest = ((weihuan * curCardInfo.getLimit() * (curCardInfo.getInterest() / 100) / curCardInfo.getYeartoday()) * 100) / 100;
		System.out.println("interest= " + interest);
		float service_fee = ((weihuan * curCardInfo.getLimit() * (curCardInfo.getService_fee() / 100) / curCardInfo.getYeartoday()) * 100) / 100;
		System.out.println("service_fee= " + service_fee);
		float money = weihuan + interest + service_fee + curCardInfo.getService_app();
		System.out.println("money= " + money);
		tv_shoukuan.setText("平台收款 = " + DataFormatUtil.floatsaveTwo(money));

	}

	// 代还款申请
	private void applyRepayment() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", pro_id);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("params" + params);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(ApplyRepayActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.APPLY_REPAYMENT, params, BaseData.class, null, successListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> successListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !"".equals(b)) {
						// JSONObject s = JSON.parseObject(new String(b));
						System.out.println("申请还款= " + new String(b));
						curCardInfo = new CardInfo();
						curCardInfo = JSON.parseObject(new String(b), CardInfo.class);
						if (curCardInfo != null) {
							initData();
						}
						// 代还款记录
						myLoanlist();
					}
					// System.out.println(s);
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 代还款申请记录
	private void myLoanlist() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		// data.put("is_status", "5,6,7,8");
		data.put("is_status", "0,1,2,3,5,7");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("data== " + data);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(ApplyRepayActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.MY_LOAN_LIST, params, BaseData.class, null, loanlistsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> loanlistsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					curloanRecords.clear();
					byte[] b = Base64.decode(response.data);
					if (b != null && !"".equals(b)) {
						System.out.println("还款记录= " + new String(b));
						List<LoanRecord> loanRecords = new ArrayList<LoanRecord>();
						loanRecords = JSON.parseArray(new String(b), LoanRecord.class);
						curloanRecords.addAll(loanRecords);
						loanRecordListAdapter.reset(curloanRecords);
						loanRecordListAdapter.notifyDataSetChanged();
						setListViewHeightBasedOnChildren(lv_loanrecord);
					} else {
						rela_norecord.setVisibility(View.VISIBLE);
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
				dialogHelper.stopProgressDialog();
			}
		};
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated 0. stub
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		// 跳到网页
		Intent rela_service = new Intent(ApplyRepayActivity.this, WebViewActivity.class);
		rela_service.putExtra("url", JsonConfig.HTML + "/index/detail.html?id=17");
		rela_service.putExtra("title", "代还款说明");
		startActivity(rela_service);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

	}

	// listView 动态设置高度
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

}
