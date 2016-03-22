package com.sptech.qujj.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ant.liao.GifView;
import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogImportMaillError;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.DownloadMail;
import com.sptech.qujj.model.Downloadmailstaus;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Md5;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.ImportmailErrorListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 
 * 导入邮箱
 * 
 * @author gusonglei
 * 
 */

public class AddMaildoingActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {

	private TitleBar titleBar;

	private RelativeLayout rale_checkmail, rale_success, rale_nodata, rale_failed, rale_nonetwork;// 验证邮箱，
	                                                                                              // 导入中，没有账单数据，失败，网络问题
	private TextView tv_mail;
	private String mailname;
	private SharedPreferences spf;
	private int mailId;
	private Button btn_agin, btn_changemail, btn_handapply, btn_handapply1;
	private GifView gf1, gif_checkview;
	private DialogHelper dialogHelper;
	private final int SHOW_TOAST = 1;
	private TextView tv_failhint;
	private int curdownloadnum;
	private int doingnum = 0;
	private int responsenum = 0;
	private DownTimer downTimer = new DownTimer(60 * 1000, 1000);
	private TwoTimer twoTimer = new TwoTimer(90 * 1000, 1000);
	// private String Port = "";
	// private String Host = "";

	// private int cur_status;
	private String CurStart = "";// 记录当前连接状态

	private final int ASYNCDOWNLOAD = 1;
	private final int RECEIVE = ASYNCDOWNLOAD + 1;
	private final int PASSERROR = RECEIVE + 1;
	private final int NODATA = PASSERROR + 1;
	private final int DOWNFAIL = NODATA + 1;
	private final int END_DOWNLOAD = DOWNFAIL + 1;// 结束下载
	private final int SUCCESS = END_DOWNLOAD + 1;// 下载邮件成功
	private final int SETMAILUIDL = SUCCESS + 1;// 上传未下载的邮箱唯一标识

	private final int CHECKMAIL = SETMAILUIDL + 1; // 校验邮箱
	private final int ERRORDIALOG = CHECKMAIL + 1;
	List<DownloadMail> allDownloadMails = new ArrayList<DownloadMail>(); // 过滤过后的可以下载的
	                                                                     // 邮件
	List<String> allUidList = new ArrayList<String>();// 所有未下载的UIdL
	List<String> curUidList = new ArrayList<String>();// 所有未下载的UIdL(剔除后的)
	ArrayList<String> subject_filter;// curdownloadmailstaus.getSubject_filter();
	Map<String, String> uidMap;// curdownloadmailstaus.getUidl();

	private String test = "";
	private Downloadmailstaus curdownloadmailstaus;

	private String host = ""; // dialog 弹出框里 host
	private int port; // 弹出框里 port
	private int issafe; // 弹出框里 issafe

	// 邮件上传实际符合总数
	private int mailcount;
	private boolean checkDone = false;
	StringBuffer sb = new StringBuffer();// 邮件内容

	boolean stopThread = false;// 停止Top flag
	boolean finished = false;
	private int errornum = 0;

	private DialogImportMaillError dime;

	// private SocketThread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_slidebar_daomail);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("导入账单", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		dialogHelper = new DialogHelper(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		mailname = getIntent().getStringExtra("emailname");
		mailId = getIntent().getIntExtra("mailId", 0);

