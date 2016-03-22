package com.sptech.qujj.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.BitmapUploadUtil;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 用户信息验证
 * 
 * @author yebr
 * 
 */
public class UserInfoVerificationActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private Button btn_next, left;
	private EditText ed_name, ed_card;
	private String name, card;
	private int nextflag = 0;
	private ImageView img_clear, img_clear_two; // input 清除按钮
	private int pro_id = 0; // 产品id
	
	private ImageView iv1,iv2,iv3;//用户上传的证件照
	
	private boolean flag1 = false; //用于判断用户是否上传了图片的标识
	private boolean flag2 = false;
	private boolean flag3 = false;
	
	private CustomDialog dialog;
	private String mFilePath;//SD卡路径
	private String mImgPath1,mImgPath2,mImgPath3;//三张照片的图片路径
	private Uri mUri1,mUri2,mUri3;//三招照片的Uri
	
	private ImageView iv_success_flag1, iv_success_flag2, iv_success_flag3;

/*——————————————————————————————主干逻辑——————————————————————————————*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_userinfo_verifivation);
		initView();
		Tools.addActivityList(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_next:
			userverify();//确认性按钮
			break;
		case R.id.img_clear:
			ed_name.setText("");//清空姓名输入框
			break;
		case R.id.img_clear_two:
			ed_card.setText("");//清空证件号码输入框
			break;
		case R.id.img01:					
			doDialogListener(dialog, mUri1, 7, 2);//获取正面照
			break;
		case R.id.img02:
			doDialogListener(dialog, mUri2, 8, 4);//获取反面照
			break;
		case R.id.img03:	
			doDialogListener(dialog, mUri3, 9, 6);//获取手持证件照
			break;			
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//if(requestCode==7||requestCode==8||requestCode==9)
			dialog.dismiss();
		if(resultCode == RESULT_OK){
			
			switch (requestCode) {
			case 1:		
				
				uploadBitmap(1);//上传正面照（来源：拍照）			
				break;
			case 2:				
				uploadBitmap2(1, data);	//上传正面照（来源：相册）	        
				break;
			case 3:
				uploadBitmap(2);//上传反面照（来源：拍照）
				break;
			case 4:
				uploadBitmap2(2, data);//上传反面照（来源：相册）
				break;
			case 5:
				uploadBitmap(3);//上手持证件照（来源：拍照）
				break;
			case 6:
				uploadBitmap2(3, data);//上传手持证件照（来源：相册）
				break;
			case 7:		
				doPhoto(mUri1, 1);		
				break;
			case 8:
				doPhoto(mUri2, 3);
				break;
			case 9:
				doPhoto(mUri3, 5);
				break;
			}
			
		} 
	}
	
	public void doPhoto(Uri uri, int reqCode){
		Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("scale", false);
		//设置宽高比例
		intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置裁剪图片宽高
        intent.putExtra("outputX", 600);
	    intent.putExtra("outputY", 600);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("noFaceDetection", true); // no face detection
	    
		//广播刷新相册 
		Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intentBc.setData(uri);     
		this.sendBroadcast(intentBc);   
		startActivityForResult(intent, reqCode); //设置裁剪参数显示图片至ImageView*/
	}
	
	/**
	 * 点击按钮进入下个界面
	 */
	private void userverify() {
		
		checkIsOk();//检查用户填写的资料是否符合要求
		
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		if (nextflag == 0) {
			// ActivityJumpManager.jumpToMain(this, MainActivity.class);
			ActivityJumpManager.finishActivity(this, 1);
		} else {
			ActivityJumpManager.finishActivity(this, 1);
		}
	}
	
