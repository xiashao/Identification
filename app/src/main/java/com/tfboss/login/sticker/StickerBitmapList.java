package com.tfboss.login.sticker;

import com.tfboss.login.DrawActivity;
import com.tfboss.login.DrawView;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class StickerBitmapList
{
	private StickerBitmap[] stickerBitmaps;
	private int size=0;
	private int onTouchStickerBitmapIndex;

	private StickerTools stickerTools;
	private boolean isStickerToolsDraw;

	private DrawView container;

	private final int TOOLSTOTALDRAWTIME = 75;
	private int stickerToolsDrawTime=0;

	public StickerBitmapList(DrawView container)
	{
		this.container = container;

		stickerBitmaps = new StickerBitmap[1];
		stickerTools = new StickerTools(container,this);
		isStickerToolsDraw = false;
	}

	public boolean addStickerBitmap(StickerBitmap stickerBitmap) {
		/** Toast.makeText(container.context,"addStickerBitmap",1).show(); 这句话去掉 */
		stickerBitmaps[0] = stickerBitmap;
		size=1;
		return true;
	}

	public void setOnTouchStickerBitmapLock()
	{
		stickerBitmaps[0].setLock();
	}

	public void mirrorStickerBitmap() {
		stickerBitmaps[0].mirrorTheBitmap();
	}

	public void drawOnTouchStickerBitmapInCanvas()
	{
		stickerBitmaps[0].drawBitmap(container.getPaintCanvas());
		deleteOnTouchStickerBitmap();
	}

	public void deleteOnTouchStickerBitmap()
	{
		if(stickerBitmaps[0]!=null) {
			DrawActivity.isChoose=false;
			stickerBitmaps[0] = null;
			size = 0;
			isStickerToolsDraw = false;
		}
	}

	public void drawStickerBitmapList(Canvas canvas)
	{
		for(int i = 0; i < size; i++)
		 {
			stickerBitmaps[i].drawBitmap(canvas);
		}
		if(isStickerToolsDraw)
		{
			stickerTools.drawTools(canvas, stickerBitmaps[onTouchStickerBitmapIndex].isLock());

			stickerToolsDrawTime++;

			if(stickerToolsDrawTime >= TOOLSTOTALDRAWTIME)
			{
				stickerToolsDrawTime = 0;

				isStickerToolsDraw = false;
			}
		}
	}

	public void setIsStickerToolsDraw(boolean isStickerToolsDraw, PointF leftTopPoint)
	{
		/** 是否绘制工具栏的标识--(对图贴操作的工具的工具栏) */
		this.isStickerToolsDraw = isStickerToolsDraw;

		if(isStickerToolsDraw)
		{
			stickerTools.setStartLeftTop(leftTopPoint);

			stickerToolsDrawTime = 0;
		}
	}

	public void onTouchEvent(MotionEvent event)
	{
		if(stickerBitmaps[0]!=null)
		stickerBitmaps[0].onTouchEvent(event);
	}

	public int getOnTouchType(float x, float y)
	{
		if(isStickerToolsDraw && stickerTools.setOnTouchEvent(x, y))
		{
			return 1;	/** 返回1,表示点击的是贴图工具栏 */
		}
		for(int i = size - 1; i >=0; i--)
		{
			/** 列举所有列表中的贴图,如果手指在屏幕上的点在该贴图上 */
			if(stickerBitmaps[i].isPointInsideBitmap(x, y))
			{
				/** 设置当前被选择的贴图为该被点击的贴图的索引 */
				onTouchStickerBitmapIndex = i;

				return 0;	/** 返回0,表示手指触碰的是贴图 */
			}
		}

		return -1;	/** 返回0,表示手指触碰的是贴图 */
	}

	public void freeBitmaps()
	{
		for(int i = 0; i < size; i++)
		{
			stickerBitmaps[i].bitmap.recycle();
		}
	}
}