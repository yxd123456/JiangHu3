package com.sptech.qujj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 申请借你钱页面
 * 
 * @author LiLin
 * 
 */
public class ApplyToLoadActivity extends BaseActivity implements
		OnClickTitleListener, OnClickListener, OnCheckedChangeListener {
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	TitleBar titleBar;
	ImageView image_jieji_right, image_xinyong_right;
	TextView tv_select_creditcard, tv_load_card, tv_poundage,
			tv_poundage_remind, tv_pro_detail;
	EditText et_tot_load;
	Button btn_commit;
	CheckBox cb_agreepro;
	LinearLayout ll_creditcard, ll_debitcard;
	Boolean IS_CHECK = false;// 默认未勾选协议

	int creditcard_id, debitcard_id, limit;
	String cardName,cardDetail;
	float money;
	final static int REQUESTCODE_CREDITCARD = 1;// 信用卡requestcode
	final static int REQUESTCODE_DEBITCARD = 2;// 借记卡requestcode

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Tools.addActivityList(this);
		setContentView(R.layout.activity_applytoload);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO,
				Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		image_jieji_right = (ImageView) findViewById(R.id.image_jieji_right);// 点击选择信用卡
		image_xinyong_right = (ImageView) findViewById(R.id.image_xinyong_right);// 点击选择借记卡
		tv_select_creditcard = (TextView) findViewById(R.id.tv_select_creditcard);// 选择信用卡
		tv_load_card = (TextView) findViewById(R.id.tv_load_card);// 选择借记卡
		et_tot_load = (EditText) findViewById(R.id.et_tot_load);// 输入金额
		btn_commit = (Button) findViewById(R.id.btn_commit);// 提交按钮
		cb_agreepro = (CheckBox) findViewById(R.id.cb_agreepro);// 同意协议
		tv_poundage = (TextView) findViewById(R.id.tv_poundage);// 手续费
		tv_pro_detail = (TextView) findViewById(R.id.tv_pro_detail);// 协议内容
		tv_poundage_remind = (TextView) findViewById(R.id.tv_poundage_remind);// 手续费提醒
		ll_creditcard = (LinearLayout) findViewById(R.id.ll_select_creditcard);// 选择信用卡
		ll_debitcard = (LinearLayout) findViewById(R.id.ll_select_debitcard);// 选择借记卡
	}

	private void initListener() {
		// TODO Auto-generated method stub
		titleBar.bindTitleContent("申请借你钱", R.drawable.jh_back_selector, 0, "",
				"常见问题");
		titleBar.setOnClickTitleListener(this);
		image_jieji_right.setOnClickListener(this);
		image_xinyong_right.setOnClickListener(this);
		cb_agreepro.setOnCheckedChangeListener(this);
		ll_creditcard.setOnClickListener(this);
		ll_debitcard.setOnClickListener(this);
		tv_poundage_remind.setOnClickListener(this);
		btn_commit.setOnClickListener(this);
		tv_pro_detail.setOnClickListener(this);
		et_tot_load.addTextChangedListener(textWatcher);
	}

	private void initData() {
		// TODO Auto-generated method stub
		tv_poundage_remind.setText("您需在" + "day" + "还款" + et_tot_load.getText()
				+ tv_poundage.getText() + "元（还款金额=本金+手续费）");
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("url", JsonConfig.HTML + "/index/service");
		intent.putExtra("title", "常见问题");
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 选择信用卡
		case R.id.ll_select_creditcard:
			Intent creditcardIntent = new Intent(this,
					SelectBlueCardActivity.class);
			startActivityForResult(creditcardIntent, REQUESTCODE_CREDITCARD);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		// 选择借记卡
		case R.id.ll_select_debitcard:
			Intent debitcardIntent = new Intent(this,
					SelectBankBardActivity.class);
			startActivityForResult(debitcardIntent, REQUESTCODE_DEBITCARD);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		// 提交
		case R.id.btn_commit:
			if(apply()){
				money = Float.parseFloat(et_tot_load.getText().toString());
				Log.e("shuangpeng", "money:"+money+""+"debitcard_id:"+debitcard_id+"creditcard_id:"+creditcard_id);
				Intent apply = new Intent(this, TradingPwdActivity.class);
				apply.putExtra("credit_id", creditcard_id);
				apply.putExtra("debit_id", debitcard_id);
				apply.putExtra("money", money);
				apply.putExtra("cardDetail", cardDetail);
				startActivity(apply);
			}
			
			break;
		case R.id.tv_pro_detail:
			Intent agree = new Intent(this, WebView2Activity.class);
			agree.putExtra("url", JsonConfig.HTML + "/index/user_papers");
			agree.putExtra("title", "趣救急注册协议");
			startActivity(agree);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		default:
			break;
		}
	}

	private boolean apply() {
		Log.e("shuangpeng", creditcard_id + "0:0" + debitcard_id + ":" + money);
		// dialogHelper.startProgressDialog();
		// money = Float.parseFloat(et_tot_load.getText().toString());
		// debitcard_id = Integer.parseInt(tv_load_card.getText().toString());
		// creditcard_id =
		// Integer.valueOf(Md5.md5s(tv_select_creditcard.getText().toString()));
		// creditcard_id
		// =Integer.valueOf(tv_select_creditcard.getText().toString()).intValue();
		if (String.valueOf(tv_select_creditcard.getText()).equals("")
				|| String.valueOf(tv_select_creditcard.getText()) == null 
				|| tv_select_creditcard.getText().equals("必须为已导账单信用卡")) {
			// 信用卡不能为空
			ToastManage.showToast("请选择信用卡");
			return false;
		}
		if (String.valueOf(tv_load_card.getText()).equals("")
				|| String.valueOf(tv_load_card.getText()) == null
				|| tv_load_card.getText().equals("选择已绑定的借记卡")) {
			ToastManage.showToast("请选择借记卡");
			return false;
		}
		if (money > 9000 || money < 1000
				|| et_tot_load.getText().equals("最低1000元，最高10000元")) {
			ToastManage.showToast("申请额度为1000元-10000元");
			return false;
		}
		if (!IS_CHECK) {
			ToastManage.showToast("请勾选注册协议");
			return false;
		}
		return true;
	}


	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean b) {
		// TODO Auto-generated method stub
		IS_CHECK = b;
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			money = Float.parseFloat(et_tot_load.getText().toString());
			initData();
			if (money > 9000
					|| et_tot_load.getText().equals("最低1000元，最高10000元")) {
				ToastManage.showToast("申请额度为1000元-10000元");
				return;
			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_CREDITCARD:
				Bundle b = data.getExtras();
				cardName = b.getString("tv_bank");
				if(b.getString("card_no").length()>4){
					tv_select_creditcard.setText(cardName+"("+b.getString("card_no").substring(b.getString("card_no").length()-4)+")");
				}else{
					tv_select_creditcard.setText(cardName+"("+b.getString("card_no")+")");
				}
				creditcard_id = b.getInt("credit_id");
				cardDetail = cardName+"信用卡"+"("+b.getString("card_no").substring(b.getString("card_no").length()-4)+")";
				break;
			case REQUESTCODE_DEBITCARD:
				Bundle b1 = data.getExtras();
				
				if(b1.getString("card_no").length()>4){
					tv_load_card.setText(b1.getString("tv_bank")+"("+b1.getString("card_no").substring(b1.getString("card_no").length()-4)+")");
				} else {
					tv_load_card.setText(b1.getString("tv_bank")+"("+b1.getString("card_no")+"");
				}
				debitcard_id = b1.getInt("credit_id");
			default:
				break;
			}
		}

	}

}
