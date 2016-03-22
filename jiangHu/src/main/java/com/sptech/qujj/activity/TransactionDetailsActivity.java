package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVFinancialAssetsAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.BuyPronumber;
import com.sptech.qujj.model.Product;
import com.sptech.qujj.model.TransactionDetail;
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
 * 交易详情
 * 
 * @author yebr
 * 
 */
public class TransactionDetailsActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private List<BuyPronumber> bList = new ArrayList<BuyPronumber>();

	private LVFinancialAssetsAdapter lvFinancialAssetsAdapter; // 理财资产adapter
	private RelativeLayout rela_content;
	// private ListView listView;
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	private String order_no = "";// 订单编号
	private String subject = "";// 产品名称
	private TransactionDetail curtransactionDetail; // 交易详情
	private Product curproduct;// 传进来的产品
	private LinearLayout linear_one;// 交易账号
	private TextView tv_proname, tv_novalue, tv_buyvalue, tv_timevalue, tv_futimevalue, tv_state, tv_payinfo;// 项目名称，交易账号，申购金额，交易时间，付款时间，确认状态，支付信息；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_transactiondetail);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		if (getIntent() != null) {
			// order_no = getIntent().getStringExtra("order_no");
			// subject = getIntent().getStringExtra("subject");
			curproduct = (Product) getIntent().getSerializableExtra("product");
		}
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		linear_one = (LinearLayout) findViewById(R.id.linear_one);
		rela_content = (RelativeLayout) findViewById(R.id.rela_content);
		tv_proname = (TextView) findViewById(R.id.tv_proname);
		tv_novalue = (TextView) findViewById(R.id.tv_novalue);
		tv_buyvalue = (TextView) findViewById(R.id.tv_buyvalue);
		tv_timevalue = (TextView) findViewById(R.id.tv_timevalue);
		tv_futimevalue = (TextView) findViewById(R.id.tv_futimevalue);

		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_payinfo = (TextView) findViewById(R.id.tv_payinfo);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("交易详情", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		if (curproduct != null) {
			if (curproduct.getIs_payment() == 1) {
				getUserasserDetail();
				// buy_number * buy_money
			} else {
				initCurData();
			}
		}
	}

	private void initCurData() {
		rela_content.setVisibility(View.VISIBLE);
		tv_proname.setText(curproduct.getSubject());
		// 交易账号
		linear_one.setVisibility(View.GONE);
		// tv_novalue.setText(curtransactionDetail.getOrder_id());

		tv_buyvalue.setText(DataFormatUtil.floatsaveTwo(curproduct.getBuy_number() * curproduct.getBuy_money()) + "元");
		// 交易时间
		tv_timevalue.setText(DateManage.StringToDatehms(curproduct.getAddtime() + ""));
		// 付款时间
		tv_futimevalue.setText(DateManage.StringToDatehms(curproduct.getAddtime() + ""));
		tv_state.setText("交易成功");
		tv_payinfo.setText("余额支付");
	}

	private void initViewData() {
		// TODO Auto-generated method stub
		tv_proname.setText(curproduct.getSubject());
		tv_novalue.setText(curtransactionDetail.getOrder_id());
		// tv_buyvalue.setText(DataFormatUtil.floatsaveTwo(curtransactionDetail.getMoney())
		// + "元");
		tv_buyvalue.setText(DataFormatUtil.floatsaveTwo(curproduct.getBuy_number() * curproduct.getBuy_money()) + "元");
		tv_timevalue.setText(DateManage.StringToDateymd(curtransactionDetail.getAddtime() + ""));
		tv_futimevalue.setText(DateManage.StringToDatehms(curtransactionDetail.getQuery_time()));

		if (curtransactionDetail.getIs_status() == 0) {
			tv_state.setText("已接受");
		} else if (curtransactionDetail.getIs_status() == 1) {
			tv_state.setText("处理中");
		} else if (curtransactionDetail.getIs_status() == 2) {
			tv_state.setText("交易成功");
		} else if (curtransactionDetail.getIs_status() == 3) {
			tv_state.setText("交易失败");
		}
		// else if (curtransactionDetail.getIs_status() == 3) {
		// tv_state.setText("请求失败");
		// }
		String card_no = curtransactionDetail.getCard_no();
		if (card_no != null && !card_no.equals("")) {
			String card_four = card_no.substring(card_no.length() - 4, card_no.length());
			tv_payinfo.setText(curtransactionDetail.getCard_bank() + "(" + card_four + ")");
		}
	}

	// 交易详情
	private void getUserasserDetail() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("order_no", curproduct.getOrder_no());
		System.out.println("order_no==" + curproduct.getOrder_no());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_ASSET_DETAIL, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("交易详情 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("交易详情 数据=" + s);
						TransactionDetail transactionDetail = new TransactionDetail();
						transactionDetail = JSON.parseObject(new String(b), TransactionDetail.class);
						curtransactionDetail = transactionDetail;
						initViewData();
						rela_content.setVisibility(View.VISIBLE);
					} else {
						rela_content.setVisibility(View.GONE);
						ToastManage.showToast("空数据");
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// "card_bank":"建设银行","addtime":1440414654,"query_time":"1440414654","is_status":3,
	// "money":2000,"card_no":"6227002872870362715","order_id":"1508241910546448"

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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.rela_balance:
		// Intent intent =new Intent(this,AccountDetailActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		// break;

		default:
			break;
		}

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
