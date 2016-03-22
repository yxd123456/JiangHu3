package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
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
import com.umeng.socialize.utils.Log;

/**
 * 账单详情
 * 
 * @author yebr
 * 
 */

public class BillDetailActivity extends BaseActivity implements OnClickTitleListener {

	private TitleBar titleBar;
	private SharedPreferences spf;
	private TextView tv_timevalue;
	private int targetid;
	private TextView tv_proname, tv_jyzh, tv_nhsy, tv_lcqx, tv_jyje, tv_jysj, tv_fksj, tv_qrzt, tv_fkmx, tv_xykvalue, tv_jiaoyishijian, tv_fukuanshijian, tv_fukuanmingxi;
	private LinearLayout ll_jyzh, ll_fkmx, ll_nhsy, ll_lcqx, ll_xyk, ll_jysj;
	private String descript;
	private int status;
	private int istype;
	private ImageView img_state;
	private float money;// 交易金额
	private DialogHelper dialogHelper;

	private RelativeLayout rela_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.billdetail_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("账单详情", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		tv_timevalue = (TextView) findViewById(R.id.tv_timevalue);

		targetid = getIntent().getIntExtra("targetid", 0);
		istype = getIntent().getIntExtra("istype", 0);
		descript = getIntent().getStringExtra("description");
		money = getIntent().getFloatExtra("money", 0);

		System.out.println("Type !! == " + istype);

		tv_proname = (TextView) findViewById(R.id.tv_proname);
		img_state = (ImageView) findViewById(R.id.img_state);
		tv_jyzh = (TextView) findViewById(R.id.tv_jyzh);
		tv_nhsy = (TextView) findViewById(R.id.tv_nhsy);
		tv_lcqx = (TextView) findViewById(R.id.tv_lcqx);
		tv_jyje = (TextView) findViewById(R.id.tv_jyje);
		tv_jysj = (TextView) findViewById(R.id.tv_jysj);
		tv_fksj = (TextView) findViewById(R.id.tv_fksj);
		tv_qrzt = (TextView) findViewById(R.id.tv_qrzt);
		tv_fkmx = (TextView) findViewById(R.id.tv_fkmx);
		tv_xykvalue = (TextView) findViewById(R.id.tv_xykvalue);// 信用卡信息

		tv_jiaoyishijian = (TextView) findViewById(R.id.tv_jiaoyishijian);// 交易时间
		tv_fukuanshijian = (TextView) findViewById(R.id.tv_fukuanshijian);// 付款时间

		ll_jysj = (LinearLayout) findViewById(R.id.ll_jysj);// 交易时间view
		ll_jyzh = (LinearLayout) findViewById(R.id.ll_jyzh); // 交易账号
		ll_fkmx = (LinearLayout) findViewById(R.id.ll_fkmx);// 付款明细
		ll_nhsy = (LinearLayout) findViewById(R.id.ll_nhsy);// 年化收益
		ll_lcqx = (LinearLayout) findViewById(R.id.ll_lcqx);// 理财期限

		ll_xyk = (LinearLayout) findViewById(R.id.ll_xyk);// 信用卡

		tv_fukuanmingxi = (TextView) findViewById(R.id.tv_fukuanmingxi); // 付款明细

		rela_content = (RelativeLayout) findViewById(R.id.rela_content);

		if (descript != null) {
			//tv_proname.setText(descript + "");
			if(descript.contains("提现")){
				tv_proname.setText("余额提现");	
			} else if(descript.contains("充值")){
				tv_proname.setText("账户充值");
			} else if(descript.contains("指定")){
				tv_proname.setText("借你钱借款");
			} else if(descript.contains("支付")){
				tv_proname.setText("帮你还借款");
			} else {
				tv_proname.setText(descript + "");
			}
			
		}
		getData();
	}

	private void getData() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("target_id", targetid);
		data.put("is_type", istype);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.USERBILLDETAIL, params, BaseData.class, null, progreSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> progreSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONObject s = JSON.parseObject(new String(b));
					System.out.println("返回数据 = " + new String(b));
					int is_status = s.getIntValue("is_status");

					// 理财账单（ type3 - 投资详情/type4 - 收益详情 ）
					if (istype == 3 || istype == 4) {
						String tran_account = s.getString("tran_account");// 交易账号
						if (null == tran_account) {
							ll_jyzh.setVisibility(View.GONE);
						} else {
							tv_jyzh.setText(tran_account);
						}

						if (s.containsKey("interest")) {
							float interest = s.getFloat("interest");// 年化收益
							tv_nhsy.setText(interest + "%");
						}
						String limit = s.getString("limit");// 投资期限
						if (null == limit) {
							ll_lcqx.setVisibility(View.GONE);
						} else {
							tv_lcqx.setText((Integer.parseInt(limit.replace("天", "")))/30 + "个月");
						}

						tv_jyje.setText(DataFormatUtil.floatsaveTwo(money) + "元");// 交易金额
						if (istype == 4) {
							tv_jiaoyishijian.setText("创建时间:  ");
							tv_fukuanshijian.setText("处理时间:  ");
						}
						// int addtime = s.getIntValue("addtime");
						// tv_jysj.setText(DateManage.StringToDateymd(String.valueOf(addtime))
						// + "");// 交易时间
						// int status_time = s.getIntValue("status_time");//
						// tv_fksj.setText(DateManage.StringToDatehms(String.valueOf(status_time))
						// + "");// 付款时间

						if (is_status == 0) {
							tv_qrzt.setText("已申请");
						} else if (is_status == 1) {
							tv_qrzt.setText("交易成功");
						} else if (is_status == 2) {
							tv_qrzt.setText("交易失败");
						}
						// 付款明细
						int is_payment = s.getIntValue("is_payment");
						if (is_payment == 1) { // 银行卡支付
							String bank = "";
							bank = s.getString("card_bank");
							String cardno = "";
							cardno = s.getString("card_no");
							if ("".equals(bank)) {
								tv_fkmx.setText("无");
							} else {
								tv_fkmx.setText(bank + "(" + DataFormatUtil.bankcardsaveFour(cardno) + ")");
							}
						} else {
							ll_fkmx.setVisibility(View.GONE);
						}
					}

