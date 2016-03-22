package com.sptech.qujj.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sptech.qujj.R;

/**
 * 
 * 彩色圆形进度条
 */
public class CircleProgressBar extends View {

	/** 画Progress各个组件所需要的画笔 **/
	private Paint mPaint, mPaint1;
	/** 进度圆环的宽度 **/
	private float mRingWidth;

	public float getmRingWidth() {
		return mRingWidth;
	}

	public void setmRingWidth(float mRingWidth) {
		this.mRingWidth = mRingWidth;
	}

	/** 正常圆环的宽度 **/
	private float mNormalRingWidth;

	public float getmNormalRingWidth() {
		return mNormalRingWidth;
	}

	public void setmNormalRingWidth(float mNormalRingWidth) {
		this.mNormalRingWidth = mNormalRingWidth;
	}

	/** 进度圆环的一般状态颜色 **/
	private int mRingNormalColor;
	/** 进度圆环的进度颜色 **/
	private int mRingProgressColor;
	/** 进度文字的大小 **/
	private float mProgressTextSize;
	/** 进度文字的颜色 **/
	private int mProgressTextColor;
	/** 进度文字的颜色 **/
	private int mProgressGrayTextColor = Color.parseColor("#737373");

	public int getmProgressTextColor() {
		return mProgressTextColor;
	}

	public void setmProgressTextColor(int mProgressTextColor) {
		this.mProgressTextColor = mProgressTextColor;
	}

	/** 进度文字的显示与否 **/
	private boolean mProgressTextVisibility;
	/** 组件显示成填充类型 **/
	private static final int STYLE_FILL = 1;
	/** 组件显示成画笔类型 **/
	private static final int STYLE_STAROKE = 0;
	/** 组件所需显示的类型：fill或者stroke **/
	private int mProgressBarStyle;
	/** 默认的进度字体大小 **/
	private static final int DEFAULT_PROGRESS_TEXT_SIZE = 16;
	/** 默认的进度圈宽度 **/
	private static final int DEFAULT_RING_CIRCLE_WIDTH = 3;
	/** 默认的正常圈宽度 **/
	private static final int DEFAULT_NORMAL_RING_CIRCLE_WIDTH = 3;
	/** 进度 **/
	private int mProgress;
	/** 设置最大进度 **/
	private int mMaxProgress;
	/** 默认最大进度 **/
	private static final int DEFAULT_MAX_PROGRESS = 100;
	/** 圆的度数 360度 **/
	private static final int CIRCLE_ANGLE = 360;

	/**
	 * CircleProgressBar
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public CircleProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * CircleProgressBar
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 * @param defStyle
	 *            MulticolorCircleProgressBar defStyle
	 */
	public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		float density = getResources().getDisplayMetrics().density;
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
		mProgressBarStyle = typeArray.getInt(R.styleable.CircleProgressBar_circleStyle, STYLE_STAROKE);
		mProgressTextColor = typeArray.getColor(R.styleable.CircleProgressBar_progressTextColor, Color.BLACK);
		mProgressTextSize = typeArray.getDimension(R.styleable.CircleProgressBar_progressTextSize, (int) (DEFAULT_PROGRESS_TEXT_SIZE * density + 0.5f));
		mProgressTextVisibility = typeArray.getBoolean(R.styleable.CircleProgressBar_progressTextVisibility, true);
		mRingWidth = typeArray.getDimension(R.styleable.CircleProgressBar_ringWidth, (int) (DEFAULT_RING_CIRCLE_WIDTH * density));
		mNormalRingWidth = typeArray.getDimension(R.styleable.CircleProgressBar_ringNormalWidth, (int) (DEFAULT_NORMAL_RING_CIRCLE_WIDTH * density));
		mRingNormalColor = typeArray.getColor(R.styleable.CircleProgressBar_ringNormalColor, Color.GRAY);
		mRingProgressColor = typeArray.getColor(R.styleable.CircleProgressBar_ringProgressColor, Color.YELLOW);
		mMaxProgress = typeArray.getInt(R.styleable.CircleProgressBar_maxProgress, DEFAULT_MAX_PROGRESS);
		mRingWidth = mProgressBarStyle == STYLE_FILL ? 0 : mRingWidth;
		mNormalRingWidth = mProgressBarStyle == STYLE_FILL ? 0 : mNormalRingWidth;
		// typeArray 缓存
		typeArray.recycle();
		mPaint = new Paint();
		mPaint1 = new Paint();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 首先画外围进度圈
		System.out.println("mProgress ==" + mProgress);
		int center = getWidth() / 2;
		int radius = (int) (center - mRingWidth / 2);
		mPaint.setColor(mRingNormalColor);
		mPaint.setStyle(mProgressBarStyle == STYLE_FILL ? Paint.Style.FILL : Paint.Style.STROKE);
		// 中间的进度百分比，先转换成float在进行除法运算，不然都为0
		int percent = (int) ((mProgress * 1.0 / mMaxProgress) * 100);
		System.out.println("mMaxProgress ==" + mMaxProgress);
		System.out.println("percent ==" + percent);
		mPaint.setStrokeWidth(mNormalRingWidth);
		mPaint.setAntiAlias(true);
		canvas.drawCircle(center, center, radius, mPaint);

