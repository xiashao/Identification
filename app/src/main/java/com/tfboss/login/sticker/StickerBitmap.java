package com.tfboss.login.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.tfboss.login.util.DrawAttribute;
import com.tfboss.login.util.FingerPassword;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class StickerBitmap
{
	public Bitmap bitmap;
	private boolean isLock;
	private View containter;
	private StickerBitmapList stickerBitmapList;

	private int mode;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private float lastXDrag;
	private float lastYDrag;
	private float onDownZoomDist;
	private float onDownZoomRotation = 0;
	private PointF onDownZoomMidPoint= new PointF();

	private Matrix bitmapMatrix;
	private Matrix onDownMatrix;
	private Matrix onMoveMatrix;

	int translateWidth;
	int translateHeight;

	Boolean isFristDraw;

	public StickerBitmap(View containter, StickerBitmapList stickerBitmapList, Bitmap bitmap)
	{
		this.bitmap = bitmap;
		this.containter = containter;
		this.stickerBitmapList = stickerBitmapList;
		this.isLock = false;

		isFristDraw=true;

		bitmapMatrix = new Matrix();
		int width=bitmap.getWidth();
		int height=bitmap.getHeight();
		translateWidth=(DrawAttribute.screenWidth-width)/2;
		translateHeight=(DrawAttribute.screenHeight-height)/2;
		onDownMatrix = new Matrix();
		onMoveMatrix = new Matrix();
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				if(!isLock)
				{
					stickerBitmapList.setIsStickerToolsDraw(false,null);

					mode = DRAG;

					lastXDrag = event.getX();
					lastYDrag = event.getY();

					onDownMatrix.set(bitmapMatrix);
				}
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if(!isLock)
				{
					mode = ZOOM;

					onDownZoomDist = spacing(event);

					onDownZoomRotation = rotation(event);

					onDownMatrix.set(bitmapMatrix);

					midPoint(onDownZoomMidPoint, event);
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if(!isLock)
				{
					if (mode == ZOOM)
					{
						onMoveMatrix.set(onDownMatrix);

						float rotation = rotation(event) - onDownZoomRotation;

						float scale = spacing(event) / onDownZoomDist;

						int widthMid= DrawAttribute.screenWidth/2,heightMid=DrawAttribute.screenHeight/2;
						onMoveMatrix.setTranslate(translateWidth,translateHeight);
						onMoveMatrix.postScale(scale, scale, widthMid, heightMid);
						onMoveMatrix.postRotate(rotation, widthMid, heightMid);
						FingerPassword.setRotate(rotation);
						FingerPassword.setScale(scale);

						bitmapMatrix.set(onMoveMatrix);

						containter.postInvalidate();
					}
					else if (mode == DRAG)
					{

					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:

				PointF leftTopPoint = new PointF();
				this.getLeftTopPointF(leftTopPoint);

				stickerBitmapList.setIsStickerToolsDraw(true,leftTopPoint);

				mode = NONE;
				break;
		}
		return true;
	}

	/** 判断手指在屏幕上的点是不是在贴图上 */
	public boolean isPointInsideBitmap(float x, float y)
	{
		float[] points = new float[8];
		points[0] = 0;
		points[1] = 0;
		points[2] = 0;
		points[3] = bitmap.getHeight();	/** 该图贴的图片的高度 */
		points[4] = bitmap.getWidth();	/** 该图贴的图片的宽度 */
		points[5] = bitmap.getHeight();	/** 该图贴的图片的高度 */
		points[6] = bitmap.getWidth();	/** 该图贴的图片的宽度 */
		points[7] = 0;

		bitmapMatrix.mapPoints(points);

		float A,B,C;
		for(int i = 0; i < 4; i++)
		{
			A = -(points[(3+2*i)%8] - points[1+2*i]);
			B = points[2*(i+1)%8] - points[2*i];
			C = -(A * points[2*i] + B * points[1+2*i]);
			if (A * x + B * y + C > 0)
			{
				return false;
			}
		}
		return true;
	}

	public void drawBitmap(Canvas canvas)
	{
		if(isFristDraw)
		{
			bitmapMatrix.setTranslate(translateWidth, translateHeight);
			isFristDraw=false;
		}
		canvas.drawBitmap(bitmap, bitmapMatrix, null);
	}

	public void mirrorTheBitmap()
	{
		Matrix matrix = new Matrix();
		matrix.preScale(-1, 1);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
				matrix, true);
		containter.postInvalidate();
	}

	/** 返回图贴是否被锁定 */
	public boolean isLock()
	{
		return isLock;
	}

	public void setLock()
	{
		if(isLock)
		{
			isLock = false;
		}
		else
		{
			isLock = true;
		}
	}

	/** 获取并设置 (经过旋转,平移或其他变换之后 包含该图贴的最小矩阵的左上角的点) */
	public void getLeftTopPointF(PointF leftTopPoint)
	{
		float[] points = new float[8];
		points[0] = 0;
		points[1] = 0;
		points[2] = 0;
		points[3] = bitmap.getHeight();	/** 该图贴的图片的高度 */
		points[4] = bitmap.getWidth();	/** 该图贴的图片的宽度 */
		points[5] = bitmap.getHeight();	/** 该图贴的图片的高度 */
		points[6] = bitmap.getWidth();	/** 该图贴的图片的宽度 */
		points[7] = 0;

		/** 图贴旋转,平移或者作出其他操作之后,将包含该图贴的最小矩阵的四个点的坐标存入float[8]中*/
		bitmapMatrix.mapPoints(points);

		float leftTopX = points[0];
		float leftTopY = points[1];
		for(int i = 2; i < 8; i += 2)
		{
			/** 矩阵的四个点,左上角的点的横坐标最小 */

			if(points[i] < leftTopX)
			{
				leftTopX = points[i];
			}
		}
		for(int i = 1; i < 8; i += 2)
		{
			/** 矩阵的四个点,左上角的点的纵坐标最小 */

			if(points[i] < leftTopY)
			{
				leftTopY = points[i];
			}
		}
		leftTopPoint.set(leftTopX, leftTopY);
	}

	/** 计算两个触碰点之间的距离 */
	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/** 取两个触碰点的中间点 */
	private void midPoint(PointF point, MotionEvent event)
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private float rotation(MotionEvent event)
	{
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));

		double radians = Math.atan2(delta_y, delta_x);

		/** 弧度转换为角度 */
		return (float) Math.toDegrees(radians);
	}
}