					// 借款账单 （5 借款详情 6 还款详情）
					if (istype == 5 || istype == 6) {
						ll_nhsy.setVisibility(View.GONE);
						ll_lcqx.setVisibility(View.GONE);
						String tran_account = s.getString("tran_account");// 交易账号
						if (null == tran_account) {
							ll_jyzh.setVisibility(View.GONE);
						} else {
							tv_jyzh.setText(tran_account);
						}
						tv_jyje.setText(money + "元");// 交易金额

						if (istype == 6) {// 显示信用卡信息
							ll_jysj.setVisibility(View.GONE);
							String bank = "";
							bank = s.getString("help_card_bank");
							String cardno = "";
							cardno = s.getString("help_card_no");
							if ("".equals(bank)||bank==null) {
								tv_xykvalue.setText("无");
							} else {
								tv_xykvalue.setText(bank + "(" + DataFormatUtil.bankcardsaveFour(cardno) + ")");
							}
							ll_xyk.setVisibility(View.VISIBLE);

							int is_payment = s.getIntValue("is_payment");
							if (is_payment == 1) {
								// 付款明细
								String bank2 = "";
								bank2 = s.getString("card_bank");
								String cardno2 = "";
								cardno2 = s.getString("card_no");
								if (bank2 == null || cardno2 == null) {
									ll_fkmx.setVisibility(View.GONE);
								} else {
									tv_fkmx.setText(bank2 + "(" + DataFormatUtil.bankcardsaveFour(cardno2) + ")");
								}

							} else {
								tv_fkmx.setText("账户余额");
							}
						}
						if (istype == 5) {
							tv_jiaoyishijian.setText("申请时间:  ");
							tv_fukuanshijian.setText("付款时间:  ");
							// 付款明细
							String detail = s.getString("detail");
							if (null == detail) {
								ll_fkmx.setVisibility(View.GONE);
							} else {
								tv_fkmx.setText(detail + "");
							}
						}
						// 确认状态
						if (is_status == 0) {
							tv_qrzt.setText("已申请");
						} else if (is_status == 1) {
							tv_qrzt.setText("还款成功");
						} else if (is_status == 2) {
							tv_qrzt.setText("还款失败");
						}
					}

					// 余额账单
					if (istype == 1 || istype == 2) {
						ll_nhsy.setVisibility(View.GONE);
						ll_lcqx.setVisibility(View.GONE);
						tv_jiaoyishijian.setText("创建时间:  ");
						tv_fukuanshijian.setText("处理时间:  ");
						String tran_account = s.getString("tran_account");// 交易账号
						if (null == tran_account||descript.contains("指定")) {
							ll_jyzh.setVisibility(View.GONE);
						} else {
							tv_jyzh.setText(tran_account);
						}
						tv_jyje.setText(money + "元");// 交易金额

						// 确认状态
						if (is_status == 0) {
							tv_qrzt.setText("已提交");
						} else if (is_status == 1) {
							if (istype == 1) {
								tv_qrzt.setText("充值成功");
							} else {
								tv_qrzt.setText("提现成功");
							}

						} else if (is_status == 2) {
							if (istype == 1) {
								tv_qrzt.setText("提现失败");
							} else {
								tv_qrzt.setText("充值失败");
							}
						}
						String user_realname = s.getString("user_realname");
						String card_bank = s.getString("card_bank");
						String card_no = s.getString("card_no");
						if (istype == 1) {
							tv_fukuanmingxi.setText("充值账户:  ");
						} else if (istype == 2) {
							tv_fukuanmingxi.setText("提现到:");
						}
						if (user_realname == null || card_bank == null || card_no == null) {
							ll_fkmx.setVisibility(View.GONE);
						} else {
							//tv_fkmx.setText(DataFormatUtil.hideFirstname(user_realname) + "  " + card_bank + "(" + DataFormatUtil.bankcardsaveFour(card_no) + ")");
							tv_fkmx.setText(card_bank + "(" + DataFormatUtil.bankcardsaveFour(card_no) + ")");
							//DataFormatUtil.hideFirstname(user_realname) + "  " + 
						}
					}

					int addtime = s.getIntValue("addtime");
					tv_jysj.setText(DateManage.StringToDateymd(String.valueOf(addtime)) + "");// 交易时间
					int status_time = s.getIntValue("status_time");//
					tv_fksj.setText(DateManage.StringToDatehms(String.valueOf(status_time)) + "");// 付款时间

					rela_content.setVisibility(View.VISIBLE);
				}
			} else {
				ToastManage.showToast(response.desc);
			}

		}
	};

	// 调用web服务失败返回数据
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
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

}
