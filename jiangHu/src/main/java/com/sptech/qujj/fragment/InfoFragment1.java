package com.sptech.qujj.fragment;

import com.sptech.qujj.R;
import com.sptech.qujj.activity.UserInfoVerificationActivity;
import com.sptech.qujj.constant.Constant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 实名认证引导页
 * @author 叶旭东
 *
 */
public class InfoFragment1 extends Fragment implements OnClickListener{
	
	private ImageView iv_complete;//边角图片，显示文字为已完善或未完善
	private ImageView iv_check;//勾选图片，显示勾中与未勾中状态
	private Button btn_complete;//显示是否完成的按钮
	private TextView tv_nda;//保密协议
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_info1, container, false);
		
		init(v);
				
		return v;
	}

	private void init(View v) {
		// TODO Auto-generated method stub
		iv_complete = (ImageView) v.findViewById(R.id.iv_complete);
		iv_check = (ImageView) v.findViewById(R.id.iv_check);
		btn_complete = (Button) v.findViewById(R.id.btn_complete);
		tv_nda = (TextView) v.findViewById(R.id.tv_nda);
		tv_nda.setText(Html.fromHtml("<u>"+"《保密协议》"+"</u>"));
		
		if(Constant.FLAG_SHIMINGRENZHENG){
			iv_check.setVisibility(View.VISIBLE);
			iv_complete.setImageResource(R.drawable.img_label_information_finished);
			btn_complete.setText("已完成");
			btn_complete.setClickable(false);
		} else {
			iv_check.setVisibility(View.INVISIBLE);
			iv_complete.setImageResource(R.drawable.img_label_information_notperfect);
			btn_complete.setText("开始认证");
			btn_complete.setClickable(true);
			btn_complete.setOnClickListener(this);
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_complete:
			startActivity(new Intent(getActivity(), UserInfoVerificationActivity.class));
			break;

		default:
			break;
		}
		
	}
	
}
