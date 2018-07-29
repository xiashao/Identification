package com.example.finaldesign.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.tfboss.login.R;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class ColorPicker extends View
{
	private static final String STATE_PARENT = "parent";
	private static final String STATE_ANGLE = "angle";
	private static final String STATE_OLD_COLOR = "color";

	private static final int[] COLORS = new int[] { 0xFFFF0000, 0xFFFF00FF,
			0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };

	private Paint mColorWheelPaint;

	private Paint mPointerHaloPaint;

	private Paint mPointerColor;

	private int mColorWheelThickness;

	private int mColorWheelRadius;
	private int mPreferredColorWheelRadius;

	private int mColorCenterRadius;
	private int mPreferredColorCenterRadius;

	private int mColorCenterHaloRadius;
	private int mPreferredColorCenterHaloRadius;

	private int mColorPointerRadius;

	private int mColorPointerHaloRadius;

	private RectF mColorWheelRectangle = new RectF();

	private RectF mCenterRectangle = new RectF();

	private boolean mUserIsMovingPointer = false;

	private int mColor;

	private int mCenterOldColor;

	private int mCenterNewColor;

	private float mTranslationOffset;

	private float mAngle;

	private Paint mCenterOldPaint;

	private Paint mCenterNewPaint;

	private Paint mCenterHaloPaint;

	private float[] mHSV = new float[3];

	private SVBar mSVbar = null;

	private OpacityBar mOpacityBar = null;

	private SaturationBar mSaturationBar = null;

	private ValueBar mValueBar = null;

	private OnColorChangedListener onColorChangedListener;

	public ColorPicker(Context context) {
		super(context);
		init(null, 0);
	}

	public ColorPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	public interface OnColorChangedListener {
		void onColorChanged(int color);
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.onColorChangedListener = listener;
	}

	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.ColorPicker, defStyle, 0);
		final Resources b = getContext().getResources();

		mColorWheelThickness = a.getDimensionPixelSize(
				R.styleable.ColorPicker_color_wheel_thickness,
				b.getDimensionPixelSize(R.dimen.color_wheel_thickness));
		mColorWheelRadius = a.getDimensionPixelSize(
				R.styleable.ColorPicker_color_wheel_radius,
				b.getDimensionPixelSize(R.dimen.color_wheel_radius));
		mPreferredColorWheelRadius = mColorWheelRadius;
		mColorCenterRadius = a.getDimensionPixelSize(
				R.styleable.ColorPicker_color_center_radius,
				b.getDimensionPixelSize(R.dimen.color_center_radius));
		mPreferredColorCenterRadius = mColorCenterRadius;
		mColorCenterHaloRadius = a.getDimensionPixelSize(
				R.styleable.ColorPicker_color_center_halo_radius,
				b.getDimensionPixelSize(R.dimen.color_center_halo_radius));
		mPreferredColorCenterHaloRadius = mColorCenterHaloRadius;
		mColorPointerRadius = a.getDimensionPixelSize(
				R.styleable.ColorPicker_color_pointer_radius,
				b.getDimensionPixelSize(R.dimen.color_pointer_radius));
		mColorPointerHaloRadius = a.getDimensionPixelSize(
				R.styleable.ColorPicker_color_pointer_halo_radius,
				b.getDimensionPixelSize(R.dimen.color_pointer_halo_radius));

		a.recycle();

		mAngle = (float) (-Math.PI / 2);

		Shader s = new SweepGradient(0, 0, COLORS, null);

		mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mColorWheelPaint.setShader(s);
		mColorWheelPaint.setStyle(Paint.Style.STROKE);
		mColorWheelPaint.setStrokeWidth(mColorWheelThickness);

		mPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerHaloPaint.setColor(Color.BLACK);
		mPointerHaloPaint.setAlpha(0x50);

		mPointerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerColor.setColor(calculateColor(mAngle));

		mCenterNewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterNewPaint.setColor(calculateColor(mAngle));
		mCenterNewPaint.setStyle(Paint.Style.FILL);

		mCenterOldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterOldPaint.setColor(calculateColor(mAngle));
		mCenterOldPaint.setStyle(Paint.Style.FILL);

		mCenterHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterHaloPaint.setColor(Color.BLACK);
		mCenterHaloPaint.setAlpha(0x00);

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.translate(mTranslationOffset, mTranslationOffset);

		canvas.drawOval(mColorWheelRectangle, mColorWheelPaint);

		float[] pointerPosition = calculatePointerPosition(mAngle);

		canvas.drawCircle(pointerPosition[0], pointerPosition[1],
				mColorPointerHaloRadius, mPointerHaloPaint);

		canvas.drawCircle(pointerPosition[0], pointerPosition[1],
				mColorPointerRadius, mPointerColor);

		canvas.drawCircle(0, 0, mColorCenterHaloRadius, mCenterHaloPaint);

		canvas.drawArc(mCenterRectangle, 90, 180, true, mCenterOldPaint);

		canvas.drawArc(mCenterRectangle, 270, 180, true, mCenterNewPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int intrinsicSize = 2 * (mPreferredColorWheelRadius + mColorPointerHaloRadius);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(intrinsicSize, widthSize);
		} else {
			width = intrinsicSize;
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(intrinsicSize, heightSize);
		} else {
			height = intrinsicSize;
		}

		int min = Math.min(width, height);
		setMeasuredDimension(min, min);
		mTranslationOffset = min * 0.5f;

		mColorWheelRadius = min / 2 - mColorWheelThickness
				- (mColorPointerHaloRadius / 2);
		mColorWheelRectangle.set(-mColorWheelRadius, -mColorWheelRadius,
				mColorWheelRadius, mColorWheelRadius);

		mColorCenterRadius = (int) ((float) mPreferredColorCenterRadius * ((float) mColorWheelRadius / (float) mPreferredColorWheelRadius));
		mColorCenterHaloRadius = (int) ((float) mPreferredColorCenterHaloRadius * ((float) mColorWheelRadius / (float) mPreferredColorWheelRadius));
		mCenterRectangle.set(-mColorCenterRadius, -mColorCenterRadius,
				mColorCenterRadius, mColorCenterRadius);
	}

	private int ave(int s, int d, float p) {
		return s + java.lang.Math.round(p * (d - s));
	}

	private int calculateColor(float angle) {
		float unit = (float) (angle / (2 * Math.PI));
		if (unit < 0) {
			unit += 1;
		}

		if (unit <= 0) {
			mColor = COLORS[0];
			return COLORS[0];
		}
		if (unit >= 1) {
			mColor = COLORS[COLORS.length - 1];
			return COLORS[COLORS.length - 1];
		}

		float p = unit * (COLORS.length - 1);
		int i = (int) p;
		p -= i;

		int c0 = COLORS[i];
		int c1 = COLORS[i + 1];
		int a = ave(Color.alpha(c0), Color.alpha(c1), p);
		int r = ave(Color.red(c0), Color.red(c1), p);
		int g = ave(Color.green(c0), Color.green(c1), p);
		int b = ave(Color.blue(c0), Color.blue(c1), p);

		mColor = Color.argb(a, r, g, b);
		return Color.argb(a, r, g, b);
	}

	public int getColor() {
		return mCenterNewColor;
	}

	public void setColor(int color) {
		mAngle = colorToAngle(color);
		mPointerColor.setColor(calculateColor(mAngle));

		if (mOpacityBar != null) {
			// set the value of the opacity
			mOpacityBar.setColor(mColor);
			mOpacityBar.setOpacity(Color.alpha(color));
		}

		if (mSVbar != null)
		{
			Color.colorToHSV(color, mHSV);
			mSVbar.setColor(mColor);

			if (mHSV[1] < mHSV[2]) {
				mSVbar.setSaturation(mHSV[1]);
			} else { // if (mHSV[1] > mHSV[2]) {
				mSVbar.setValue(mHSV[2]);
			}
		}

		if (mSaturationBar != null) {
			Color.colorToHSV(color, mHSV);
			mSaturationBar.setColor(mColor);
			mSaturationBar.setSaturation(mHSV[1]);
		}

		if (mValueBar != null && mSaturationBar == null) {
			Color.colorToHSV(color, mHSV);
			mValueBar.setColor(mColor);
			mValueBar.setValue(mHSV[2]);
		} else if (mValueBar != null) {
			Color.colorToHSV(color, mHSV);
			mValueBar.setValue(mHSV[2]);
		}

		invalidate();
	}

	private float colorToAngle(int color) {
		float[] colors = new float[3];
		Color.colorToHSV(color, colors);

		return (float) Math.toRadians(-colors[0]);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);

		float x = event.getX() - mTranslationOffset;
		float y = event.getY() - mTranslationOffset;

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			float[] pointerPosition = calculatePointerPosition(mAngle);
			if (x >= (pointerPosition[0] - mColorPointerHaloRadius)
					&& x <= (pointerPosition[0] + mColorPointerHaloRadius)
					&& y >= (pointerPosition[1] - mColorPointerHaloRadius)
					&& y <= (pointerPosition[1] + mColorPointerHaloRadius)) {
				mUserIsMovingPointer = true;
				invalidate();
			}
			// Check wheter the user pressed on the center.
			if (x >= -mColorCenterRadius && x <= mColorCenterRadius
					&& y >= -mColorCenterRadius && y <= mColorCenterRadius) {
				mCenterHaloPaint.setAlpha(0x50);
				setColor(getOldCenterColor());
				mCenterNewPaint.setColor(getOldCenterColor());
				invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mUserIsMovingPointer) {
				mAngle = (float) java.lang.Math.atan2(y, x);
				mPointerColor.setColor(calculateColor(mAngle));

				setNewCenterColor(mCenterNewColor = calculateColor(mAngle));

				if (mOpacityBar != null) {
					mOpacityBar.setColor(mColor);
				}

				if (mValueBar != null) {
					mValueBar.setColor(mColor);
				}

				if (mSaturationBar != null) {
					mSaturationBar.setColor(mColor);
				}

				if (mSVbar != null) {
					mSVbar.setColor(mColor);
				}

				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			mUserIsMovingPointer = false;
			mCenterHaloPaint.setAlpha(0x00);
			invalidate();
			break;
		}
		return true;
	}

	private float[] calculatePointerPosition(float angle) {
		float x = (float) (mColorWheelRadius * Math.cos(angle));
		float y = (float) (mColorWheelRadius * Math.sin(angle));

		return new float[] { x, y };
	}

	public void addSVBar(SVBar bar) {
		mSVbar = bar;
		mSVbar.setColorPicker(this);
		mSVbar.setColor(mColor);
	}

	public void addOpacityBar(OpacityBar bar) {
		mOpacityBar = bar;
		mOpacityBar.setColorPicker(this);
		mOpacityBar.setColor(mColor);
	}

	public void setNewCenterColor(int color) {
		mCenterNewColor = color;
		mCenterNewPaint.setColor(color);
		if (mCenterOldColor == 0) {
			mCenterOldColor = color;
			mCenterOldPaint.setColor(color);
		}
		if (onColorChangedListener != null) {
			onColorChangedListener.onColorChanged(color);
		}
		invalidate();
	}

	public void setOldCenterColor(int color) {
		mCenterOldColor = color;
		mCenterOldPaint.setColor(color);
		invalidate();
	}

	public int getOldCenterColor() {
		return mCenterOldColor;
	}

	public void changeOpacityBarColor(int color) {
		if (mOpacityBar != null) {
			mOpacityBar.setColor(color);
		}
	}

	public void changeValueBarColor(int color) {
		if (mValueBar != null) {
			mValueBar.setColor(color);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable(STATE_PARENT, superState);
		state.putFloat(STATE_ANGLE, mAngle);
		state.putInt(STATE_OLD_COLOR, mCenterOldColor);

		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle savedState = (Bundle) state;

		Parcelable superState = savedState.getParcelable(STATE_PARENT);
		super.onRestoreInstanceState(superState);

		mAngle = savedState.getFloat(STATE_ANGLE);
		setOldCenterColor(savedState.getInt(STATE_OLD_COLOR));
		mPointerColor.setColor(calculateColor(mAngle));
	}
}