		// 画文字进度，设置到进度圈中间
		if (mProgressTextVisibility && mProgressBarStyle != STYLE_FILL) {
			mPaint.setStrokeWidth(0);
			mPaint.setColor(mProgressTextColor);
			mPaint.setTextSize(mProgressTextSize);
			// // 设置字体
			// Typeface typeFace = Typeface.createFromAsset(getContext()
			// .getAssets(), "fonts/impact.ttf");
			// // 设置字体
			// mPaint.setTypeface(typeFace);
			// 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
			float textWidth = mPaint.measureText(percent + "%");
			float qiang = mPaint.measureText("抢");
			// 画笔重置
			mPaint1 = new Paint();
			if (percent == 100) {
				// 文字部分
				/*mPaint.setColor(mProgressGrayTextColor);
				textWidth = mPaint.measureText("售罄");
				canvas.drawText("售罄", center - textWidth / 2, center + mProgressTextSize / 2, mPaint);*/
				this.setBackgroundResource(R.drawable.img_licai_sq);
				// 售罄的圆环设置为灰色的
				 mPaint1.setColor(Color.parseColor("#ebebeb"));
				//mPaint1.setColor(Color.parseColor("#21a2fd"));
				// mPaint1.setColor(mProgressGrayTextColor);

			} else {
				// 文字部分
				canvas.drawText(percent + "%", center - textWidth / 2, center - mProgressTextSize / 3, mPaint);
				mPaint.setColor(Color.parseColor("#21a2fd"));
				canvas.drawText("抢", center - qiang / 2, center + mProgressTextSize, mPaint);

				// 百分比小于100 则 设置圆形渐变颜色
				// Color.parseColor("#e17aff"),
				// Color.parseColor("#d87cff"),
				// Color.parseColor("#c780ff"),
				// Color.parseColor("#9886ff"),
				// Color.parseColor("#9c88ff"),
				// Color.parseColor("#818efe"),
				// Shader mShader = new SweepGradient(getWidth() / 2,
				//
				// getHeight() / 2, new int[] {
				//
				// Color.parseColor("#6394fd"), Color.parseColor("#5796fd"),
				// Color.parseColor("#4a99fd"),
				// Color.parseColor("#2d9ffd"),
				// Color.parseColor("#21a2fd") }, new float[] { 0.2f,
				// 0.4f, 0.6f, 0.8f, 1.0f });

				// new float[] { 0, 0.1f,0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f,
				// 0.8f, 0.9f, 1.0f });
				// mPaint1.setShader(mShader);
				mPaint1.setColor(Color.parseColor("#21a2fd"));
			}
		}

		// 画圆环进度
		mPaint1.setAntiAlias(true);
		mPaint1.setStrokeWidth(mRingWidth);
		RectF rectF = new RectF(center - radius, center - radius, center + radius, center + radius);

		switch (mProgressBarStyle) {
		case STYLE_FILL:
			mPaint1.setStyle(Paint.Style.FILL);
			canvas.drawArc(rectF, -90, CIRCLE_ANGLE * mProgress / mMaxProgress, false, mPaint1);
			// canvas.drawArc(rectF, -90, CIRCLE_ANGLE * mProgress /
			// mMaxProgress,
			// true, mPaint1);
			break;
		case STYLE_STAROKE:
		default:
			mPaint1.setStyle(Paint.Style.STROKE);
			canvas.drawArc(rectF, -90, CIRCLE_ANGLE * mProgress / mMaxProgress, false, mPaint1);
			// canvas.drawArc(rectF, -90, CIRCLE_ANGLE * mProgress /
			// mMaxProgress,
			// true, mPaint1);
			break;

		}

	}

	/**
	 * 设置进度
	 * 
	 * @param progress
	 *            progress
	 */
	public synchronized void setProgress(int progress) {
		if (progress <= 0) {
			mProgress = 0;

		} else if (progress > mMaxProgress) {
			mProgress = mMaxProgress;
		} else {
			mProgress = progress;
		}

		System.out.println("mProgress ==" + progress);
		postInvalidate();
	}

	/**
	 * 获取进度
	 * 
	 * @return 进度
	 */
	public synchronized int getProgress() {
		return mProgress;
	}

	/**
	 * 获取最大进度
	 * 
	 * @return maxProgress
	 */
	public synchronized int getMaxProgress() {
		return mMaxProgress;
	}
}