/*——————————————————————————————枝干逻辑——————————————————————————————*/
	
	private void goToNext() {
		// TODO Auto-generated method stub
		Intent in = new Intent(UserInfoVerificationActivity.this, UserInfoVerificationTwoActivity.class);

		// nextflag ! 点击下次先 ，分情况: 0 表示 实名认证的时候 -- 回到首页;
		// 1 表示 交易的时候--返回到交易界面;
		// 2 表示 充值 提现的时候,跳到账户余额 页面
		// 3 在账户与安全点击实名认证，完成后跳回个人中心
		in.putExtra("nextflag", nextflag);
		in.putExtra("realname", name);
		in.putExtra("cardid", card);
		in.putExtra("pro_id", pro_id);
		startActivity(in);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void checkIsOk() {
		// TODO Auto-generated method stub
		name = ed_name.getText().toString().trim();
		card = ed_card.getText().toString().trim();

		if (name == null || "".equals(name)) {
			ToastManage.showToast("姓名不能为空");
			return;
		}
		if (card == null || "".equals(card)) {
			ToastManage.showToast("银行卡号不能为空");
			return;
		}
		if (!CheckUtil.isIdCard(card)) {
			ToastManage.showToast("请输入正确的身份证号");
			return;
		}
		if (!flag1||!flag2||!flag3) {
			ToastManage.showToast("请上传三张必须的证件照");
			return;
		}
		goToNext();
	}
	
	private void uploadBitmap2(int type, Intent data) {
		// TODO Auto-generated method stub
		/*Uri uri = data.getData(); 			 
        ContentResolver cr = this.getContentResolver();  */
         try {  
             //Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri)); 
        	/* Options options = new Options();
             options.inPurgeable = true;
             options.inInputShareable = true;
             options.inPreferredConfig = Bitmap.Config.RGB_565;
             options.inSampleSize = 5;
        	 Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);*/
        	 Bitmap bitmap = null;
             switch (type) {
				case 1:
					
					 //bitmap = BitmapFactory.decodeFile(sdcardTempFile.getAbsolutePath());       
					 bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri1));
					 iv1.setImageBitmap(bitmap);  
					
					 new BitmapUploadUtil(this).uploadBitmapToServer(bitmap, type, iv_success_flag1);
		             flag1 = true;
					break;
				case 2:
					bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri2));
					 iv2.setImageBitmap(bitmap); 
					
					 new BitmapUploadUtil(this).uploadBitmapToServer(bitmap, type, iv_success_flag2);
		             flag2 = true;
					break;
				case 3:
					bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri3));
					 iv3.setImageBitmap(bitmap);  
					
					 new BitmapUploadUtil(this).uploadBitmapToServer(bitmap, type, iv_success_flag3);
		             flag3 = true;
					break;
			 }
             //BitmapUploadUtil.compressBmpToFile(bitmap);
            
	         
			
         } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e); 
               
         }  
	}
	
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){

	    Intent intent = new Intent("com.android.camera.action.CROP");

	    intent.setDataAndType(uri, "image/*");

	    intent.putExtra("crop", "true");

	    intent.putExtra("aspectX", 1);

	    intent.putExtra("aspectY", 1);

	    intent.putExtra("outputX", outputX);

	    intent.putExtra("outputY", outputY);

	    intent.putExtra("scale", false);

	    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

	    intent.putExtra("return-data", false);

	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

	    intent.putExtra("noFaceDetection", true); // no face detection

	    startActivityForResult(intent, requestCode);

	    }

	private void uploadBitmap(int type) {
		// TODO Auto-generated method stub
		FileInputStream fis = null;				
		try {
			fis = new FileInputStream(mImgPath1);
			//Bitmap bitmap = BitmapFactory.decodeStream(fis);
			Bitmap bitmap = null;
			 switch (type) {
				case 1:
					bitmap = BitmapFactory.decodeStream(
							getContentResolver().openInputStream(mUri1));					 
					 //bitmap = BitmapUploadUtil.compressImageFromFile(mImgPath1);//对图片进行压缩
					 iv1.setImageBitmap(bitmap);  
					 new BitmapUploadUtil(this).uploadBitmapToServer(bitmap, type, iv_success_flag1);
		             flag1 = true;
					break;
				case 2:
					/* bitmap = BitmapUploadUtil.compressImageFromFile(mImgPath2);*/
					bitmap = BitmapFactory.decodeStream(
							getContentResolver().openInputStream(mUri2));
					 iv2.setImageBitmap(bitmap);  
					 new BitmapUploadUtil(this).uploadBitmapToServer(bitmap, type, iv_success_flag2);
		             flag2 = true;		     
					break;
				case 3:
					 /*bitmap = BitmapUploadUtil.compressImageFromFile(mImgPath3);*/
					bitmap = BitmapFactory.decodeStream(
							getContentResolver().openInputStream(mUri3));
					 iv3.setImageBitmap(bitmap); 
					 new BitmapUploadUtil(this).uploadBitmapToServer(bitmap, type, iv_success_flag3);
		             flag3 = true;
					break;
			 }
			
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

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}
	
	TextWatcher textWatcher = new TextWatcher() {
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
			if (ed_name.getText().toString() != null && !ed_name.getText().toString().equals("")) {
				img_clear.setVisibility(View.VISIBLE);
			} else {
				img_clear.setVisibility(View.INVISIBLE);
			}

		}
	};

	TextWatcher textWatcherpwd = new TextWatcher() {
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
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}
		}
	};
	
