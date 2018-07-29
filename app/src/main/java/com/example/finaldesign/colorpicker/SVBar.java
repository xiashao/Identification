package com.example.finaldesign.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.tfboss.login.R;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class SVBar extends View
{
	private static final String STATE_PARENT = "parent";
	private static final String STATE_COLOR = "color";
	private static final String STATE_SATURATION = "saturation";
	private static final String STATE_VALUE = "value";

	private int mBarThickness;

	private int mBarLength;
	private int mPreferredBarLength;

	private int mBarPointerRadius;

	private int mBarPointerHaloRadius;

	private int mBarPointerPosition;

	private Paint mBarPaint;

	private Paint mBarPointerPaint;

	private Paint mBarPointerHaloPaint;

	private RectF mBarRect = new RectF();

	private Shader shader;

	private boolean mIsMovingPointer;

	private int mColor;

	private float[] mHSVColor = new float[3];

	private float mPosToSVFactor;

	private float mSVToPosFactor;

	private ColorPicker mPicker = null;

	public SVBar(Context context) {
		super(context);
		init(null, 0);
	}

	public SVBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public SVBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.ColorBars, defStyle, 0);
		final Resources b = getContext().getResources();

		mBarThickness = a.getDimensionPixelSize(
				R.styleable.ColorBars_bar_thickness,
				b.getDimensionPixelSize(R.dimen.bar_thickness));
		mBarLength = a.getDimensionPixelSize(R.styleable.ColorBars_bar_length,
				b.getDimensionPixelSize(R.dimen.bar_length));
		mPreferredBarLength = mBarLength;
		mBarPointerRadius = a.getDimensionPixelSize(
				R.styleable.ColorBars_bar_pointer_radius,
				b.getDimensionPixelSize(R.dimen.bar_pointer_radius));
		mBarPointerHaloRadius = a.getDimensionPixelSize(
				R.styleable.ColorBars_bar_pointer_halo_radius,
				b.getDimensionPixelSize(R.dimen.bar_pointer_halo_radius));

		a.recycle();

		mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPaint.setShader(shader);

		mBarPointerPosition = (mBarLength / 2) + mBarPointerHaloRadius;

		mBarPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPointerHaloPaint.setColor(Color.BLACK);
		mBarPointerHaloPaint.setAlpha(0x50);

		mBarPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPointerPaint.setColor(0xff81ff00);

		mPosToSVFactor = 1 / ((float) mBarLength / 2);
		mSVToPosFactor = ((float) mBarLength / 2) / 1;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int intrinsicSize = mPreferredBarLength
				+ (mBarPointerHaloRadius * 2);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int width;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(intrinsicSize, widthSize);
		} else {
			width = intrinsicSize;
		}

		mBarLength = width - (mBarPointerHaloRadius * 2);
		setMeasuredDimension((mBarLength + (mBarPointerHaloRadius * 2)),
				(mBarPointerHaloRadius * 2));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBarLength = w - (mBarPointerHaloRadius * 2);

		mBarRect.set(mBarPointerHaloRadius,
				(mBarPointerHaloRadius - (mBarThickness / 2)),
				(mBarLength + (mBarPointerHaloRadius)),
				(mBarPointerHaloRadius + (mBarThickness / 2)));

		if(!isInEditMode()){
			shader = new LinearGradient(mBarPointerHaloRadius, 0,
					(mBarLength + mBarPointerHaloRadius), mBarThickness, new int[] {
							0xffffffff, Color.HSVToColor(mHSVColor), 0xff000000 },
					null, Shader.TileMode.CLAMP);
		} else {
			shader = new LinearGradient(mBarPointerHaloRadius, 0,
					(mBarLength + mBarPointerHaloRadius), mBarThickness, new int[] {
							0xffffffff, 0xff81ff00, 0xff000000 }, null,
					Shader.TileMode.CLAMP);
			Color.colorToHSV(0xff81ff00, mHSVColor);
		}
		
		mBarPaint.setShader(shader);
		mPosToSVFactor = 1 / ((float) mBarLength / 2);
		mSVToPosFactor = ((float) mBarLength / 2) / 1;
		float[] hsvColor = new float[3];
		Color.colorToHSV(mColor, hsvColor);
		if (hsvColor[1] < hsvColor[2]) {
			mBarPointerPosition = Math.round((mSVToPosFactor * hsvColor[1])
					+ mBarPointerHaloRadius);
		} else {
			mBarPointerPosition = Math
					.round((mSVToPosFactor * (1 - hsvColor[2]))
							+ mBarPointerHaloRadius + (mBarLength / 2));
		}
		if(isInEditMode()){
			mBarPointerPosition = (mBarLength / 2) + mBarPointerHaloRadius;
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawRect(mBarRect, mBarPaint);

		canvas.drawCircle(mBarPointerPosition, mBarPointerHaloRadius,
				mBarPointerHaloRadius, mBarPointerHaloPaint);

		canvas.drawCircle(mBarPointerPosition, mBarPointerHaloRadius,
				mBarPointerRadius, mBarPointerPaint);
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);

		float x = event.getX();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    	mIsMovingPointer = true;
			if (x >= (mBarPointerHaloRadius)
					&& x <= (mBarPointerHaloRadius + mBarLength)) {
				mBarPointerPosition = Math.round(x);
				calculateColor(Math.round(x));
				mBarPointerPaint.setColor(mColor);
				invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsMovingPointer)
			{
				if (x >= mBarPointerHaloRadius
						&& x <= (mBarPointerHaloRadius + mBarLength)) {
					mBarPointerPosition = Math.round(x);
					calculateColor(Math.round(x));
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
						mPicker.changeOpacityBarColor(mColor);
					}
					invalidate();
				} else if (x < mBarPointerHaloRadius) {
					mBarPointerPosition = mBarPointerHaloRadius;
					mColor = Color.WHITE;
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
						mPicker.changeOpacityBarColor(mColor);
					}
					invalidate();
				} else if (x > (mBarPointerHaloRadius + mBarLength)) {
					mBarPointerPosition = mBarPointerHaloRadius + mBarLength;
					mColor = Color.BLACK;
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
						mPicker.changeOpacityBarColor(mColor);
					}
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mIsMovingPointer = false;
			break;
		}
		return true;
	}

	public void setSaturation(float saturation) {
		mBarPointerPosition = Math.round((mSVToPosFactor * saturation)
				+ mBarPointerHaloRadius);
		calculateColor(mBarPointerPosition);
		mBarPointerPaint.setColor(mColor);

		if (mPicker != null) {
			mPicker.setNewCenterColor(mColor);
			mPicker.changeOpacityBarColor(mColor);
		}
		invalidate();
	}

	public void setValue(float value) {
		mBarPointerPosition = Math.round((mSVToPosFactor * (1 - value))
				+ mBarPointerHaloRadius + (mBarLength / 2));
		calculateColor(mBarPointerPosition);
		mBarPointerPaint.setColor(mColor);

		if (mPicker != null) {
			mPicker.setNewCenterColor(mColor);
			mPicker.changeOpacityBarColor(mColor);
		}
		invalidate();
	}

	public void setColor(int color) {
		Color.colorToHSV(color, mHSVColor);
		shader = new LinearGradient(mBarPointerHaloRadius, 0,
				(mBarLength + mBarPointerHaloRadius), mBarThickness, new int[] {
						0xffffffff, color, 0xff000000 }, null,
				Shader.TileMode.CLAMP);
		mBarPaint.setShader(shader);
		calculateColor(mBarPointerPosition);
		mBarPointerPaint.setColor(mColor);
		if (mPicker != null) {
			mPicker.setNewCenterColor(mColor);
			mPicker.changeOpacityBarColor(mColor);
		}
		invalidate();
	}

	private void calculateColor(int x) {
		if (x > (mBarPointerHaloRadius + (mBarLength / 2))
				&& x < (mBarPointerHaloRadius + mBarLength)) {
			mColor = Color
					.HSVToColor(new float[]{
							mHSVColor[0],
							1f,
							(float) (1 - (mPosToSVFactor * (x - (mBarPointerHaloRadius + (mBarLength / 2)))))});
		} else if (x > mBarPointerHaloRadius
				&& x < (mBarPointerHaloRadius + mBarLength)) {
			mColor = Color.HSVToColor(new float[]{mHSVColor[0],
					(float) ((mPosToSVFactor * (x - mBarPointerHaloRadius))),
					1f});
		} else if (x == mBarPointerHaloRadius) {
			mColor = Color.WHITE;
		} else if (x == mBarPointerHaloRadius + mBarLength) {
			mColor = Color.BLACK;
		}
	}

	public int getColor() {
		return mColor;
	}


	public void setColorPicker(ColorPicker picker) {
		mPicker = picker;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable(STATE_PARENT, superState);
		state.putFloatArray(STATE_COLOR, mHSVColor);
		float[] hsvColor = new float[3];
		Color.colorToHSV(mColor, hsvColor);
		if (hsvColor[1] < hsvColor[2]) {
			state.putFloat(STATE_SATURATION, hsvColor[1]);
		} else {
			state.putFloat(STATE_VALUE, hsvColor[2]);
		}

		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle savedState = (Bundle) state;

		Parcelable superState = savedState.getParcelable(STATE_PARENT);
		super.onRestoreInstanceState(superState);

		setColor(Color.HSVToColor(savedState.getFloatArray(STATE_COLOR)));
		if (savedState.containsKey(STATE_SATURATION)) {
			setSaturation(savedState.getFloat(STATE_SATURATION));
		} else {
			setValue(savedState.getFloat(STATE_VALUE));
		}
	}
}