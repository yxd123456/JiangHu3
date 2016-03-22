package com.sptech.qujj.http;

/**
 * 
 * @author yebr
 * 
 */
public class JsonConfig {
	public static boolean DEBUG = true;
	public static int PAGECNT;
	// 本地测试地址
	public final static String ROOT = "http://hzzn.f3322.net:9638/api";//
	public final static String HTML = "http://hzzn.f3322.net:9636/";

	// 生产环境地址
	// public final static String ROOT = "http://api.99ji.cn:9780/api";
	// public final static String HTML = "http://qu.99ji.cn/";

	// public final static String ROOT = "http://114.55.51.103:9780/api";
	// public final static String HTML = "http://114.55.51.103/";
	
	public static String EXCLUSIVE_SERVICE;//专属客服
	
	public static String DELETE_CREDIT_CARD;//删除信用卡

	public static String ADD_REPAYMENT_APPLY_CODE; // 手动申请 验证码
	public static String GETBANNERLIST;// 轮播图
	public static String CODEREG; // 获取验证码
	public static String REGISTER; // 注册
	public static String LOGIN; // 登录
	public static String REGISTERCODE;
	public static String REGISTERS;
	public static String UPDATEPWD;
	public static String UPDATEPWDCODE;
	public static String PRODUCTLIST; // 理财产品列表
	public static String PRODUCT_DETAIL;// 产品详情
	public static String PRODUCT_PAY;// 支付
	public static String REALNAMECHECKCODE;
	public static String REALNAMECHECK;
	public static String REALNAMEAUTHEN;
	public static String UPDATEUSERPWD;
	public static String UPDATAPAYPWDCODE;
	public static String UPDATEPAYPWDCHECK;
	public static String UPDATEPAYPWD;
	public static String GETUSERINFO;
	public static String ACCOUNTMONEY;// 账户余额
	public static String UPLOADFACE;
	public static String LOGOUT;
	public static String MAILLIST;
	public static String ADDMAIL;
	public static String SETPAYPWD;// 设置交易密码

	public static String GET_USER_RED;// 获取用户可用红包
	public static String ENTER_PAY;// 确认支付信息
	public static String ORDER_PAY;// 支付订单
	public static String USER_ASSET;// 理财资产
	public static String USER_EARLY;// 赎回
	public static String CHECK_EARLY;// 完成赎回操作
	public static String GET_PRODUCT_BUY_NUM;// 购买人数
	public static String USER_ASSET_DETAIL;// 交易详情
	public static String USER_RECHARGE;// 充值 - 提交
	public static String USER_RECHARGE_CONFIRM;// 充值 - 确认
	public static String USER_CASHOUT;// 提现 -提交
	public static String USER_CASHOUT_CONFIRM;// 提现 - 确认
	public static String USER_RECHARGE_DETAIL;// 充值明细
	public static String PRODUCT_PROFIT;// 理财收益
	public static String CONTACTS;

	public static String BANKCARDLIST;
	public static String BANKCARDCODE;
	public static String ADDBANKCARD;
	public static String SUPPORTBANKLIST;
	public static String USERBILLS;
	public static String USERBILLSS;
	public static String DELETEBANKCARD;
	public static String DELMAIL;
	// public static String DOWNLOADMAIL;
	// public static String ASYNCDOWNLOAD;
	public static String BINGCREDITCARD;
	public static String FEEDBACK;
	public static String BILLDETAIL;
	public static String OPENMAILRECORD;
	public static String REAPYMENT;
	public static String MESSAGEDETAIL;
	public static String MESSAGELIST;
	public static String READMESSAGE;
	public static String USERBILLDETAIL;
	public static String BANKOTHERLIST;
	public static String SETREMIND;
	public static String GETREMINDSET;
	public static String STATUSREADMESSAGE;
	public static String AUTOUPDATE;
	public static String SETTINGPUSH;
	public static String CARD_LIST;// 首页 账单列表
	public static String RIGHTAWAY_REPAYMENT; // 首页判断 信用卡一键还款时的跳转;
	                                          // /repayment/rightaway_repayment

	public static String GET_SHARE;// 获取分享的配置
	public static String DEL_ORDER; // 放弃购买 /product/del_order
	public static String GET_AD; // 广告位

	// 代还款申请
	public static String APPLY_REPAYMENT;// 代还款申请
	public static String MY_LOAN_LIST;// (is_status:5,6,7,8) 代还款记录
	public static String INSERT_APPLY_EPAYMENT;// 提交申请
	// public static String APPLY_INTERST;// 代还款申请利，手续费

	// 还款明细
	public static String REPAYMENT_DETAIL;
	public static String UNITE_PAY; // 获取支付信息 统一
	public static String REPAY_LOAN; // 确认还款
	public static String FINISH_PAY;// 完成还款支付
	// mail/start_download 账单下载
	public static String START_DOWNLOAD; // 开始下载
	public static String SET_MAIL_UIDL;// 上传邮箱唯一标识
	public static String END_DOWNLOAD;// 结束下载
	public static String MAIL_ACCOUNT_CHECK;// 邮箱账号校验