public void doDialogListener(final CustomDialog dialog, final Uri uri, final int reqCode1, final int reqCode2){
		
		dialog.show();
		Log.d("Test", "真是醉了");
		
		dialog.findViewById(R.id.tv_photograph).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

				startActivityForResult(intent, reqCode1);
				
			}
		});
		// 从图库中获取图片
		dialog.findViewById(R.id.tv_gallery).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);  
                  
                intent2.setType("image/*");             
                
                intent2.putExtra("crop", true);
                intent2.putExtra("aspectX", 1);
                intent2.putExtra("aspectY", 1);
                intent2.putExtra("outputY", 400);
                intent2.putExtra("scale", false);
                intent2.putExtra("return-data", false);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent2.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent2.putExtra("noFaceDetection", true);
                startActivityForResult(intent2, reqCode2);  */
				 Intent intent = new Intent("android.intent.action.PICK");               
				 intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");               
				 intent.putExtra("output", uri);               
				 intent.putExtra("crop", "true");               
				 intent.putExtra("aspectX", 1);// 裁剪框比例               
				 intent.putExtra("aspectY", 1);               
				 intent.putExtra("outputX", 600);// 输出图片大小               
				 intent.putExtra("outputY", 600);               
				 startActivityForResult(intent, reqCode2);
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

	@SuppressLint("ResourceAsColor")
	private void initView() {
		iv_success_flag1 = (ImageView) findViewById(R.id.iv_success_flag1);
		iv_success_flag1.setVisibility(View.INVISIBLE);
		iv_success_flag2 = (ImageView) findViewById(R.id.iv_success_flag2);
		iv_success_flag3 = (ImageView) findViewById(R.id.iv_success_flag3);
		
		mFilePath = Environment.getExternalStorageDirectory().getPath();
		mImgPath1 = mFilePath+"/"+"img1.png";
		mImgPath2 = mFilePath+"/"+"img2.png";
		mImgPath3 = mFilePath+"/"+"img3.png";
		mUri1 = Uri.fromFile(new File(mImgPath1));
		mUri2 = Uri.fromFile(new File(mImgPath2));
		mUri3 = Uri.fromFile(new File(mImgPath3));
	    
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
		
		iv1 = (ImageView) findViewById(R.id.img01);
		iv2 = (ImageView) findViewById(R.id.img02);
		iv3 = (ImageView) findViewById(R.id.img03);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("用户信息验证", 0, 0, "取消", "");
		nextflag = getIntent().getIntExtra("nextflag", 0);
		titleBar.setOnClickTitleListener(this);
		if (nextflag == 1) {
			pro_id = getIntent().getExtras().getInt("id");
		}
		btn_next = (Button) findViewById(R.id.btn_next);
		ed_name = (EditText) findViewById(R.id.ed_name);
		ed_card = (EditText) findViewById(R.id.ed_card);
		btn_next.setOnClickListener(this);
		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);
		
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
	
		ed_name.addTextChangedListener(textWatcher);
		ed_card.addTextChangedListener(textWatcherpwd);
		ed_name.setOnFocusChangeListener(new OnFocusChangeListener() {
	
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					if (ed_name.getText().toString() != null && !ed_name.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	
		ed_card.setOnFocusChangeListener(new OnFocusChangeListener() {
	
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					if (ed_card.getText().toString() != null && !ed_card.getText().toString().equals("")) {
						img_clear_two.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}
	
	

}
