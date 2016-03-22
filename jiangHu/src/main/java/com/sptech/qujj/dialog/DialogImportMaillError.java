package com.sptech.qujj.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.view.ImportmailErrorListener;

public class DialogImportMaillError {

	private Activity context;
	private Dialog dialog;

	private Button dialog_im_right;
	private ImageView im_close;
	ImportmailErrorListener importmailErrorListener;

	private TextView tv_mailno;
	public EditText et_popserver, et_port;
	// 邮箱名称，端口，发件服务器；
	private String emailname, port, host;

	private CheckBox img_agree;
	private int issafe; // 弹出框里 issafe

	public DialogImportMaillError(Activity context, String emailname, int issafe, String port, String host, ImportmailErrorListener importmailErrorListener) {
		this.context = context;
		this.emailname = emailname;
		this.port = port;
		this.host = host;
		this.issafe = issafe;
		this.importmailErrorListener = importmailErrorListener;
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_importmailerror, null);
		tv_mailno = (TextView) view.findViewById(R.id.tv_mailno);
		tv_mailno.setText("邮箱账号：" + emailname);
		img_agree = (CheckBox) view.findViewById(R.id.img_agree);

		et_popserver = (EditText) view.findViewById(R.id.et_popserver);
		et_port = (EditText) view.findViewById(R.id.et_port);

		if (port == null || port.equals("0")) {
			et_port.setText("110");
		} else {
			et_port.setText(port);
		}
		if (host == null || host.equals("")) {
			et_popserver.setText("");
		} else {
			et_popserver.setText(host);
		}

		if (issafe == 1) {
			img_agree.setChecked(true);
		} else {
			img_agree.setChecked(false);
		}

		img_agree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (img_agree.isChecked()) {
					et_port.setText("995");
					et_port.setSelection("995".length());
				} else {
					et_port.setText("110");
					et_port.setSelection("110".length());
				}

			}
		});
		dialog_im_right = (Button) view.findViewById(R.id.dialog_im_right);
		dialog_im_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String pop_hostString = et_popserver.getText().toString().trim();
				String pop_portString = et_port.getText().toString().trim();
				int port = 0;
				if (pop_hostString != null && !"".equals(pop_portString)) {
					port = Integer.valueOf(pop_portString);
				}
				if (pop_portString.equals("")) {
					ToastManage.showToast("请输入服务器端口!");
					return;
				} else if (port < 1 || port > 65535) {
					ToastManage.showToast("输入的端口有误,端口范围为1-65535。");
					return;
				}
				closeDialog();
				boolean check = false;
				if (img_agree.isChecked()) {
					check = true;
				}
				importmailErrorListener.ImportmailErrorListener(pop_hostString, pop_portString, check);

			}
		});
		im_close = (ImageView) view.findViewById(R.id.im_close);
		im_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				context.finish();
			}
		});
		dialog = new Dialog(context, R.style.dialog);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					closeDialog();
					context.finish();
					return true;
				}
				return false;
			}
		});
		dialog.show();
	}

	public void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
