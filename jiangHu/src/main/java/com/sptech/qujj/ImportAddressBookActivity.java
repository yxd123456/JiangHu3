package com.sptech.qujj;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.sptech.qujj.util.UploadDataToServer;
import com.sptech.qujj.util.UploadDataToServer.OnRequestFinished;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

/**
 * 导入通讯录
 * @author 叶旭东
 *
 */
public class ImportAddressBookActivity extends BaseActivity {

	Context mContext = this;
    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };   
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;    
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;    
    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    /**联系人名称**/
    private List<String> mContactsName = new ArrayList<String>();    
    /**联系人头像**/
    private List<String> mContactsNumber = new ArrayList<String>();
    /**联系人头像**/
    private List<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();	
	private DialogHelper dialogHelper;	
	private TitleBar mTitleBar;	
	private HashMap<String, Object> data = new HashMap<String, Object>();
    
/*——————————————————————————————主要逻辑——————————————————————————————*/
	   @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_contacts);
		init();
	}
    

	//点击“开启通讯录”按钮触发
    public void daoru(View v){
    	dialogHelper.startProgressDialog("正在开启中...");
    	getPhoneContacts();//获取通讯录的联系人名字和号码
   
    	doData();//处理要上传的数据
    	
    	//将数据上传到服务器
    	new UploadDataToServer(this).upload(JsonConfig.CONTACTS, data, new OnRequestFinished() {
			
			@Override
			public void success(BaseData response) {
				// TODO Auto-generated method stub
				dialogHelper.stopProgressDialog();
				Constant.FLAG_KAIQITONGXUNLU = true;
				ToastManage.showToast("通讯录导入成功");
			}
			
			@Override
			public void failure(VolleyError error) {
				// TODO Auto-generated method stub
				dialogHelper.stopProgressDialog();
				ToastManage.showToast("通讯录导入失败");
			}
		});
    }    
/*——————————————————————————————引用逻辑——————————————————————————————*/
    private void init() {
		// TODO Auto-generated method stub
		dialogHelper = new DialogHelper(this);
		mTitleBar = (TitleBar) findViewById(R.id.contacts_titleBar);
		mTitleBar.bindTitleContent("导入通讯录", R.drawable.jh_back_selector, 0, "", "");
		mTitleBar.setOnClickTitleListener(new OnClickTitleListener() {
			
			@Override
			public void onRightButtonClick(View view) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLeftButtonClick(View view) {
				// TODO Auto-generated method stub
				ActivityJumpManager.finishActivity(ImportAddressBookActivity.this, 1);
			}
		}); 
	}

    private void doData() {
		// TODO Auto-generated method stub
		StringBuilder builder1 = new StringBuilder();
		for (int i = 0; i < mContactsName.size(); i++) {
			builder1.append(mContactsName.get(i)+",");
		}
		String str1 = builder1.toString();
		StringBuilder builder2 = new StringBuilder();
		for (int i = 0; i < mContactsNumber.size(); i++) {
			builder2.append(mContactsNumber.get(i)+",");
		}
		String str2 = builder1.toString();
		data.put("name", str1);
		data.put("phones", str2);
	}

	/**得到手机通讯录联系人信息**/
    @SuppressLint("NewApi")
	private void getPhoneContacts() {
	ContentResolver resolver = mContext.getContentResolver();

	// 获取手机联系人
	Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);


	if (phoneCursor != null) {
	    while (phoneCursor.moveToNext()) {

		//得到手机号码
		String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
		//当手机号码为空的或者为空字段 跳过当前循环
		if (TextUtils.isEmpty(phoneNumber))
		    continue;
		
		//得到联系人名称
		String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
		
		//得到联系人ID
		Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

		//得到联系人头像ID
		Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
		
		//得到联系人头像Bitamp
		Bitmap contactPhoto = null;

		//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		if(photoid > 0 ) {
		    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
		    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		    contactPhoto = BitmapFactory.decodeStream(input);
		}else {
		    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		}
		
		mContactsName.add(contactName);
		mContactsNumber.add(phoneNumber);
		mContactsPhonto.add(contactPhoto);
	    }

	    phoneCursor.close();
	}
    }
	
}
