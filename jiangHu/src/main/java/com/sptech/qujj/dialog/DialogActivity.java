package com.sptech.qujj.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.sptech.qujj.R;
import com.sptech.qujj.model.Adver;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.view.EventHandleListener;

/**
 * 首页 活动-- 弹出框
 * 
 * @author yebr
 * 
 */

public class DialogActivity {

	private Activity mycontext;
	private Dialog dialog;

	private Button btn_next;
	private ImageView img_close, img_adver;
	EventHandleListener eventHandleListener;

	private Adver adver;

	public DialogActivity(Activity context, Adver adver, EventHandleListener eventHandleListener) {
		this.mycontext = context;
		this.adver = adver;
		this.eventHandleListener = eventHandleListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		//
		// // 这句代码换掉dialog默认背景，否则dialog的边缘发虚透明而且很宽
		// // 总之达不到想要的效果
		// getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		// LayoutInflater inflater = ((AnimationActivity)
		// context).getLayoutInflater();
		// localView = inflater.inflate(R.layout.animclearpan, null);
		// localView.setAnimation(AnimationUtils.loadAnimation(context,
		// R.anim.slide_bottom_to_top));
		// setContentView(localView);
		// // 这句话起全屏的作用
		// getWindow().setLayout(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		//
		// initView();
		// initListener();
		View view = LayoutInflater.from(mycontext).inflate(R.layout.dialog_view_activity, null);

		btn_next = (Button) view.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				eventHandleListener.eventLeftHandlerListener();
			}
		});
		if (adver.getAd_url() == null || adver.getAd_url().equals("")) {
			btn_next.setVisibility(View.GONE);
		}
		img_close = (ImageView) view.findViewById(R.id.img_close);
		img_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});
		// dialog = new Dialog(this, R.style.Dialog_Fullscreen);
		// dialog.setContentView(R.layout.main);
		dialog = new Dialog(mycontext, R.style.fulldialog);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					closeDialog();
					return true;
				}
				return false;
			}
		});

		img_adver = (ImageView) view.findViewById(R.id.img_adver);
		// String cardname = bankcardBean.getCard_bank();
		// String cardStream = cardMap.get(adverImage);
		// System.out.println("card--name==" + cardname);
		// System.out.println("card--img==" + cardStream);
		String adverImage = adver.getAdpic();
		if (adverImage == null || adverImage.equals("")) {
			// img_adver.setImageResource(R.drawable.img_nobank);
		} else {
			byte[] b = Base64.decode(adverImage);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			img_adver.setImageBitmap(bit);
		}
		dialog.show();
	}

	public void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
