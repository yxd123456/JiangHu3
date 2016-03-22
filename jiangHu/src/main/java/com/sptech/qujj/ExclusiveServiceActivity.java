package com.sptech.qujj;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * 专属客服
 * @author 叶旭东
 *
 */
public class ExclusiveServiceActivity extends BaseActivity {
	
	private IWXAPI api;//微信API
	
	private ClipboardManager myClipboard;//剪贴板管理器
	private ClipData myClip;//剪切数据
	
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	
	private String jsonString;//获取到的JSON字符串
	
	private TitleBar titleBar;
	
	private TextView tv_weixin_name;//显示微信号
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ke_fu);
		
		init();
		
		doJSON();//从服务器获取客服微信数据并显示在TextView上

	}
	
	private void doJSON() {
		// TODO Auto-generated method stub
		dialogHelper.startProgressDialog();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.EXCLUSIVE_SERVICE, params, BaseData.class, null, uploadSuccessListener, errorListener());	
	}

	private void init() {
		// TODO Auto-generated method stub
		dialogHelper = new DialogHelper(this);
		
		api = WXAPIFactory.createWXAPI(this, "wx26f61dae5d3c2ee3", false);
		
		myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		
		tv_weixin_name = (TextView) findViewById(R.id.tv_weixin_name);
		
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("专属客服",  R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(new OnClickTitleListener() {
			
			@Override
			public void onRightButtonClick(View view) {
				// TODO Auto-generated method stub
				ActivityJumpManager.finishActivity(ExclusiveServiceActivity.this, 1);
			}
			
			@Override
			public void onLeftButtonClick(View view) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private Listener<BaseData> uploadSuccessListener = new Listener<BaseData>() {

		private String weixin_name;

		@Override
		public void onResponse(BaseData response) {
			if (response.code.equals("0")) {
				dialogHelper.stopProgressDialog();
				
				jsonString = new String(Base64.decode(response.data.getBytes(), Base64.DEFAULT));
				
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					weixin_name = jsonObject.getString("weixn_name");
					tv_weixin_name.setText(weixin_name);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
					
				myClip = ClipData.newPlainText("text", weixin_name);

				myClipboard.setPrimaryClip(myClip);
				
			} else {
				ToastManage.showToast(response.desc);
			}
		}
	};


	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				//dialogHelper.stopProgressDialog();
			}
		};
	}
	
	
	public void goToWeiXin(View v){
		ToastManage.showToast("微信号复制成功\n到微信新增好友粘贴搜索");
		api.openWXApp();
	}
	
}
