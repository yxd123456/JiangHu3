package com.sptech.qujj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class YeUtils {
	
	public static void startActivity(Context activity, Class<Activity> cls){
		Intent i = new Intent(activity, cls);
		activity.startActivity(i);
	}
	
	public static void startActivity(Context activity, Class<Activity> cls, Bundle bundle){
		Intent i = new Intent(activity, cls);
		i.putExtras(bundle);
		activity.startActivity(i);
	}
	
	public static void backToActivity(Context activity, Bundle bundle){
		Intent i = new Intent();
		if(bundle != null)
			i.putExtras(bundle);
		((Activity) activity).setResult(((Activity)activity).RESULT_OK, i);
		((Activity) activity).finish();
	}
	
	public static void takePictureFromSystemCamera(Context context, String picName, int reqCode){
		String mFilePath = Environment.getExternalStorageDirectory().getPath();
		String mImgPath1 = mFilePath+"/"+picName;
		Uri uri = Uri.fromFile(new File(mImgPath1));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		
		((Activity)context).startActivityForResult(intent, reqCode);
	}
	
	public static void selectPictureFromAlbum(Context context, int reqCode){
		Intent intent2 = new Intent();  
	    
	    intent2.setType("image/*");  
	      
	    intent2.setAction(Intent.ACTION_GET_CONTENT);   
	   
	    ((Activity)context).startActivityForResult(intent2, reqCode); 
	}
	
	/**
	 * 拍好照片并显示到ImageVioew上
	 */
	public static void takePictureAndShow(String imgPath, ImageView iv){
		FileInputStream fis = null;				
		try {
			fis = new FileInputStream(imgPath);
			Bitmap bitmap = BitmapUploadUtil.compressImageFromFile(imgPath);//对图片进行压缩
			iv.setImageBitmap(bitmap);  			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取图片并进行显示
	 * @param context
	 * @param data
	 * @param iv
	 */
	public static void getPictureAndShow(Context context, Intent data, ImageView iv){
		Uri uri = data.getData(); 			 
        ContentResolver cr = context.getContentResolver();  
         try {  
             //Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri)); 
        	 Options options = new Options();
             options.inPurgeable = true;
             options.inInputShareable = true;
             options.inPreferredConfig = Bitmap.Config.RGB_565;
             options.inSampleSize = 5;
        	 Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);		 
			 iv.setImageBitmap(bitmap);  
			 
         } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e); 
               
         }  
	}
	
}
