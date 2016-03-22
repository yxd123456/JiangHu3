package com.sptech.qujj.view;

import com.sptech.qujj.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBar extends RelativeLayout implements OnClickListener {

	public static final String TAG = "TitleBarView";
	private Context context;
	private TextView tv_title;
	private Button btn_left, btn_right;
	// private ImageView img_right;
	// private RelativeLayout relativeright;
	private OnClickTitleListener onClickTitleListener;

	public interface OnClickTitleListener {
		public void onLeftButtonClick(View view);

		public void onRightButtonClick(View view);
	}

	public TitleBar(Context context) {
		super(context);
		this.context = context;
		inflateSelf();
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		inflateSelf();
	}

	private void inflateSelf() {
		LayoutInflater.from(context).inflate(R.layout.view_titlebar, this);
	}

	public void inflateSelf(int id) {
		LayoutInflater.from(context).inflate(id, this);
	}

	// 设置背景颜色
	public void setBackground() {
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.title_view);
		relativeLayout.setBackgroundColor(getResources()
				.getColor(R.color.white));
	}

	@Override
	protected void onFinishInflate() {
		btn_left = (Button) findViewById(R.id.btn_left); // 左边的按钮
		tv_title = (TextView) findViewById(R.id.tv_title); // 标题
		btn_right = (Button) findViewById(R.id.btn_right); // 右边的按钮
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (onClickTitleListener == null) {
			Log.e(TAG, "not set OnClickButtonListener instance");
			return;
		}
		switch (v.getId()) {
		case R.id.btn_left:
			onClickTitleListener.onLeftButtonClick(v);
			break;
		case R.id.btn_right:
			onClickTitleListener.onRightButtonClick(v);
			break;
		default:
			break;
		}
	}

	public void bindTitleContent(String titleText, int leftResId,
			int rightResId, String leftText, String rightText) {
		this.tv_title.setText(titleText);
		if (leftResId > 0) {
			btn_left.setVisibility(View.VISIBLE);
			btn_left.setBackgroundResource(leftResId);
			// btn_left.setText(leftText);
		} else if (leftText != "") {
			btn_left.setVisibility(View.VISIBLE);
			btn_left.setBackgroundResource(0);
			btn_left.setText(leftText);
		} else {
			btn_left.setVisibility(View.GONE);
		}

		if (rightResId > 0) {
			btn_right.setVisibility(View.VISIBLE);
			btn_right.setBackgroundResource(rightResId);
			// btn_right.setText(rightText);
		} else if (rightText != "") {
			btn_right.setVisibility(View.VISIBLE);
			btn_right.setBackgroundResource(0);
			btn_right.setText(rightText);
		} else {
			btn_right.setVisibility(View.GONE);
		}
	}

	// 修改标题
	public void ChangeTitle(String titleText) {
		this.tv_title.setText(titleText);
	}

	// 设置左边按钮的可见性
	public void setLeftButtonVisibility(boolean flag) {
		if (flag)
			btn_left.setVisibility(VISIBLE);
		else
			btn_left.setVisibility(INVISIBLE);
	}

	// 设置右边按钮的可见性
	public void setRightButtonVisibility(boolean flag) {
		if (flag)
			btn_right.setVisibility(VISIBLE);
		else
			btn_right.setVisibility(INVISIBLE);
	}

	public OnClickTitleListener getOnClickTitleListener() {
		return onClickTitleListener;
	}

	public void setOnClickTitleListener(
			OnClickTitleListener onClickTitleListener) {
		this.onClickTitleListener = onClickTitleListener;
	}

}