	public static String CARD_DETIAL;// 信用卡详情
	public static String OVER_REPAYMENT;// 手动更改 账单状态

	public static String ACCOUNT_MONEY_DETAIL;// 余额明细
	
	

	/*
	 * 更换的接口
	 */
	public static String START_RECHARGE;// 启动充值
	public static String START_CASHOUT;// 启动提现
	
	//借你钱申请
	public static String BORROW_MONEY_APPLY;
	public static String BORROW_MONEY_DETAIL;//借款详情
	//public static String REPAYMENT_DETAIL;//借款详情
	public static String BORROW_MONEY_LIST;//借款明细
	public static String BORROW_MONEY_INDEX;//借你钱-首页
	public static String BORROW_MONEY_REPAYMENT;;//借你钱-还款中
	public static String SINGLE_PAY;;//单笔支付
	
	public static String PICUPLOAD;
	static {
		PAGECNT = 200;
		// DOWNLOAD_MAIL_STATUS = SERVICE + "/async/download_mail_status";
		// ADD_POP_SERVER = ROOT + "/user/add_pop_server";
		
		
		EXCLUSIVE_SERVICE = ROOT + "/user/weixin_service";

		LOGIN = ROOT + "/index/login";
		REGISTER = ROOT + "/index/register_check";
		REGISTERCODE = ROOT + "/index/register_code";
		REGISTERS = ROOT + "/index/register";
		UPDATEPWD = ROOT + "/index/update_user_pwd";
		UPDATEPWDCODE = ROOT + "/index/update_user_pwd_code";
		UPDATEUSERPWD = ROOT + "/user/update_user_pwd";
		LOGOUT = ROOT + "/index/logout";
		FEEDBACK = ROOT + "/index/set_opinion";
		MESSAGELIST = ROOT + "/index/get_message_list";
		MESSAGEDETAIL = ROOT + "/index/get_message_info";
		READMESSAGE = ROOT + "/index/read_message";
		STATUSREADMESSAGE = ROOT + "/index/status_read_message";
		AUTOUPDATE = ROOT + "/index/auto_update";
		/**
		 * 首页
		 * 
		 */
		CARD_LIST = ROOT + "/repayment/card_list";
		RIGHTAWAY_REPAYMENT = ROOT + "/repayment/rightaway_repayment";
		GET_SHARE = ROOT + "/user/get_share";
		GET_AD = ROOT + "/user/get_ad"; // 广告
		/**
		 * 个人
		 */
		REALNAMECHECKCODE = ROOT + "/user/realname_authentication_code";
		REALNAMECHECK = ROOT + "/user/realname_authentication_check";
		REALNAMEAUTHEN = ROOT + "/bankcard/realname_authentication1";
		UPDATAPAYPWDCODE = ROOT + "/user/update_pay_pwd_code";
		UPDATEPAYPWDCHECK = ROOT + "/user/update_pay_pwd_check";
		UPDATEPAYPWD = ROOT + "/user/update_pay_pwd";
		GETUSERINFO = ROOT + "/user/get_user_info";
		UPLOADFACE = ROOT + "/user/face_upload";
		PICUPLOAD = ROOT + "/user/pic_upload";
		MAILLIST = ROOT + "/user/mail_list";
		ADDMAIL = ROOT + "/user/add_mail";
		DELMAIL = ROOT + "/user/del_mail";
		// DOWNLOADMAIL = ROOT + "/user/download_mail";
		OPENMAILRECORD = ROOT + "/user/open_getmailrecord";

		SETPAYPWD = ROOT + "/user/set_pay_pwd";
		SETREMIND = ROOT + "/user/set_user_remind_setting";
		GETREMINDSET = ROOT + "/user/get_user_setting";
		SETTINGPUSH = ROOT + "/user/set_push_setting";
		/**
		 * 理财中心
		 */
		PRODUCTLIST = ROOT + "/product/product_list";
		// PRODUCT_INFO = ROOT + "/product/product_info";
		PRODUCT_DETAIL = ROOT + "/product/product_detail";
		// PRODUCT_BUY = ROOT + "/product/product_buy";
		PRODUCT_PAY = ROOT + "/product/product_pay";
		ENTER_PAY = ROOT + "/product/enter_pay";
		GET_USER_RED = ROOT + "/product/get_user_red";

		ENTER_PAY = ROOT + "/product/enter_pay";
		ORDER_PAY = ROOT + "/product/order_pay";
		USER_ASSET = ROOT + "/product/user_asset";
		USER_EARLY = ROOT + "/product/user_early";
		CHECK_EARLY = ROOT + "/product/check_early";
		PRODUCT_PROFIT = ROOT + "/product/product_profit"; // 理财收益

		GET_PRODUCT_BUY_NUM = ROOT + "/product/get_product_buy_num";
		USER_ASSET_DETAIL = ROOT + "/product/user_asset_detail";

		USER_RECHARGE = ROOT + "/money/user_recharge";// 充值 - 提交
		USER_RECHARGE_CONFIRM = ROOT + "/money/user_recharge_confirm"; // 充值 -确认
		USER_CASHOUT = ROOT + "/money/user_cashout"; // 提现
		USER_CASHOUT_CONFIRM = ROOT + "/money/user_cashout_confirm"; // 提现- 确认
		USER_RECHARGE_DETAIL = ROOT + "/money/user_recharge_detail"; // 充值明细

		DEL_ORDER = ROOT + "/product/del_order"; // /product/del_order
		
		CONTACTS = ROOT + "/user/import_user_address_list";

		/**
		 * 账户余额
		 */
		ACCOUNTMONEY = ROOT + "/money/account_money";
		USERBILLS = ROOT + "/user/apply_progress";
		BILLDETAIL = ROOT + "/user/apply_progress_detail";
		USERBILLSS = ROOT + "/money/user_bills";
		USERBILLDETAIL = ROOT + "/money/user_bills_detail";

		/**
		 * 银行卡
		 */
		DELETE_CREDIT_CARD = ROOT+"/bankcard/delete_credit_card";
		BANKCARDLIST = ROOT + "/bankcard/get_bank_card_list";
		BANKCARDCODE = ROOT + "/bankcard/add_bank_card_code";
		ADDBANKCARD = ROOT + "/bankcard/add_bank_card";
		SUPPORTBANKLIST = ROOT + "/bankcard/support_bank_list";
		DELETEBANKCARD = ROOT + "/bankcard/delete_bank_card";
		BINGCREDITCARD = ROOT + "/bankcard/bind_credit_card";
		BANKOTHERLIST = ROOT + "/bankcard/support_other_bank_list";
		/**
		 * 邮箱导入
		 */
		// ASYNCDOWNLOAD = ROOT + "/async/download_mail_status";
		/**
		 * 手动添加代还款申请
		 */
		REAPYMENT = ROOT + "/repayment/manual_repayment_apply";
		// 手动申请验证码
		ADD_REPAYMENT_APPLY_CODE = ROOT + "/repayment/add_repayment_apply_code";

		// 代还款申请
		APPLY_REPAYMENT = ROOT + "/repayment/apply_repayment"; // 代还款申请
		MY_LOAN_LIST = ROOT + "/repayment/my_loan_list"; // 代还款记录
		INSERT_APPLY_EPAYMENT = ROOT + "/repayment/insert_apply_epayment";// 提交申请

		REPAYMENT_DETAIL = ROOT + "/repayment/repayment_detail";// 还款明细

		UNITE_PAY = ROOT + "/money/unite_pay"; // 支付信息同意接口

		REPAY_LOAN = ROOT + "/repayment/repay_loan";// 确认还款

		FINISH_PAY = ROOT + "/repayment/finish_pay";// 完成还款支付

		//测试邮箱
		 START_DOWNLOAD =
		 "http://hzzn.f3322.net:9638/bill/analysis/start_download"; // 账单下载
		
		 END_DOWNLOAD =
		 "http://hzzn.f3322.net:9638/bill/analysis/end_download";// 结束下载
		
		 SET_MAIL_UIDL =
		 "http://hzzn.f3322.net:9638/bill/analysis/set_mail_uidl"; // 上传邮箱唯一标识
		
		 MAIL_ACCOUNT_CHECK =
		 "http://hzzn.f3322.net:9638/bill/analysis/mail_account_check"; //
		// 邮箱账号校验
		
		//账单下载SSLSocket
//		START_DOWNLOAD = "http://bill.99ji.cn:9780/bill/analysis/start_download"; // //账单下载
//
//		END_DOWNLOAD = "http://bill.99ji.cn:9780/bill/analysis/end_download";// 结束下载
//
//		SET_MAIL_UIDL = "http://bill.99ji.cn:9780/bill/analysis/set_mail_uidl"; // 上传邮箱唯一标识
//
//		MAIL_ACCOUNT_CHECK = "http://bill.99ji.cn:9780/bill/analysis/mail_account_check"; // 邮箱账号校验

	

		CARD_DETIAL = ROOT + "/repayment/card_detial"; // 信用卡详情

		OVER_REPAYMENT = ROOT + "/repayment/over_repayment"; // 手动更改账单状态

		ACCOUNT_MONEY_DETAIL = ROOT + "/money/account_money_detail";// 余额明细

		START_RECHARGE = ROOT + "/money/start_recharge";// 启动充值

		START_CASHOUT = ROOT + "/money/start_cashout";// 启动提现
		
		BORROW_MONEY_APPLY = ROOT + "/borrow/borrow_money_apply";//借你钱申请
		
		BORROW_MONEY_DETAIL = ROOT + "/borrow/borrow_money_detail";//借款详情
		
		BORROW_MONEY_LIST = ROOT + "/borrow/borrow_money_list";//借款明细
		
		BORROW_MONEY_INDEX = ROOT + "/borrow/borrow_money_index";//借你钱首页
		
		BORROW_MONEY_REPAYMENT = ROOT + "/borrow/borrow_money_repayment ";//借你钱-还款中
		
		SINGLE_PAY = ROOT + "/payment/single_pay";//单笔支付，还款

	}

}
