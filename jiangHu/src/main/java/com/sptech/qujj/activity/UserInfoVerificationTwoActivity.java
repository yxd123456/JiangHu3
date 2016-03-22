package com.sptech.qujj.activity;

import java.io.ByteArrayOutputStream;
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
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Md5;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 用户信息验证-第二步
 * 
 * @author yebr
 * 
 */
public class UserInfoVerificationTwoActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private Button btn_finish;
	private SharedPreferences spf;
	private String name, cardid, bindid, carNo, phone, cardname;
	private EditText ed_card, ed_phone, ed_verify;
	private TextView btnGetCode, et_banktype;
	private DownTimer downTimer;
	private DialogSetPwd dsp;
	private DialogHelper dialogHelper;
	private String bankkey, bankid, stream, verify;
	private ImageView iv_banklogo;
	private int requestcode = 1;
	public static int resultOk = 5;
	private int nextflag = 0; // 实名认证成功，点击下次看看，跳转的flag
	private RelativeLayout rl_banktype;
	private ImageView img_clear, img_clear_two, img_clear_three; // input 清除按钮
	private int pro_id = 0; // 产品id
	
	/*private Bitmap bitmap1, bitmap2, bitmap3;
	private byte[] byte1, byte2, byte3;*/

	private boolean decode = true;
	private List<UsablebankBean> blist = new ArrayList<UsablebankBean>();

	// nextflag ! 点击下次先 ，分情况: 0 表示 实名认证的时候 -- 回到首页;
	// 1 表示 交易的时候--返回到交易界面;
	// 2 表示 充值 提现的时候,跳到账户余额 页面
	// 3 在个人中心点击实名认证，完成后跳回个人中心
	// 4在手动申请界面，完成实名认证后跳转到手动申请界面
	// 5在添加银行卡界面，完成实名认证后跳转到添加银行卡界面

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_userinfo_verifivation_two);
		initView();
		Tools.addActivityList(this);
	}
	
	


	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		ed_card = (EditText) findViewById(R.id.ed_card);
		btnGetCode = (TextView) findViewById(R.id.tv_sendcode);
		downTimer = new DownTimer(60 * 1000, 1000);
		ed_phone = (EditText) findViewById(R.id.ed_phone);
		iv_banklogo = (ImageView) findViewById(R.id.iv_banklogo);
		et_banktype = (TextView) findViewById(R.id.et_banktype);
		rl_banktype = (RelativeLayout) findViewById(R.id.rl_banktype);
		ed_verify = (EditText) findViewById(R.id.ed_verify);
		btnGetCode.setOnClickListener(this);
		nextflag = getIntent().getIntExtra("nextflag", 0);
		name = getIntent().getStringExtra("realname");
		bindid = getIntent().getStringExtra("bindid");
		cardid = getIntent().getStringExtra("cardid");
		/*bitmap1 = getIntent().getParcelableExtra("bitmap");
		bitmap2 = getIntent().getParcelableExtra("bitmap");
		bitmap3 = getIntent().getParcelableExtra("bitmap");
		byte1 = BitmapUtil.getBitmapByte(bitmap1);
		byte2 = BitmapUtil.getBitmapByte(bitmap2);
		byte3 = BitmapUtil.getBitmapByte(bitmap3);*/
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		if (nextflag == 1) {
			pro_id = getIntent().getExtras().getInt("id");
		}
		titleBar.bindTitleContent("用户信息验证", R.drawable.jh_back_selector, 0, "", "");
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar.setOnClickTitleListener(this);
		btn_finish.setOnClickListener(this);
		rl_banktype.setOnClickListener(this);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear_three = (ImageView) findViewById(R.id.img_clear_three);

		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);
		img_clear_three.setOnClickListener(this);

		ed_card.addTextChangedListener(textWatcherone);
		ed_phone.addTextChangedListener(textWatchertwo);
		ed_verify.addTextChangedListener(textWatcherthree);

		ed_card.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					if (ed_card.getText().toString() != null && !ed_card.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		ed_phone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					if (ed_phone.getText().toString() != null && !ed_phone.getText().toString().equals("")) {
						img_clear_two.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		ed_verify.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					img_clear_two.setVisibility(View.INVISIBLE);
					if (ed_verify.getText().toString() != null && !ed_verify.getText().toString().equals("")) {
						img_clear_three.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_sendcode: // 发送验证码
			phone = ed_phone.getText().toString().trim();
			if (phone.equals("")) {
				Toast.makeText(UserInfoVerificationTwoActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CheckUtil.checkMobile(phone)) {
				Toast.makeText(UserInfoVerificationTwoActivity.this, "输入手机号格式有误", Toast.LENGTH_SHORT).show();
				return;
			}
			getkeyCode();
			break;
		case R.id.btn_finish:
			realnameverify();
			break;
		case R.id.rl_banktype:
			Intent banktype = new Intent(UserInfoVerificationTwoActivity.this, UsableBankActivity.class);
			if (decode) {
				doDecode();
			}
			if (blist != null || blist.size() == 0) {
				banktype.putExtra("blist", (Serializable) blist);
			}
			// Java代码 putExtras(key, (Serializable)list)
			// 方法传递过去，接受的时候用
			// Java代码 (List<YourObject>) getIntent().getSerializable(key)
			startActivityForResult(banktype, requestcode);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			decode = false;
			break;
		case R.id.img_clear:
			ed_card.setText("");
			break;
		case R.id.img_clear_two:
			ed_phone.setText("");
			break;
		case R.id.img_clear_three:
			ed_verify.setText("");
			break;
		default:
			break;
		}

	}

	// 解析bankcard
	private void doDecode() {
		String supportbank = spf.getString(Constant.SUPPORTBANK, "");
		System.out.println("doDecode()--supportbank 解析储蓄卡列表 == " + supportbank);
		List<UsablebankBean> buylist = JSON.parseArray(supportbank, UsablebankBean.class);
		blist = buylist;
	}

	private void realnameverify() {
		carNo = ed_card.getText().toString().trim();
		phone = ed_phone.getText().toString().trim();
		verify = ed_verify.getText().toString().trim();
		if (carNo == null || "".equals(carNo)) {
			ToastManage.showToast("银行卡号不能为空");
			return;
		}
		if (phone == null || "".equals(phone)) {
			ToastManage.showToast("手机号不能为空");
			return;
		}
		if (verify == null || "".equals(verify)) {
			ToastManage.showToast("验证码不能为空");
			return;
		}
		if (cardname == null || "".equals(cardname)) {
			ToastManage.showToast("请选择开户行");
			return;
		}
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_bank", cardname);
		data.put("card_phone", phone);
		data.put("code", verify);
		data.put("bankid", bankid);
		data.put("bank_key", bankkey);
		data.put("card_no", carNo);
		data.put("user_realname", name);
		data.put("user_idcard", cardid);

		String json = JSON.toJSONString(data);
		String base = Base64.encode(json.getBytes());
		dialogHelper.startProgressDialog();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", Md5.md5s(base + spf.getString(Constant.UID, "") + spf.getString(Constant.KEY, "").toUpperCase()));
//		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.REALNAMEAUTHEN, params, BaseData.class, null, userSuccessListener, errorListener());
		//------------------------------------------------------------
		/**
		 * 当用户成功提交所有信息之后，设置实名认证的标识为true
		 */
		Constant.FLAG_SHIMINGRENZHENG = true;
		//-------------------------------------------------------------
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> userSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				setdealpwd();
				// System.out.println("实名认证成功");
				// Intent in = new Intent(UserInfoVerificationTwoActivity.this,
				// MainActivity.class);
				// startActivity(in);
				// overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
			} else {
				// Toast.makeText(UserInfoVerificationTwoActivity.this,
				// response.desc, Toast.LENGTH_SHORT).show();
				ToastManage.showToast(response.desc);
			}

		}

	};

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	private void setdealpwd() {
		ToastManage.showToast("实名认证成功,请设置交易密码");
		// spf.edit().putString(Constant.USER_AUTH, "1").commit();
		// 实名认证成功，，调接口将本地数据刷新
		getUserInfo();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			// TODO
			if (resultCode == resultOk) {
				cardname = data.getStringExtra("name");
				bankkey = data.getStringExtra("key");
				bankid = data.getStringExtra("id");
				stream = data.getStringExtra("stream");
				et_banktype.setText(cardname);
				// iv_banklogo.setBackgroundResource(R.drawable.img_banklogo);
				if (stream.equals("")) {
					iv_banklogo.setImageResource(R.drawable.img_nobank);
				} else {
					byte[] b = Base64.decode(stream);
					Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
					iv_banklogo.setImageBitmap(bit);
				}
			}
			break;

		default:
			break;
		}
	}

	private void getkeyCode() {
		phone = ed_phone.getText().toString().trim();
		if (phone.length() == 0) {
			ToastManage.showToast("手机号不能为空");
			return;
		}
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_phone", phone);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.BANKCARDCODE, params, BaseData.class, null, keyCodeSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> keyCodeSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				Toast.makeText(UserInfoVerificationTwoActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
				btnGetCode.setText("剩余59秒");
				downTimer.start();
				btnGetCode.setEnabled(false);
			} else {
				Toast.makeText(UserInfoVerificationTwoActivity.this, response.desc, Toast.LENGTH_SHORT).show();
				btnGetCode.setText("发送验证码");
				btnGetCode.setEnabled(true);
				btnGetCode.setTextColor(getResources().getColor(R.color.main_color));
				downTimer.cancel();
			}

		}
	};

	private void getUserInfo() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在加载个人资料，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(UserInfoVerificationTwoActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.GETUSERINFO, params, BaseData.class, null, usersuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("个人信息 数据 == " + s);
						String userface = s.getString("user_face");
						String username = s.getString("user_realname");
						String phone = s.getString("user_phone");
						String idcard = s.getString("user_idcard");
						String bankcard = s.getString("bankcard_num");
						String emailnum = s.getString("mail_num");
						String userauth = s.getString("is_auth");

						spf.edit().putString(Constant.USER_EMAILNUM, emailnum).commit();

						if (userface != null) {
							spf.edit().putString(Constant.USER_FACE, userface).commit();
						} else {
							spf.edit().putString(Constant.USER_FACE, "").commit();
						}

						spf.edit().putString(Constant.USER_NAME, username).commit();
						spf.edit().putString(Constant.USER_PHONE, phone).commit();
						spf.edit().putString(Constant.USER_CARD, idcard).commit();
						spf.edit().putString(Constant.USER_BANKCARD_NUM, bankcard).commit();
						spf.edit().putString(Constant.USER_AUTH, userauth).commit();

						// 刷新用户的本地数据，然后弹出 设置交易密码
						int goflag = 0;
						dsp = new DialogSetPwd(UserInfoVerificationTwoActivity.this, goflag, nextflag, pro_id);
						dsp.createMyDialog();
					}
				} else {
					dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}

		};
	}

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

	class DownTimer extends CountDownTimer {

		public DownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 倒计时开始
			long m = millisUntilFinished / 1000;
			// String hh = String.valueOf(m/3600);
			// String mm = String.valueOf(m%3600/60);
			String ss = String.valueOf(m % 3600 % 60);
			// hh = twoLength(hh);
			// mm = twoLength(mm);
			ss = twoLength(ss);
			// tvLoginGetauth.setText(ss+"秒后再次获取");
			// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
			btnGetCode.setTextColor(getResources().getColor(R.color.main_color));
			btnGetCode.setText("　　" + ss + "s　　");
		}

		/**
		 * 如果小于位，加0
		 * 
		 * @param str
		 * @return
		 */
		private String twoLength(String str) {
			if (str.length() == 1) {
				return str = "0" + str;
			}
			return str;
		}

		@Override
		public void onFinish() {
			// canGetAuth = true;
			btnGetCode.setText("发送验证码");
			btnGetCode.setEnabled(true);
			// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
			btnGetCode.setTextColor(getResources().getColor(R.color.main_color));
		}
	};

	TextWatcher textWatcherone = new TextWatcher() {
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
			if (ed_card.getText().toString() != null && !ed_card.getText().toString().equals("")) {
				img_clear.setVisibility(View.VISIBLE);
			} else {
				img_clear.setVisibility(View.INVISIBLE);
			}
		}
	};

	TextWatcher textWatchertwo = new TextWatcher() {
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
			if (ed_phone.getText().toString() != null && !ed_phone.getText().toString().equals("")) {
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}
		}
	};

	TextWatcher textWatcherthree = new TextWatcher() {
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
			if (ed_verify.getText().toString() != null && !ed_verify.getText().toString().equals("")) {
				img_clear_three.setVisibility(View.VISIBLE);
			} else {
				img_clear_three.setVisibility(View.INVISIBLE);
			}
		}
	};

}
