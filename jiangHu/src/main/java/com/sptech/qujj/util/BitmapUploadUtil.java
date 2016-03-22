package com.sptech.qujj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.sptech.qujj.R;
import com.sptech.qujj.activity.PersonInfoActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.view.BitmapHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.widget.ImageView;
import android.graphics.BitmapFactory;

public class BitmapUploadUtil {
	
	private Context context;
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private int type;
	private ImageView iv_flag;
	
	public BitmapUploadUtil(Context context){
		this.context = context;
		this.spf = context.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
	}
	
	
	
	 public static Bitmap compressImageFromFile(String srcPath) {  
	        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        newOpts.inJustDecodeBounds = true;//只读边,不读内容  
	        newOpts.inPreferredConfig = Config.RGB_565;
	        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
	  
	        newOpts.inJustDecodeBounds = false;  
	        int w = newOpts.outWidth;  
	        int h = newOpts.outHeight;  
	        float hh = 800f;//  
	        float ww = 480f;//  
	        int be = 1;  
	        if (w > h && w > ww) {  
	            be = (int) (newOpts.outWidth / ww);  
	        } else if (w < h && h > hh) {  
	            be = (int) (newOpts.outHeight / hh);  
	        }  
	        if (be <= 0)  
	            be = 1;  
	        newOpts.inSampleSize = be;//设置采样率  
	          
	        newOpts.inPreferredConfig = Config.RGB_565;//该模式是默认的,可不设  
	        newOpts.inPurgeable = true;// 同时设置才会有效  
	        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
	          
	        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
//	      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩  
	                                    //其实是无效的,大家尽管尝试  
	        return bitmap;  
	    }  
	
	public void uploadBitmapToServer(Bitmap bitmap, int type, ImageView iv_flag){
		
		this.type = type;
		this.iv_flag = iv_flag;
		
		uploadBitmapToServer(bitmap, type);

	}
	
	public void uploadBitmapToServer(Bitmap bitmap, int type){
		
		dialogHelper = new DialogHelper(context);
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在上传照片，请稍后.....");
		
		HashMap<String, Object> files = new HashMap<String, Object>();
		String mFilePath = BitmapHelper.saveBitmap2file(bitmap, 100);//将图片转换为字符串
		File file = new File(mFilePath);//将字符串转换为文件对象
		FileInputStream fis = null;
		byte[] buffer = null;
		try {
			fis = new FileInputStream(file);//将文件对象转换为文件输入流
			buffer = new byte[(int) file.length()];//新建一个字节数组
			fis.read(buffer);//将输入流读入字节数组
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!Tools.isNull(mFilePath)) {
			System.out.println(Base64.encode(buffer));

			files.put("user_face", Base64.encode(buffer));//将字节数组进行编码并放入HashMap中
		} else {
			files.clear();
		}
		
		final HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, Object> data = new HashMap<String, Object>(); 
		params.put("uid", spf.getString(Constant.UID, "").toString());
		data.put("pic", HttpUtil.getData(files));
		data.put("type", type);
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		// ThreadPoolManager.getInstance().addTask(new Runnable() {
		//
		// @Override
		// public void run() {
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>((Activity)context, false);
		request.HttpVolleyRequestPost(false, JsonConfig.PICUPLOAD, params, BaseData.class, null, uploadSuccessListener, errorListener());

	}
	
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> uploadSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			if (response.code.equals("0")) {
				dialogHelper.stopProgressDialog();
				switch (type) {
				case 1:
					iv_flag.setVisibility(View.VISIBLE);
					break;
				case 2:
					iv_flag.setVisibility(View.VISIBLE);
					break;
				case 3:
					iv_flag.setVisibility(View.VISIBLE);
					break;
				}
				
				ToastManage.showToast("照片上传成功");
			} else {
				ToastManage.showToast(response.desc);
			}
		}
	};


	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}
}
