package com.sptech.qujj.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.InvestActivity;
import com.sptech.qujj.activity.MyLoanActivity;
import com.sptech.qujj.activity.SelectCardActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.view.EventInvestListener;

/**
 * 确认支付 - 弹出框
 * 
 * @author yebr
 * 
 */

public class DialogPay implements OnClickListener {

	private Activity context;
	private Dialog dialog;
	private int requestcode = 2;

	private Button btn_next;
	private ImageView img_close;
	EventInvestListener eventHandleListener;
	private RelativeLayout rela_hongbao; // 红包

	private TextView tv_proname, tv_money, tv_userbalance, tv_usermoney, tv_hongbao, tv_needpay;// 项目名称，
	// 投资金额，账户余额,红包金额,需支付金额
	private ImageView img_selectcard;// 选择银行卡按钮

	private String proname;// 项目名称
	private Boolean redflag = false; // 红包 flag
	private float redmoney; // 红包 金额
	private float user_money; // 用户余额
	private float buymoney;// 用户输入的金额

	private BankcardBean defaultcard; // 默认的银行卡
	private SharedPreferences spf;

	private int is_payment = 0;// 0 余额 1 银行卡
	private CheckBox cb_red;
	private int position_select = 0; // 上次选中的 银行卡

	private RadioButton rb_balance, rb_bankcard;
	private RelativeLayout relative_balance, relative_card;// 余额支付，银行卡支付

	private ImageView img_card; // 银行卡图标
	private TextView tv_cardinfo;// 建设银行(4527)
	HashMap<String, String> cardMap = new HashMap<String, String>();

	private int number_has; // 购买份数
	private List<BankcardBean> curBanklist = new ArrayList<BankcardBean>();//

	public DialogPay(Activity context, String proname, Boolean redflag, float redmoney, float buymoney, float user_money, BankcardBean defaultcard, List<BankcardBean> curBanklist, int number_has,
	        EventInvestListener eventHandleListener) {
		this.context = context;
		this.proname = proname;
		this.redflag = redflag;
		this.redmoney = redmoney;
		this.user_money = user_money;
		this.defaultcard = defaultcard;
		this.buymoney = buymoney;
		this.curBanklist.addAll(curBanklist);
		this.number_has = number_has;

		if (proname.equals("平台还款")) {
			this.position_select = MyLoanActivity.position_select;
		} else {
			this.position_select = InvestActivity.position_select;
		}

		System.out.println("redflag==" + redflag);
		System.out.println("redmoney==" + redmoney);
		System.out.println("user_money==" + user_money);
		System.out.println("buymoney==" + buymoney);
		if (defaultcard != null) {
			System.out.println("defaultcard===" + defaultcard.getCard_bank());
		}
		System.out.println("number_has===" + number_has);
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_pay, null);

		img_card = (ImageView) view.findViewById(R.id.img_card);
		tv_cardinfo = (TextView) view.findViewById(R.id.tv_cardinfo);
		tv_usermoney = (TextView) view.findViewById(R.id.tv_usermoney);
		rela_hongbao = (RelativeLayout) view.findViewById(R.id.rela_hongbao);
		cb_red = (CheckBox) view.findViewById(R.id.cb_red);
		tv_hongbao = (TextView) view.findViewById(R.id.tv_hongbao);
		tv_money = (TextView) view.findViewById(R.id.tv_money);
		tv_proname = (TextView) view.findViewById(R.id.tv_proname);
		tv_needpay = (TextView) view.findViewById(R.id.tv_needpay);

		tv_userbalance = (TextView) view.findViewById(R.id.tv_userbalance);

		relative_balance = (RelativeLayout) view.findViewById(R.id.relative_balance);
		relative_card = (RelativeLayout) view.findViewById(R.id.relative_card);

		img_selectcard = (ImageView) view.findViewById(R.id.img_selectcard);
		img_selectcard.setOnClickListener(this);

		rb_balance = (RadioButton) view.findViewById(R.id.rb_balance);
		rb_bankcard = (RadioButton) view.findViewById(R.id.rb_bankcard);
		rb_balance.setEnabled(false);
		rb_bankcard.setEnabled(false);

		rb_balance.setChecked(true);
		is_payment = 0;

		relative_balance.setOnClickListener(this);
		relative_card.setOnClickListener(this);