		tv_failhint = (TextView) findViewById(R.id.tv_failhint);
		rale_success = (RelativeLayout) findViewById(R.id.rale_success);
		rale_checkmail = (RelativeLayout) findViewById(R.id.rale_checkmail);
		rale_nodata = (RelativeLayout) findViewById(R.id.rale_nodata);
		rale_failed = (RelativeLayout) findViewById(R.id.rale_failed);
		rale_nonetwork = (RelativeLayout) findViewById(R.id.rale_nonetwork);
		btn_agin = (Button) findViewById(R.id.btn_agin);
		btn_handapply = (Button) findViewById(R.id.btn_handapply);
		btn_handapply1 = (Button) findViewById(R.id.btn_handapply1);
		btn_changemail = (Button) findViewById(R.id.btn_changemail);
		btn_agin.setOnClickListener(this);
		btn_changemail.setOnClickListener(this);
		btn_handapply.setOnClickListener(this);
		btn_handapply1.setOnClickListener(this);
		gf1 = (GifView) findViewById(R.id.gif_view);
		gf1.setGifImage(R.drawable.img_mail_loadinggreen111);
		gif_checkview = (GifView) findViewById(R.id.gif_checkview);
		gif_checkview.setGifImage(R.drawable.img_mail_test);
		tv_mail = (TextView) findViewById(R.id.tv_mail);
		tv_mail.setText("当前邮箱：" + mailname);
		// rale_failed.setVisibility(View.VISIBLE);
		rale_checkmail.setVisibility(View.VISIBLE);
		// rale_success.setVisibility(View.VISIBLE);
		// rale_nodata.setVisibility(View.VISIBLE);
		// rale_nonetwork.setVisibility(View.VISIBLE);
		getBill(mailId);
		// receive(null);
		// receive("", "", "");

	}

	// // 定时器暂停的时候重调用
	// private void onPaus() {
	// // TODO Auto-generated method stub
	//
	// System.out.println("onPaus=== !!!!!");
	// if (downTimer != null) {
	// downTimer.start();
	// }
	//
	// }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_agin: // 消息设置
			finish();
			break;
		case R.id.btn_changemail:
			if (twoTimer != null) {
				twoTimer.cancel();
			}
			if (downTimer != null) {
				downTimer.cancel();
			}
			finish();
			break;
		case R.id.btn_handapply:
			// Intent intent = new Intent(AddMaildoingActivity.this,
			// HandapplyActivity.class);
			// startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			ActivityJumpManager.jumpToMain(AddMaildoingActivity.this, MainActivity.class);
			if (twoTimer != null) {
				twoTimer.cancel();
			}
			break;
		case R.id.btn_handapply1:
			// Intent intent2 = new Intent(AddMaildoingActivity.this,
			// HandapplyActivity.class);
			// startActivity(intent2);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			ActivityJumpManager.jumpToMain(AddMaildoingActivity.this, MainActivity.class);
			if (twoTimer != null) {
				twoTimer.cancel();
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		System.out.println("按下了back键 onKeyDown()");
		stopMyThread();
		if (dime != null) {
			dime.closeDialog();
		}
		ActivityJumpManager.finishActivity(AddMaildoingActivity.this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		// Object obj = new QDecoderStream("");

	}

	private void getBill(int getid) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍后...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("account_id", getid);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.START_DOWNLOAD, params, BaseData.class, null, userSuccessListener, errorListener());
	}

	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> userSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			// System.out.println("开始下载 返回的code= ?? " + response.code);
			if (response.code.equals("0")) {
				// handler.sendMessage(handler.obtainMessage(ASYNCDOWNLOAD, 0));
				System.out.println("开始下载返回的 数据==  " + response.data);
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					String dataString = new String(b);
					System.out.println("data== " + dataString);
					if (dataString != null && !dataString.equals("")) {
						// doStaus(dataString);
						Downloadmailstaus downloadmailstaus = new Downloadmailstaus();
						downloadmailstaus = JSON.parseObject(dataString, Downloadmailstaus.class);
						//
						curdownloadmailstaus = downloadmailstaus;
						if (curdownloadmailstaus != null) {
							port = curdownloadmailstaus.getPop_port();
							host = curdownloadmailstaus.getPop_host();
							issafe = curdownloadmailstaus.getPop_type();
							downSocket();
							// thread = new SocketThread();
							// thread.start();
						}
					}
				} else {
					ToastManage.showToast(response.desc);
					ActivityJumpManager.finishActivity(AddMaildoingActivity.this, 1);
				}
			}
		}
	};
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ASYNCDOWNLOAD:
				int now_status = (int) msg.obj;
				break;
			case RECEIVE:
				rale_checkmail.setVisibility(View.GONE);
				rale_success.setVisibility(View.VISIBLE);
				break;
			case CHECKMAIL:
				rale_checkmail.setVisibility(View.GONE);
				rale_success.setVisibility(View.VISIBLE);
				CheckMail();
				break;
			case PASSERROR:
				rale_checkmail.setVisibility(View.GONE);
				rale_success.setVisibility(View.GONE);
				rale_failed.setVisibility(View.VISIBLE);
				break;
			case NODATA:
				rale_checkmail.setVisibility(View.GONE);
				rale_success.setVisibility(View.GONE);
				rale_nodata.setVisibility(View.VISIBLE);
				break;
			case SUCCESS:// 下载成功
				rale_checkmail.setVisibility(View.GONE);
				rale_nodata.setVisibility(View.GONE);
				rale_success.setVisibility(View.VISIBLE);
				break;
			// 登录成功，账单下载失败，UIDL获取失败 top 出错
			case DOWNFAIL:
				rale_checkmail.setVisibility(View.GONE);
				rale_success.setVisibility(View.GONE);
				rale_nodata.setVisibility(View.GONE);
				rale_failed.setVisibility(View.VISIBLE);
				tv_failhint.setText("账单下载失败,请60秒后重新抓取账单");
				break;
			case ERRORDIALOG:
				dime = new DialogImportMaillError(AddMaildoingActivity.this, curdownloadmailstaus.getAccount(), issafe, String.valueOf(port), host, new ImportmailErrorListener() {
					@Override
					public void ImportmailErrorListener(String diahost, String diaport, boolean check) {
						// TODO
						// 判断 是否修改
						// 修改后继续 socket 访问
						// 保存修改过后的host,port,issafe;
						host = diahost;
						port = Integer.parseInt(diaport);
						if (check) {
							issafe = 1;
						} else {
							issafe = 0;
						}
						System.out.println("diahost== " + host);
						System.out.println("diaport== " + port);
						System.out.println("diacheck== " + issafe);
						downSocket();
					}
				});
				dime.createMyDialog();
				break;
			// case END_DOWNLOAD:// 结束下载，上传邮件
			// // String uidl = (String) msg.obj;
			// DownloadMail downloadMail = (DownloadMail) msg.obj;
			// // System.out.println("curMailText== " + curMailText);
			// // System.out.println("uidl== " + downloadMail.getUidl());
			// EndDownload(downloadMail);// mail_content
			// break;

			case END_DOWNLOAD:
				int allDownMailNum = allDownloadMails.size();
				// System.out.println("Handler--END_DOWNLOAD所有可下载的mail Size == "
				// + allDownMailNum);
				EndDownload(0);
				break;
			case SETMAILUIDL: // 上传 未下载的所有uidL
				List<String> curmaiList = (List<String>) msg.obj;
				if (curmaiList.size() != 0) {
					setMailUidl(curmaiList);
				}
				break;
			default:
				break;
			}
		}
	};

	// private void downSocket() {
	// thread = new Thread(new Runnable() {

	private void downSocket() {
		Thread thread = new Thread(new Runnable() {

			@SuppressWarnings("unused")
			@Override
			public void run() {
				// private class SocketThread extends Thread {
				// public volatile boolean exit = false;
				//
				// @SuppressWarnings("unused")
				// @Override
				// public void run() {
				// while (!exit) {
				// handler.sendEmptyMessage(RECEIVE);

				// while (!stopThread) {
				System.out.println("downSocket() 连接 Start-port==" + port);
				System.out.println("downSocket() 连接 Start-host==" + host);
				System.out.println("downSocket() 连接 Start-issafe==" + issafe);
				String account = curdownloadmailstaus.getAccount();// 用户名
				String password = curdownloadmailstaus.getPassword();// 密码
				int is_check = curdownloadmailstaus.getIs_check();// 是否校验
				subject_filter = curdownloadmailstaus.getSubject_filter();// 正则
				uidMap = curdownloadmailstaus.getUidl();// uidMap
				System.out.println("downSocket() 连接 account==" + account);
				System.out.println("downSocket() 连接 password==" + password);
				Socket s = null;

				SSLSocket ssl = null;
				String start = "";// 连接pop服务器返回情况
				BufferedReader sockin = null;
				PrintWriter out = null;

				try {
					// InetAddress address = InetAddress.getByName(host);
					if (issafe == 1) {
						downTimer.start();
						SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
						s = factory.createSocket(host, port);
						// s.connect(new InetSocketAddress(address, port),
						// 8000);
						// ssl = (SSLSocket) factory.createSocket(host,
						// port);
						System.out.println("ok");
						// s.setSoTimeout(120 * 1000);
						// 缓存输入和输出
						System.out.println("安全连接 Start-S==");
					} else {
						downTimer.start();
						s = new Socket(host, port);
						// s = new Socket();
						// s.connect(new InetSocketAddress(address, port),
						// 8000);
						// s.setSoTimeout(120 * 1000);// 2 way
						System.out.println("不安全连接 Start");
					}
					// 将socket对应的输入流包装秤BufferedReader
					out = new PrintWriter(s.getOutputStream(), true);
					sockin = new BufferedReader(new InputStreamReader(s.getInputStream()));
					// out = new PrintWriter(s.getOutputStream(), true);
					// sockin = new BufferedReader(new
					// InputStreamReader(s.getInputStream()));
					start = sockin.readLine();
					System.out.println(" Start-S==" + start);

					// 判断连接是否成功
					if (checkOk(start)) {
						checkDone = true;
						// 邮箱登录
						// 将用户名发送到POP3服务程序
						// out.println("USER " + account);
						out.write("USER " + account + "\r\n");
						out.flush();
						String user_reply = "";
						user_reply = sockin.readLine();
						System.out.println("用户名user-S:" + user_reply);
						// 判断用户名输入是否成功
						if (checkOk(user_reply)) {
							System.out.println("密码");
							out.write("PASS " + password + "\r\n");
							out.flush();
							// out.println("PASS " + password);
							String pass = "";
							pass = sockin.readLine();
							System.out.println("密码pass-S:" + pass);
							if (checkOk(pass)) {
								// 登录成功就 校验邮箱
								if (is_check != 1) {
									handler.sendEmptyMessage(CHECKMAIL);
								} else {
									handler.sendEmptyMessage(RECEIVE);
								}
								// 过滤UIDL
								// out.println("UIDL");
								out.write("UIDL " + "\r\n");
								out.flush();
								String allmail = "";
								allmail = sockin.readLine();
								System.out.println("UIDL-S:" + allmail);
								if (checkOk(allmail)) {
									do {
										String reply = sockin.readLine();
										if (reply != null && reply.length() > 0)
											if (".".equals(reply))
												break;
										// System.out.println("UIDL-all id: "
										// +
										// reply);
										String[] allsubject = reply.split(" ");
										if (uidMap != null) {
											if (!uidMap.containsKey(allsubject[1])) {
												allUidList.add(reply);
												// curUidList.add(allsubject[1]);
												System.out.println("未下载 UIDL-all id: " + reply);
											}
										} else {// uidMap 为空则全部添加
											allUidList.add(reply);
											// curUidList.add(allsubject[1]);
											System.out.println("全部都是未下载的 UIDL-all id" + reply);
										}

									} while (true);
									System.out.println("循环checkTop-- allUid Size== " + allUidList.size());
									boolean getSubject = false;
									if (allUidList.size() != 0) {
										twoTimer.start();
									}
									// 循环top未下载的 邮件
									if (allUidList.size() != 0) {
										for (int i = 0; i < allUidList.size(); i++) {
											if (!stopThread) {
												try {
													CheckTop(allUidList, i, out, sockin, false);
												} catch (Exception e) {
													// TODO: handle exception
													System.out.println("for 循环跳出");
												}
												System.out.println("已经top过的 uid 数量 = " + (i + 1));
											}

										}
									}

									System.out.println("所有uid 数量 = " + allUidList.size());
									if (allDownloadMails.size() != 0) {
										handler.sendEmptyMessage(END_DOWNLOAD);
									} else {
										if (errornum == 5) {
											handler.sendEmptyMessage(DOWNFAIL);
										} else {
											handler.sendEmptyMessage(NODATA);
										}

									}
									// System.out.println("所有的未下载的UIDl- Size = "
									// + curUidList.size());
									System.out.println("finished= " + finished);
									if (!finished) {
										handler.sendMessage(handler.obtainMessage(SETMAILUIDL, curUidList));
									}
									out.close();
									sockin.close();
								} else {// uidl错误
									System.out.println("uidl错误");
									handler.sendEmptyMessage(DOWNFAIL);
								}
							} else {// 密码输入后 错误，页面提示密码错误呀
								System.out.println("密码输入错误");
								handler.sendEmptyMessage(PASSERROR);//
							}
						} else {// 用户名输入Error,页面提示错误
							System.out.println("用户名输入Error");
							handler.sendEmptyMessage(PASSERROR);//
						}
					} else {
						System.out.println("连接不成功！！弹出pop 设置弹出框 ");
					}

				} catch (SocketTimeoutException e) {
					System.out.println("超时！！");
					// handler.sendEmptyMessage(ERRORDIALOG);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("UnknownHostException-111uidl错误");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("IOException-222uidl错误");
					// handler.sendEmptyMessage(DOWNFAIL);
					// handler.sendEmptyMessage(NODATA);
					// 连接不成功，弹出pop 设置弹出框
					// handler.sendEmptyMessage(ERRORDIALOG);
				}
				System.out.println("333uidl 结束");
				// handler.sendEmptyMessage(DOWNFAIL);
				// if (rale_success.isShown()) {
				// handler.sendEmptyMessage(DOWNFAIL);
				// }
				// // 循环上传结束

				System.out.println(" 上传的UIdL size== " + curUidList.size());

			}
		});

		thread.start();

	}

	private void CheckTop(List<String> allUidList, int i, PrintWriter out, BufferedReader sockin, boolean agin) {
		String titleString = "";// 解码后的标题
		String subject = "";// 解码前的标题
		boolean getSubject = false;
		System.out.println("TOP i== " + i);
		System.out.println("TOP 开始-- allUidList.get(i) == " + allUidList.get(i));

		try {
			String[] allsubject = allUidList.get(i).split(" ");
			// + allsubject[0]);
			String topmsg = "TOP " + allsubject[0] + " 30";
			System.out.println("topmsg == " + topmsg + "\r\n");
			// out.println(topmsg + "\r\n");
			out.write(topmsg + "\r\n");
			// out.write(topmsg + "\r\n");
			out.flush();
			String top30mailstatus = "";
			top30mailstatus = sockin.readLine() + "";
			System.out.println("top30--S == " + top30mailstatus);
			if (checkOk(top30mailstatus)) {
				curUidList.add(allsubject[1]);
				do {
					String replytop = sockin.readLine();
					if (replytop != null && replytop.length() > 0)
						if (".".equals(replytop))
							break;
					// 截取标题
					if (getSubject) {
						if (replytop.startsWith(" =?")) {
							// System.out.println("－－addSubject －－　"
							// + replytop);
							subject = subject.concat(replytop);
						} else {
							getSubject = false;
						}
					}
					if (replytop.startsWith("Subject:")) {
						getSubject = true;
						subject = replytop;
					}
				} while (true);
				titleString = getTitle(subject);
				System.out.println("通过UId过滤的id= " + allsubject[0] + "UIdL " + allsubject[1] + " 标题解码后 ==== " + titleString);

				if (subject_filter != null) {
					boolean checksub = false;
					String curMailText = "";
					sb = new StringBuffer();
					for (int j = 0; j < subject_filter.size(); j++) {
						// System.out.println("正则j ---" +
						// subject_filter.get(j));
						checksub = CheckUtil.checkSubject(subject_filter.get(j), titleString);
						System.out.println("check == " + checksub);
						System.out.println("过滤 ！！titleString == " + titleString + "id ==  " + allsubject[0]);
						if (checksub) {
							int count = 0;// 总行数
							// 标题通过验证，则下载此邮件
							// RETR[Msg#]处理返回由参数标识的邮件的全部文本
							System.out.println("通过标题过滤的邮件id == " + allsubject[0] + "uidl= " + allsubject[1]);
							// out.println("RETR "
							// + allsubject[0]);
							out.write("RETR " + allsubject[0] + "\r\n");
							out.flush();
							String RETRmail = "";
							RETRmail = sockin.readLine();
							System.out.println("RETR-S:" + RETRmail);
							byte[] curByte = null;
							if (checkOk(RETRmail)) { // 获取邮件全部文本成功
								do {
									String replyRETR = "";
									replyRETR = sockin.readLine();
									System.out.println("replyRETR== " + replyRETR);
									sb.append(replyRETR.concat("\r\n"));
									if (replyRETR != null && replyRETR.length() > 0)
										if (".".equals(replyRETR))
											break;

								} while (true);
								// System.out.println("sb=== " + sb.toString());
								int countNum = sb.length() / 1048576;
								int yushu = sb.length() % 1048576;
								if (yushu > 0) {
									countNum += 1;
								}
								System.out.println("yushu== " + yushu);
								System.out.println("countNum== " + countNum);
								for (int m = 0; m < countNum; m++) {
									if (m + 1 == countNum) {
										curMailText = sb.toString().substring(m * 1048576, sb.length() - 1);
									} else {
										curMailText = sb.toString().substring(m * 1048576, 1048576 * (m + 1) - 1);
									}
									System.out.println("m === " + m);
									DownloadMail downloadmail = new DownloadMail();
									downloadmail.setUidl(allsubject[1]);
									downloadmail.setCurMailNum(m + 1);
									downloadmail.setCurMailContent(curMailText);
									downloadmail.setMailcountNum(countNum);
									if (!"".equals(curMailText)) {
										doingnum += 1;
										// handler.sendMessage(handler.obtainMessage(END_DOWNLOAD,
										// downloadmail));
										allDownloadMails.add(downloadmail);
										System.out.println("上传邮件 title = " + titleString + "  uid =" + allsubject[1]);
									}
								}
							} else {// 获取文本失败
								System.out.println("获取账单全部文本失败--剔除当前i= id = uidl= " + i + allsubject[0] + allsubject[1]);
								// handler.sendEmptyMessage(DOWNFAIL);
								System.out.println(" 当前size== " + curUidList.size());
								curUidList.remove(allsubject[1]);// 剔除当前id
								System.out.println("剔除size== " + curUidList.size());
							}
						}
					}
				}
			} else {
				errornum += 1;
				System.out.println("Top 出错 Agin-- i == " + i);
				System.out.println("停止 Top  errornum == " + errornum);
				if (errornum == 5) {
					stopThread = true;
				}
				// // Top 出错
				// if (!agin) {
				// System.out.println("Top 出错 Agin-- i == " + i);
				// // CheckTop(allUidList, i, out, sockin, true);
				// } else {
				// System.out.println(" 剔除的前size== " + curUidList.size());
				// curUidList.remove(allsubject[1]);// 剔除当前id
				// System.out.println(" 剔除的id == " + allsubject[1]);
				// System.out.println(" 剔除的后 size== " + curUidList.size());
				// }
			}

			// // 循环 i+1
			// if (++i < allUidList.size()) {
			// System.out.println("Top 下一个 -- i == " + i);
			// // CheckTop(allUidList, i, out, sockin, false);
			// } else {
			// // System.out.println("i == " + i);
			// System.out.println("i == " + allUidList.size());
			// }

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("CheckTop try 出错");
			System.out.println(e);
		}
	}

	// 上传邮件
	// private void EndDownload(DownloadMail downloadMail) {
	private void EndDownload(int i) {
		DownloadMail downloadMail = allDownloadMails.get(i);
		System.out.println("请求次数EndDownload== " + doingnum);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("account_id", mailId);
		data.put("uidl", downloadMail.getUidl());
		// data.put("mail_content", curMailText);
		data.put("mail_content", downloadMail.getCurMailContent());
		data.put("package_index", downloadMail.getCurMailNum());
		data.put("package_count", downloadMail.getMailcountNum());
		// System.out.println("data=== " + data.toString());
		System.out.println("邮件account_id== " + mailId);
		System.out.println("邮件uidl== " + downloadMail.getUidl());
		// System.out.println("邮件mail_content== " +
		// downloadMail.getCurMailContent());
		System.out.println("邮件package_index== " + downloadMail.getCurMailNum());
		System.out.println("邮件package_count== " + downloadMail.getMailcountNum());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());

		String json = JSON.toJSONString(data);
		byte[] base = android.util.Base64.encode(json.getBytes(), android.util.Base64.DEFAULT);
		String content = "";
		try {
			content = URLEncoder.encode(new String(base), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params.put("data", content);
		String con = spf.getString(Constant.UID, "").toString() + spf.getString(Constant.KEY, "").toString().toUpperCase();
		String condata = new String(base) + con;
		String sign = Md5.md5s(condata);
		params.put("sign", sign);
		// params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID,
		// "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.END_DOWNLOAD, params, BaseData.class, null, endDownloadSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> endDownloadSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			responsenum += 1;
			System.out.println("响应次数EndDownload== " + responsenum);
			if (response.code.equals("0")) {
				// handler.sendMessage(handler.obtainMessage(ASYNCDOWNLOAD, 0));
				System.out.println("上传邮件返回的 数据==  " + response.data);
				// byte[] b = Base64.decode(response.data);
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					String dataString = new String(b);
					System.out.println("data== " + dataString);
					JSONObject s = JSON.parseObject(dataString);
					int downloadnum = s.getInteger("downloadnum");
					System.out.println("downloadnum== " + downloadnum);
					mailcount += downloadnum;
				}
				System.out.println("onResponse--mailcount!!!!! == " + mailcount);
			} else {
				ToastManage.showToast(response.desc);
			}

			// 判断当前响应数量 是否等于 请求次数
			if (responsenum != doingnum) {
				EndDownload(responsenum);
				System.out.println("上传下一封 response== " + responsenum);
			} else {
				System.out.println(" 上传的UIdL size== " + curUidList.size());
				System.out.println("请求次数 = " + doingnum);
				System.out.println("errornum 次数 = " + errornum);
				if (errornum == 5) {
					System.out.println("没有可下载的邮件!!!!!!!errornum == 5 ");
					handler.sendEmptyMessage(DOWNFAIL);
				} else {
					if (mailcount == 0) {
						System.out.println("mail==0 ");
						// handler.sendEmptyMessage(NODATA);
						rale_checkmail.setVisibility(View.GONE);
						rale_success.setVisibility(View.GONE);
						rale_nodata.setVisibility(View.VISIBLE);
					} else {
						SPHelper.setRefresh(true);
						System.out.println("mail !=0 ");
						System.out.println("mailcount !=  " + mailcount);
						Intent main = new Intent(AddMaildoingActivity.this, MainActivity.class);
						main.putExtra("isShow", false);
						startActivity(main);
						if (twoTimer != null) {
							twoTimer.cancel();
						}
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				}
			}

		}
	};

	// 邮箱账号校验
	private void CheckMail() {
		String curHost = curdownloadmailstaus.getPop_host();
		int curPort = curdownloadmailstaus.getPop_port();
		int curpoptype = curdownloadmailstaus.getPop_type();
		System.out.println("host== " + host + "  curhost==" + curHost);
		System.out.println("port== " + port + "  curport==" + curPort);
		System.out.println("issafe== " + issafe + "  curissafe==" + curpoptype);
		HashMap<String, Object> data = new HashMap<String, Object>();
		// 判断dialog 返回的数据 修改没有？
		if (host.equals(curHost) && port == curPort && issafe == curpoptype) {
			data.put("account_id", mailId);
			data.put("is_check", 1);
		} else {
			data.put("account_id", mailId);
			data.put("is_check", 1);
			data.put("pop_change", 1);
			data.put("pop_host", host);
			data.put("pop_port", port);
			data.put("pop_type", issafe);
		}
		System.out.println("data== " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.MAIL_ACCOUNT_CHECK, params, BaseData.class, null, checkSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> checkSuccessListener = new Listener<BaseData>() {
		@Override
		public void onResponse(BaseData response) {
			System.out.println("邮箱账号校验成功！");
		}
	};

	// SET_MAIL_UIDL 上传邮箱唯一标识
	private void setMailUidl(List<String> allMaiList) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("account_id", mailId);
		data.put("uidl", allMaiList);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.SET_MAIL_UIDL, params, BaseData.class, null, setMailUIDSuccessListener, errorListener());
	}

	private Listener<BaseData> setMailUIDSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			// System.out.println("开始下载 返回的code= ?? " + response.code);
			if (response.code.equals("0")) {
				// handler.sendMessage(handler.obtainMessage(ASYNCDOWNLOAD, 0));
				System.out.println("上传邮箱唯一标识 成功！！");
			}

		}
	};

	//
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("onPaus=== !!!!!");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			System.out.println("按下了back键 onKeyDown()");
			stopMyThread();
			if (dime != null) {
				dime.closeDialog();
			}
			ActivityJumpManager.finishActivity(this, 1);
			// downTimertwo.cancel();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 判断ok 方法
	public boolean checkOk(String data) {
		if (data.startsWith("+OK")) {
			return true;
		}
		return false;
	}

	// 解码 标题
	public String getTitle(String subject) {
		String allTitle = "";
		subject = subject.replace("Subject: ", "");
		if ("".equals(subject)) {
			// /=\?(.*?)\?(.*?)\?(.*?)\?=/gi
			return "";
		} else {
			String[] allsubject = subject.split(" ");
			for (int j = 0; j < allsubject.length; j++) {
				try {
					System.out.println("allsubject 拆分 == " + allsubject[j]);
					String[] var = allsubject[j].split("\\?");
					// System.out.println("allsubject split == " + var);
					String decodeTitle = "";
					if (var.length == 5) {
						decodeTitle = decodeTitle(var[3], var[2], var[1]);
					} else if (var.length == 4) {
						decodeTitle = decodeTitle(var[2], var[1], "");
					} else {
						return "";
					}
					// System.out.println("! curTitle == " + decodeTitle);
					allTitle = allTitle.concat(decodeTitle);
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("getTitle try 出错 ");
				}
			}
			// System.out.println("! allTitle == " + allTitle);
		}
		return allTitle;
	}

	// // 转 utf_8
	public String getUtf8(byte[] bye, String str) {
		String dataString = "";
		try {
			String aa = new String(bye, str);
			System.out.println(str + " 转utf8 = " + aa);
			dataString = aa;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataString;
	}

	// 根据取出的 incode ,outcode , content 解码
	public String decodeTitle(String convert, String inCode, String outCode) {
		String buf = "";
		System.out.println("convert== " + convert);
		System.out.println("inCode== " + inCode);
		System.out.println("outCode== " + outCode);

		byte[] bye = null;// 转gbk
		if ("".equals(convert))
			return buf;
		switch (inCode.toUpperCase()) {
		case "QUOTED-PRINTABLE":
		case "Q":
			System.out.println("len-- convert == " + CheckUtil.qpDecoding(convert));
			bye = CheckUtil.qpDecoding(convert);
			System.out.println("len-- bye == " + bye);
			break;
		case "BASE64":
		case "B":
			// bye = Base64.decode(convert, Base64.DEFAULT);
			bye = android.util.Base64.decode(convert, android.util.Base64.DEFAULT);
			// bye = Base64.decode(convert);
			System.out.println("buf BASE64 == " + new String(bye));
			// buf = new String(Base64.decode(convert, Base64.DEFAULT));
			break;
		case "BINARY":
			bye = convert.getBytes();
			// var buf = new Buffer(_convert, 'binary');
			break;
		case "7BIT":
		case "8BIT":
			bye = convert.getBytes();
		default:
			bye = convert.getBytes();
			// var buf = new Buffer(_convert);
			break;
		}

		switch (outCode.toUpperCase()) {
		case "GBK":
		case "GB2312":
			buf = getUtf8(bye, "GBK");
			break;
		case "GB18030":
			buf = getUtf8(bye, "GB18030");
			break;
		case "CP932":
			buf = getUtf8(bye, "CP932");
			break;
		case "CP936":
			buf = getUtf8(bye, "CP936");
			break;
		case "CP949":
			buf = getUtf8(bye, "CP949");
			break;
		case "CP950":
			buf = getUtf8(bye, "CP950");
			break;
		case "BIG5":
			buf = getUtf8(bye, "BIG5");
			break;
		case "SHIFT_JIS":
			buf = getUtf8(bye, "SHIFT_JIS");
			break;
		case "EUC-JP":
			buf = getUtf8(bye, "EUC-JP");
			break;
		case "UTF8":
		case "UTF-8":
			buf = new String(bye);
			// _convert = buf.toString('utf-8');
			break;
		default:
			buf = new String(bye);
			// _convert = buf.toString();
			break;
		}

		return buf;
	}

	// 终止线程
	public void stopMyThread() {
		// if (thread != null) {
		// System.out.println("stop exit== " + thread.exit);
		// thread.exit = true;
		// // thread.interrupt();
		// // thread.stop();
		// }
		stopThread = true;
		finished = true;
		if (twoTimer != null) {
			twoTimer.cancel();
		}
		if (downTimer != null) {
			downTimer.cancel();
		}
	}

	class DownTimer extends CountDownTimer {
		public DownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 倒计时开始
			long m = millisUntilFinished / 1000;
			String ss = String.valueOf(m % 3600 % 60);
			ss = twoLength(ss);
		}

		private String twoLength(String str) {
			if (str.length() == 1) {
				return str = "0" + str;
			}
			return str;
		}

		@Override
		public void onFinish() {
			// asyncDownload();
			if (!checkDone) {
				handler.sendEmptyMessage(ERRORDIALOG);
			}
			downTimer.cancel();
		}
	};

	// Top邮件过多 提醒
	class TwoTimer extends CountDownTimer {
		public TwoTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 倒计时开始
			long m = millisUntilFinished / 1000;
			String ss = String.valueOf(m % 3600 % 60);
			ss = twoLength(ss);
		}

		private String twoLength(String str) {
			if (str.length() == 1) {
				return str = "0" + str;
			}
			return str;
		}

		@Override
		public void onFinish() {
			// asyncDownload();
			if (!stopThread && !finished) {
				// handler.sendEmptyMessage(ERRORDIALOG);
				// ToastManage.showToast("邮件数量较多，正在努力更新中请稍后...");
				// Toast.makeText(AddMaildoingActivity.this,
				// "邮件数量较多，正在努力更新中请稍后...", Toast.LENGTH_LONG).show();
				ToastManage.showToast("邮件数量较多，正在努力更新中请稍后...");
			}
			twoTimer.cancel();
		}
	};

}
