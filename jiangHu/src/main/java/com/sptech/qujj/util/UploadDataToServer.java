package com.sptech.qujj.util;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UploadDataToServer {
	
	private Context context;
	
	private SharedPreferences spf;
	
	
	private OnRequestFinished finished;
	
	public UploadDataToServer(Context context){
		this.context = context;
		spf = context.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
	}
	
	public void upload(String jsonConfig, OnRequestFinished listener){
		finished = listener;
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());	
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>((Activity)context, false);
		request.HttpVolleyRequestPost(false, jsonConfig, params, BaseData.class, null, uploadSuccessListener, errorListener());
	}
	
	public void upload(String jsonConfig, HashMap<String, Object> data, OnRequestFinished listener){

		finished = listener;
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());	
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>((Activity)context, false);
		request.HttpVolleyRequestPost(false, jsonConfig, params, BaseData.class, null, uploadSuccessListener, errorListener());
	}
	
	 @SuppressWarnings("rawtypes")
		private Listener<BaseData> uploadSuccessListener = new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				finished.success(response);
			}
		};


		@SuppressLint("ShowToast")
		private Response.ErrorListener errorListener() {
			return new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					finished.failure(error);
				}
			};
		}
		
		public interface OnRequestFinished{
			void success(BaseData response);
			void failure(VolleyError error);
		}

	
	
}
