package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVIncomeAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.Income;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 累计收益-（列表）
 * 
 * @author yebr
 * 
 */
public class IncomeActivity extends BaseActivity implements OnClickTitleListener {

	private TitleBar titleBar;
	PullToRefreshListView lvYesmbRecord;// 理财收益列表
	private TextView tv_total;
	private List<Income> incomes = new ArrayList<Income>();

	boolean canload = true; // 是否还有加载项

	private LVIncomeAdapter incomeAdapter;
	private int start = 0; // 分页起始位置
	private int limit = 10; // 每页显示条数
	/**
	 * 当前的页码
	 */
	int currentPage = 1;
	private final int ACTION_LIST = 1;
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	private float total;

	// private ProgressBar progress_loadmore;
	private int visibleLastIndex = 0; // 最后的可视项索引
	public String month = "";

	private RelativeLayout rela_nodata;// 没有数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_income);
		initView();
		Tools.addActivityList(this);
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		if (getIntent() != null) {
			total = getIntent().getExtras().getFloat("total");
		}
		dialogHelper = new DialogHelper(this);

		rela_nodata = (RelativeLayout) findViewById(R.id.rela_nodata);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("累计收益", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);

		tv_total = (TextView) findViewById(R.id.tv_total);
		tv_total.setText("¥" + DataFormatUtil.floatsaveTwo(total));
		lvYesmbRecord = (PullToRefreshListView) findViewById(R.id.lv_yes_rmb_record);
		incomeAdapter = new LVIncomeAdapter(this, incomes);
		lvYesmbRecord.setAdapter(incomeAdapter);

		// View mFooterView = LayoutInflater.from(IncomeActivity.this).inflate(
		// R.layout.view_footer_lv, null);
		// progress_loadmore = (ProgressBar) mFooterView
		// .findViewById(R.id.load_more_progressBar);
		// lvYesmbRecord.addFooterView(mFooterView);

		lvYesmbRecord.setMode(Mode.BOTH);
		lvYesmbRecord.setOnRefreshListener(new OnRefreshListener2() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// currentPage = 1;
				start = 0;
				getProductprofit();
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// currentPage++;
				// lvYesmbRecord.onRefreshComplete();
				getProductprofit();

			}

		});
		getProductprofit();
	}

	private void initListDate() {
		if (incomes.size() == 0) {
			rela_nodata.setVisibility(View.VISIBLE);
			lvYesmbRecord.setVisibility(View.GONE);
		} else {
			rela_nodata.setVisibility(View.GONE);
			lvYesmbRecord.setVisibility(View.VISIBLE);
		}
		System.out.println("incomes--- size===  " + incomes.size());
		incomeAdapter.reset(incomes);
		incomeAdapter.notifyDataSetChanged();

	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	// 理财收益列表
	private void getProductprofit() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("start", start);
		data.put("limit", limit);

		System.out.println("data == " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.PRODUCT_PROFIT, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("理财收益 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				lvYesmbRecord.onRefreshComplete();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (start == 0 && incomes != null) {
						incomes.clear();
					}
					if (b != null && !b.equals("")) {
						System.out.println("理财收益  数据=" + new String(b));
						List<Income> incomeList = new ArrayList<Income>();
						incomeList = JSON.parseArray(new String(b), Income.class);
						// incomes.clear();
						// incomes = incomeList;
						// incomes.addAll(incomeList);
						System.out.println("理财收益  数据 size =" + incomes.size());
						if (incomeList.size() == 0) {
							if (start != 0)
								ToastManage.showToast("没有更多数据了");
						} else {
							start += limit;
						}
						incomes.addAll(incomeList);
						initListDate();
					} else {
						initListDate();
					}
					// progress_loadmore.setVisibility(View.GONE);
				} else {
					ToastManage.showToast(response.desc);
					// progress_loadmore.setVisibility(View.GONE);
				}
			}
		};
	}

	/**
	 * 调用web服务失败返回数据
	 * 
	 * @return
	 */
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ACTION_LIST:
				getProductprofit();
				break;
			default:
				break;
			}
		}
	};

}
