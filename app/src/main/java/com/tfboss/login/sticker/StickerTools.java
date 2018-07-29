package com.tfboss.login.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import com.tfboss.login.util.*;
import com.tfboss.login.R;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

class StickerTools
{
	private Bitmap mirrorBitmap;	/**(点击执行改变图贴的方向)的图片 */
	private Bitmap trashBitmap;		/**(点击执行清除图贴)的图片 */
	private StickerBitmapList stickerBitmapList;	/**图贴列表 以及 对图贴列表的操作 的类 */
	private float startLeftTopX=0;	/**对图贴操作的工具栏上的一个小工具的左上角的横坐标 */
	private float startLeftTopY=0;	/**对图贴操作的工具栏上的一个小工具的左上角的纵坐标 */
	private int bitmapWidth;	/**对图贴操作的工具栏上的一个小工具的宽度 */
	private int bitmapHeight;	/**对图贴操作的工具栏上的一个小工具的高度 */
	private final int toolsNum = 5;	/**设置小工具栏目的小工具一共有5个 */

	/**构造方法： */
	public StickerTools(View container, StickerBitmapList stickerBitmapList)
	{
		mirrorBitmap = ((BitmapDrawable)container.getResources().getDrawable(R.drawable.toolsmirror)).getBitmap();
		/**(点击执行改变图贴的方向)的图片 */

		trashBitmap = ((BitmapDrawable)container.getResources().getDrawable(R.drawable.toolstrash)).getBitmap();
		/**(点击执行清除图贴)的图片 */

		this.stickerBitmapList = stickerBitmapList;	/**图贴列表 以及 对图贴列表的操作 的类 */

		bitmapWidth = mirrorBitmap.getWidth();	/**对图贴操作的工具栏上的一个小工具的宽度 */
		bitmapHeight = mirrorBitmap.getHeight();	/**对图贴操作的工具栏上的一个小工具的高度 */
	}

	public void drawTools(Canvas canvas, boolean isLock)
	{
		canvas.drawBitmap(mirrorBitmap, startLeftTopX, startLeftTopY, null);
		canvas.drawBitmap(trashBitmap,startLeftTopX+bitmapWidth * 1, startLeftTopY, null);
	}

	public boolean setOnTouchEvent(float touchPointX, float touchPointY)
	{
		if(touchPointY >= startLeftTopY && touchPointY < startLeftTopY + bitmapHeight)
		{
			if(touchPointX >= startLeftTopX && touchPointX < startLeftTopX + bitmapWidth)
			{
				stickerBitmapList.mirrorStickerBitmap();
				if(FingerPassword.getForward()==1)
				{
					FingerPassword.setForward(2);
				}
				else
				{
					FingerPassword.setForward(1);
				}
				return true;
			}
			else if(touchPointX >= startLeftTopX + bitmapWidth && touchPointX < startLeftTopX + bitmapWidth * 2)
			{
				stickerBitmapList.deleteOnTouchStickerBitmap();
				return true;
			}
		}
		return false;
	}

	/** 设置对图贴操作的工具栏的左上角的横坐标,纵坐标 从而使工具栏总是处于屏幕内 */
	public void setStartLeftTop(PointF leftBottomPoint)
	{
		startLeftTopX = leftBottomPoint.x;
		startLeftTopY = leftBottomPoint.y - bitmapHeight;
		if(startLeftTopX < 0)
		{
			startLeftTopX = 0;
		}
		if(startLeftTopY < 0)
		{
			startLeftTopY = 0;
		}
		if(startLeftTopX + bitmapWidth * toolsNum  > DrawAttribute.screenWidth)
		{
			startLeftTopX = DrawAttribute.screenWidth - bitmapWidth * toolsNum;
		}
		if(startLeftTopY + bitmapHeight > DrawAttribute.screenHeight)
		{
			startLeftTopX = DrawAttribute.screenHeight - bitmapHeight;
		}
	}
}
