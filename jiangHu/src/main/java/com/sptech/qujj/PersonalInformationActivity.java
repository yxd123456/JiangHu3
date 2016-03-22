package com.sptech.qujj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.util.BitmapUploadUtil;
import com.sptech.qujj.util.Md5;
import com.sptech.qujj.view.CircleImageView;
import com.sptech.qujj.view.TitleBar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class PersonalInformationActivity extends BaseActivity {
	
	private TitleBar mTitleBar;
	private CircleImageView civ1, civ2;
	private Spinner sp_zhiye, sp_shouru, sp_jiaoyu;
	private ImageView iv_byzs;
	private Button btn_commit;
	private CustomDialog dialog;
	private String mFilePath;
	private String mImgPath;
	private Uri mUri;
	private Bitmap bitmap;
	private boolean flag;
	private Bitmap bitmap2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ge_ren_xin_yong);
		
		init();
		
		doSpinner();
		
		doBiYeZhengShu();
		
		doCommitButton();
		
	}

	private void doSpinner() {
		// TODO Auto-generated method stub
		
	}

	private void doCommitButton() {
		// TODO Auto-generated method stub
		
	}

	private void doBiYeZhengShu() {
		// TODO Auto-generated method stub
		iv_byzs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.show();
				// 拍照获取图片
				dialog.findViewById(R.id.tv_photograph).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);			
						startActivityForResult(intent, 1);
					}
				});
				// 从图库中获取图片
				dialog.findViewById(R.id.tv_gallery).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent2 = new Intent();  
		                  
		                intent2.setType("image/*");  
		                  
		                intent2.setAction(Intent.ACTION_GET_CONTENT);   
		               
		                startActivityForResult(intent2, 2);  
					}
				});
				// 取消
				dialog.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		
		mFilePath = Environment.getExternalStorageDirectory().getPath();
		mImgPath = mFilePath+"/"+"byzs.png";
		
		mUri = Uri.fromFile(new File(mImgPath));
		
		
		dialog = new CustomDialog(this, R.style.custom_dialog_style, R.layout.takephoto_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setGravity((Gravity.BOTTOM));
		window.setWindowAnimations(R.style.mydialogstyle);
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		
		mTitleBar = (TitleBar) findViewById(R.id.titleBar);
		mTitleBar.bindTitleContent("完善信息", 0, 0, "", "");
		
		sp_zhiye = (Spinner) findViewById(R.id.spinner_zhiye);
		sp_zhiye.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, 
				new String[]{"xxxxxx", "yyyyyy", "zzzzzzzz"}));
		
		sp_shouru = (Spinner) findViewById(R.id.spinner_shouru);
		sp_shouru.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, 
				new String[]{"xxxxxx", "yyyyyy", "zzzzzzzz"}));
		
		sp_jiaoyu = (Spinner) findViewById(R.id.spinner_jiaoyu);
		sp_jiaoyu.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, 
				new String[]{"xxxxxx", "yyyyyy", "zzzzzzzz"}));
		
		iv_byzs = (ImageView) findViewById(R.id.img_byzs);
		
		btn_commit = (Button) findViewById(R.id.btn_commit);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		dialog.dismiss();
		if(resultCode == RESULT_OK){
			
			
			
			
			switch (requestCode) {
			case 1:
				/*Bundle bundle = data.getExtras();
				bitmap_zm = (Bitmap) bundle.get("data");*/
				
				FileInputStream fis = null;
				
				try {
					fis = new FileInputStream(mImgPath);
					bitmap = BitmapFactory.decodeStream(fis);
					
					iv_byzs.setImageBitmap(bitmap);
					flag = true;
					new BitmapUploadUtil(this).uploadBitmapToServer(bitmap, 1);
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
				
				break;
			case 2:	
				Uri uri = data.getData(); 			 
		        ContentResolver cr = this.getContentResolver();  
		         try {  
		             bitmap2 = BitmapFactory.decodeStream(cr.openInputStream(uri));   
		             /* 将Bitmap设定到ImageView */  
		             iv_byzs.setImageBitmap(bitmap2);
		             
		             flag = true;
			         new BitmapUploadUtil(this).uploadBitmapToServer(bitmap2, 1);
					
		         } catch (FileNotFoundException e) {  
		                Log.e("Exception", e.getMessage(),e); 
		               
		         }  
		        
				break;
			}
			
		}
	}
	
}
