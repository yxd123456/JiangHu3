package com.sptech.qujj.http;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.Network;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.sptech.qujj.App;
import com.sptech.qujj.activity.LoginActivity;
import com.sptech.qujj.cache.DataCache;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogSetPwd;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;

public class HttpVolleyRequest<T> {

	private ErrorListener errorListener;
	private Listener<T> successListener;
	private Activity mAct;
	private boolean isCache = false; // 是否开启缓存
	private boolean isExit = true; // 判断缓存文件是否存在
	private String urlKey;
	private HashMap<String, String> params;
	private int method;
	private boolean isShowErrorToast = true;

	private DialogSetPwd dsp; // 弹出 设置交易密码弹出框

	public HttpVolleyRequest() {

	}

	public HttpVolleyRequest(Activity mAct) {
		this.mAct = (Activity) mAct;
	}

	public HttpVolleyRequest(Activity mAct, boolean isCache) {
		this.isCache = isCache;
		this.mAct = (Activity) mAct;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// 防止网络请求在延时前就已结束，此时缓存已经过期了
			T t = null;
			if (method == Method.GET) {
				t = (T) App.getInstance().getCache().getAsObject(getCacheKey((String) msg.obj));
			} else {
				t = (T) App.getInstance().getCache().getAsObject(getPostCacheKey((String) msg.obj, params));
			}
			successListener.onResponse(t);
		}
	};

	/**
	 * Volley Get方法
	 * 
	 * @param url
	 *            请求地址
	 * @param parentClass
	 *            父类数据结构
	 * @param class1
	 *            子类数据结构
	 * @param listener
	 *            成功监听
	 * @param errorListener
	 *            错误监听
	 */
	@SuppressWarnings("unchecked")
	public void HttpVolleyRequestGet(String url, Class<T> parentClass, Class<?> class1, Listener<T> listener, ErrorListener errorListener) {

		GsonRequest<T> request = new GsonRequest<T>(Method.GET, url, parentClass, class1, SuccessListener(), ErrorListener());
		RequestManager.getRequestQueue().add(request);
		this.errorListener = errorListener;
		this.successListener = listener;
		this.urlKey = url;
		this.method = Method.GET;
		// 把缓存带出去
		if (isCache) {
			T t = (T) App.getInstance().getCache().getAsObject(getCacheKey(url));
			if (t != null) {
				handler.sendMessageDelayed(handler.obtainMessage(0, url), 300);
			} else {
				isExit = false;
			}
		} else {
			isExit = false;
		}
	}

	/**
	 * Volley Post方法
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            上传的Map数据结构
	 * @param parentClass
	 *            父类数据结构
	 * @param class1
	 *            子类数据结构
	 * @param listener
	 *            成功监听
	 * @param errorListener
	 *            错误监听
	 */
	@SuppressWarnings("unchecked")
	public void HttpVolleyRequestPost(String url, HashMap<String, String> params, Class<T> parentClass, Class<?> class1, Listener<T> listener, ErrorListener errorListener) {

		GsonRequest<T> request = new GsonRequest<T>(Method.POST, url, params, parentClass, class1, SuccessListener(), ErrorListener());
		File file = new File(Environment.getExternalStorageDirectory().getPath() + Constant.VOLLEY_CACHE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		DiskBasedCache cache = new DiskBasedCache(file, 10 * 1024 * 1024); //
		Network network = new BasicNetwork(new HurlStack()); //
		RequestQueue mRequestQueue = new RequestQueue(cache, network); //
		mRequestQueue.start();
		mRequestQueue.add(request);

		this.errorListener = errorListener;
		this.successListener = listener;
		this.urlKey = url;
		this.method = Method.POST;
		this.params = params;
		// 把缓存带出去
		if (isCache) {
			T t = (T) App.getInstance().getCache().getAsObject(getPostCacheKey(url, params));
			if (t != null) {
				handler.sendMessageDelayed(handler.obtainMessage(0, url), 300);
			} else {
				isExit = false;
			}
		} else {
			isExit = false;
		}

	}

	public void HttpVolleyRequestPost(boolean isShowErrorToast, String url, HashMap<String, String> params, Class<T> parentClass, Class<?> class1, Listener<T> listener, ErrorListener errorListener) {
		this.isShowErrorToast = isShowErrorToast;
		HttpVolleyRequestPost(url, params, parentClass, class1, listener, errorListener);
	}

	/**
	 * 返回成功Response
	 * 
	 * @return
	 */
	private Listener<T> SuccessListener() {
		return new Listener<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onResponse(T response) {
				if (response == null) {
					return;
				}

				if (!"".equals(((BaseData<T>) response).code)) {

					if (isCache) {
						if (!isExit) {
							if (successListener != null) {
								successListener.onResponse(response);
							}
						}
						if (method == Method.GET) {
							App.getInstance().getCache().put(getCacheKey(urlKey), (BaseData<T>) response, DataCache.TIME_DAY);
						} else {
							App.getInstance().getCache().put(getPostCacheKey(urlKey, params), (BaseData<T>) response, DataCache.TIME_DAY);
						}

					} else {
						if (successListener != null) {

							String code = ((BaseData<T>) response).code;
							// if (code.equals("1001") || ) {
							//
							// }

							switch (((BaseData<T>) response).code) {
							case "1001":
							case "1003":
							case "1011":
							case "1012":
							case "1013":
							case "1014":
							case "1018":
							case "1036":
							case "1037": // 调整到登录页面

								ToastManage.showToast(((BaseData<T>) response).desc);
								Intent intent = new Intent(mAct, LoginActivity.class);
								intent.putExtra("from", "normal");

								// 清除数据
								SharedPreferences spf = mAct.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
								spf.edit().clear();
								spf.edit().commit();
								spf.edit().putBoolean(Constant.IS_LOGIN, false).commit();// TODO
								mAct.startActivity(intent);
								mAct.finish();
								break;
							// case "1028": // 跳转到实名认证页面
							// ToastManage
							// .showToast(((BaseData<T>) response).desc);
							// Intent intent2 = new Intent(mAct,
							// UserInfoVerificationActivity.class);
							// mAct.startActivity(intent2);
							// mAct.finish();
							// break;
							// case "1038": // 跳转到交易密码设置页面
							// // ToastManage
							// // .showToast(((BaseData<T>) response).desc);
							// // Intent intent3 = new Intent(mAct,
							// // UserInfoVerificationActivity.class);
							// // mAct.startActivity(intent3);
							// // mAct.finish();
							// break;
							// case :
							// successListener.onResponse(response);
							default:
								successListener.onResponse(response);
								break;
							}
						}
					}
				} else if ("".equals(((BaseData<T>) response).code) && ((BaseData<T>) response).code.length() > 0) {

					if (successListener != null) {
						successListener.onResponse(response);
					}
				}
			}
		};
	}

	/**
	 * 返回错误Response 集中处理错误提示消息
	 * 
	 * @return
	 */
	private Response.ErrorListener ErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (mAct != null && isShowErrorToast) {
					// toast显示错误信息
					Toast.makeText(mAct, VolleyErrorHelper.getMessage(error, mAct), Toast.LENGTH_SHORT).show();
				}

				if (errorListener != null)
					errorListener.onErrorResponse(error);
			}
		};
	}

	/**
	 * 完全匹配
	 * 
	 * @param url
	 * @return
	 */
	private String getCacheKey(String url) {
		// if (url.contains("&")) {
		// return url.substring(0, url.indexOf("&"));
		// } else {
		// return url;
		// }
		return url;
	}

	private String getPostCacheKey(String url, HashMap<String, String> map) {
		return url + getParams(map);
	}

	/**
	 * 根据Map 转成parmas字符串
	 * 
	 * @param localTreeMap
	 * @return
	 */
	public String getParams(HashMap<String, String> map) {
		String str1 = "";
		if (null == map) {
			return str1;
		}
		// 参数为空
		if (map.isEmpty()) {
			return str1;
		}
		Iterator<String> localIterator = map.keySet().iterator();
		while (true) {
			if (!localIterator.hasNext()) {
				return str1.substring(0, -1 + str1.length());
			}
			String str2 = (String) localIterator.next();
			str1 = str1 + str2 + "_" + (String) map.get(str2) + ",";
		}
	}
}