		tv_proname.setText(proname);
		tv_usermoney.setText(DataFormatUtil.floatsaveTwo(user_money));
		tv_money.setText("¥" + DataFormatUtil.floatsaveTwo(buymoney));
		if (redflag) {
			rela_hongbao.setVisibility(View.VISIBLE);
			tv_hongbao.setText(redmoney + "");
			tv_needpay.setText("需支付 : " + DataFormatUtil.floatsaveTwo(buymoney - redmoney) + "元");
		} else {
			tv_needpay.setText("需支付 : " + DataFormatUtil.floatsaveTwo(buymoney) + "元");
		}
		// 余额不足，
		if (user_money < buymoney) {
			is_payment = 1;
			// relative_balance.setBackgroundColor(Color.parseColor("#f6f6f6"));
			tv_usermoney.setTextColor(context.getResources().getColor(R.color.text_hint));
			tv_userbalance.setTextColor(context.getResources().getColor(R.color.text_hint));

			rb_balance.setChecked(false);
			rb_balance.setBackgroundResource(R.drawable.btn_bankcard_unselect);
			rb_bankcard.setChecked(true);
			rb_bankcard.setBackgroundResource(R.drawable.btn_bankcard_select);
		}

		cb_red.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (redflag) {
					if (cb_red.isChecked()) {
						tv_needpay.setText("需支付 : " + DataFormatUtil.floatsaveTwo(buymoney - redmoney) + "元");
					} else {
						tv_needpay.setText("需支付 : " + DataFormatUtil.floatsaveTwo(buymoney) + "元");
					}
				}

			}
		});
		btn_next = (Button) view.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);

		img_close = (ImageView) view.findViewById(R.id.img_close);
		img_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				// 关闭订单
				eventHandleListener.eventCloseORderListener();
			}
		});
		dialog = new Dialog(context, R.style.dialog);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					closeDialog();
					// 关闭订单
					eventHandleListener.eventCloseORderListener();
					return true;
				}
				return false;
			}
		});
		initBankCard(defaultcard);
		dialog.show();
	}

	public void initBankCard(BankcardBean defaultcard) {
		spf = context.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		cardMap = DataFormatUtil.getCardMap(spf);
		if (defaultcard != null) {
			tv_cardinfo.setText(defaultcard.getCard_bank() + "(" + DataFormatUtil.bankcardsaveFour(defaultcard.getCard_no()) + ")");
			String cardname = defaultcard.getCard_bank();
			String cardStream = cardMap.get(cardname);
			// System.out.println("card--name==" + cardname);
			// System.out.println("card--img==" + cardStream);
			if (cardStream == null || cardStream.equals("")) {
				img_card.setImageResource(R.drawable.img_nobank);
			} else {
				byte[] b = Base64.decode(cardStream);
				Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
				img_card.setImageBitmap(bit);
			}
		}
		if (proname.equals("平台还款")) {
			this.position_select = MyLoanActivity.position_select;
		} else {
			this.position_select = InvestActivity.position_select;
		}
		System.out.println("InvestActivity.position_select=== " + position_select);
		// else {
		// ToastManage.showToast("您还没有绑定银行卡");
		// }
	}

	public void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.relative_balance: // 点击 余额
			System.out.println("点击 余额");
			// 判断余额是否 大于支付金额
			if (user_money < buymoney) {
				// rb_balance.setEnabled(false);
				// relative_balance.setEnabled(false);
				// relative_balance.setBackgroundColor(Color.parseColor("#f6f6f6"));
				System.out.println("?? buymoney ==" + buymoney);
				ToastManage.showToast("您的余额不足,请选择其它支付方式");
				return;
			}
			if (!rb_balance.isChecked()) {
				is_payment = 0;
				rb_bankcard.setChecked(false);
				rb_bankcard.setBackgroundResource(R.drawable.btn_bankcard_unselect);
				rb_balance.setChecked(true);
				rb_balance.setBackgroundResource(R.drawable.btn_bankcard_select);
			}
			break;
		case R.id.relative_card: // 选择银行卡
			System.out.println("点击 选择银行卡");
			if (!rb_bankcard.isChecked()) {
				is_payment = 1;
				rb_balance.setChecked(false);
				rb_balance.setBackgroundResource(R.drawable.btn_bankcard_unselect);
				rb_bankcard.setChecked(true);
				rb_bankcard.setBackgroundResource(R.drawable.btn_bankcard_select);
			}

			break;
		case R.id.img_selectcard:
			Intent intent = new Intent(context, SelectCardActivity.class);
			if ("平台还款".equals(proname)) {
				intent.putExtra("backflag", 4);
			} else {
				intent.putExtra("backflag", 3);
			}
			intent.putExtra("bankcardlist", (Serializable) curBanklist);
			System.out.println("Dialog  --- " + position_select);
			intent.putExtra("position_select", position_select);
			context.startActivityForResult(intent, requestcode);
			context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_next:
			eventHandleListener.eventDataHandlerListener(is_payment, cb_red.isChecked());
			break;
		default:
			break;
		}

	}
}
