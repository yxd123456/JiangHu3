package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * 进度详情
 * 
 * @author yebr
 * 
 */

public class ProgressDetailActivity extends BaseActivity implements OnClickTitleListener {

	private TitleBar titleBar;
	private SharedPreferences spf;
	private TextView tv_timevalue;
	private int targetid;
	private TextView tv_futimevalue, tv_state, tv_bank, tv_daodate, tv_huandate, tv_proname, tv_refusedata, tv_yihuandate;
	private String descript;
	private int status;
	private ImageView img_state;
	private DialogHelper dialogHelper;
	private LinearLayout linear_seven, linear_eight, linear_nine, linear_yihuan; // 已还款时间
	private TextView tv_jylx;//交易类型
	private TextView tv_lsh;
	private LinearLayout linear_one;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_slidebar_progressdetail);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("进度详情", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		tv_timevalue = (TextView) findViewById(R.id.tv_timevalue);
		tv_futimevalue = (TextView) findViewById(R.id.tv_futimevalue);
		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_bank = (TextView) findViewById(R.id.tv_bank);
		tv_daodate = (TextView) findViewById(R.id.tv_daodate);
		tv_huandate = (TextView) findViewById(R.id.tv_huandate);
		tv_yihuandate = (TextView) findViewById(R.id.tv_yihuandate);
		linear_seven = (LinearLayout) findViewById(R.id.linear_seven);
		linear_eight = (LinearLayout) findViewById(R.id.linear_eight);
		tv_proname = (TextView) findViewById(R.id.tv_proname);
		img_state = (ImageView) findViewById(R.id.img_state);
		// 被拒原因
		tv_refusedata = (TextView) findViewById(R.id.tv_refusedata);
		linear_nine = (LinearLayout) findViewById(R.id.linear_nine);
		linear_yihuan = (LinearLayout) findViewById(R.id.linear_yihuan);
		tv_jylx = (TextView) findViewById(R.id.tv_jiaoyileixing);
		tv_lsh = (TextView) findViewById(R.id.tv_liushuihao);
		
		linear_one = (LinearLayout) findViewById(R.id.linear_one);
		
		targetid = getIntent().getIntExtra("target_id", 0);
		descript = getIntent().getStringExtra("descript");
		// status = getIntent().getIntExtra("status", 0);
		//
		// System.out.println("target_id==" + targetid);
		// System.out.println("status==" + status);
		// // if (status == 0) {
		// //
		// img_state.setBackgroundResource(R.drawable.img_progress_seal_submit);
		// // } else
		// if (status == 0 || status == 1) { // 审核中
		// img_state.setBackgroundResource(R.drawable.img_progress_seal_examine);
		// } else if (status == 2) { // 未通过
		// img_state.setBackgroundResource(R.drawable.img_progress_seal_nopart);
		// } else if (status == 3) {
		// img_state.setBackgroundResource(R.drawable.img_progress_seal_dakuan);
		// }
		// else if (status == 7) { // 已还清
		// img_state.setBackgroundResource(R.drawable.img_progress_seal_finish);
		// }
		// else if (status == 5) { // 已放款
		// img_state.setBackgroundResource(R.drawable.img_progress_seal_dakuan);
		// }
		tv_proname.setText(descript + "");
		getData();
	}

	private void getData() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("target_id", targetid);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.BILLDETAIL, params, BaseData.class, null, progreSuccessListener, errorListener());

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
					System.out.println("进度详情== " + new String(b));
					float helpmoney = s.getFloatValue("help_money");
					String bank = s.getString("card_bank");
					int addtime = s.getIntValue("addtime");
					// int loantime = s.getIntValue("loan_time");
					int helplimit = s.getIntValue("help_limit");
					int takeeffect_time = s.getIntValue("takeeffect_time");
					String cardno = s.getString("card_no");
					String description = s.getString("description"); // 被拒原因
					int repayment_time = s.getIntValue("repayment_time");// 已还款时间
					String title = s.getString("title");
					String loan_order_id = s.getString("loan_order_id");
					tv_jylx.setText(title);
					if(TextUtils.isEmpty(loan_order_id)){
						linear_one.setVisibility(View.GONE);
					} else {
						tv_lsh.setText(loan_order_id+"");
					}					
					int status = s.getIntValue("is_status");// 状态
					System.out.println("进度详情-status == " + status);
					// loanRecord.getIs_status();****************************
					if (status == 0 || status == 1) { // 1/3 审核中
						img_state.setBackgroundResource(R.drawable.img_progress_seal_examine);
					} else if (status == 3) {// 审核通过
						img_state.setBackgroundResource(R.drawable.img_progress_seal_pass);
					} else if (status == 2) {// 未通过
						img_state.setBackgroundResource(R.drawable.img_progress_seal_nopart);
					} else if (status == 4) { // 已放款
						img_state.setBackgroundResource(R.drawable.img_progress_seal_dakuan);
					} else if (status == 5) {// 已还清
						img_state.setBackgroundResource(R.drawable.img_progress_seal_finish);
					}

					if (cardno.equals("")) {
						tv_state.setText("无卡号");
					} else {
						tv_state.setText(cardno.substring(cardno.length() - 4, cardno.length()));
					}
					tv_timevalue.setText(DataFormatUtil.floatsaveTwo(helpmoney));
					tv_futimevalue.setText(bank + "");
					// tv_state.setText(cardno.substring(cardno.length()-4,
					// cardno.length()));
					tv_bank.setText(DateManage.StringToDateymd(String.valueOf(addtime)));// 申请时间

					if (takeeffect_time == 0) {
						linear_seven.setVisibility(View.GONE);
					} else {
						linear_seven.setVisibility(View.VISIBLE);
						tv_daodate.setText(DateManage.StringToDateymd(String.valueOf(takeeffect_time)));// 到账时间
					}
					// tv_daodate.setText(DateManage.StringToDateymd(String.valueOf(loantime)));
					// 应还款时间
					if (takeeffect_time == 0) {
						linear_eight.setVisibility(View.GONE);
					} else {
						linear_eight.setVisibility(View.VISIBLE);
						tv_huandate.setText(DateManage.getAddDate(String.valueOf(takeeffect_time), helplimit - 1));// 到期时间
					}
					// 如果状态是 被拒绝的 ,就显示被拒原因
					if (status == 2) {
						linear_nine.setVisibility(View.VISIBLE);
						tv_refusedata.setText(description);
					} else {
						linear_nine.setVisibility(View.GONE);
					}

					// 已还款时间
					if (repayment_time != 0) {
						linear_yihuan.setVisibility(View.VISIBLE);
						tv_yihuandate.setText(DateManage.StringToDateymd(String.valueOf(repayment_time)));// 还款时间
					} else {
						linear_yihuan.setVisibility(View.GONE);
					}
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
