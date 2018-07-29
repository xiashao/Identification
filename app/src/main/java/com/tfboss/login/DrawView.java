package com.tfboss.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.tfboss.login.sticker.*;
import com.tfboss.login.geometry.*;
import com.tfboss.login.util.*;
import com.tfboss.login.undoandredo.UndoAndRedo;


/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

/** 在DrawActivity中用于画画的View区域 */

public class DrawView extends View implements Runnable
{
	private Bitmap backgroundBitmap = null;	/** 从资源中获取的屏幕大小,纯白颜色的图片(用于画布背景) */
	private PointF backgroundBitmapLeftTopP = null;	/** 用于画布背景的纯白色图片的左上角的点 */
	private Bitmap paintBitmap = null;	/** 屏幕大小的图片(画布绘制后数据自动保存到这个paintBitmap中) */
	private Canvas paintCanvas = null;	/** 画布 */
	public StickerBitmapList stickerBitmapList = null;	/** 存储图贴列表 和 对图贴列表操作的方法的类 */

	/** 存储  （一个几何图形） 以及 （操纵几何图形进行位移,旋转,或者缩放变换的方法） 的类 */
	public BasicGeometry basicGeometry = null;

	/** 三个蓝色小点的图标(点击之后原本被画画用的View占据的界面会在上方和下方分别弹出菜单栏和工具栏) */
	private Bitmap visibleBtnBitmap = null;

	/** 根据这个enum类型的DrawStatus的数据决定画画的区域DrawView的绘图样式 */
	private DrawAttribute.DrawStatus drawStatus;

	/** 这个类中包含--存储图片的图片数组(图片指的是当画图板用的Bitmap),以及对这些画板图片操作的方法 */
	private UndoAndRedo undoAndRedo;

	public Context context;	/** 上下文 */

	/** 屏幕左右两边各有60像素宽的边缘 */
	private final int VISIBLE_BTN_WIDTH;

	/** 屏幕上下两边各有40像素宽的边缘 */
	private final int VISIBLE_BTN_HEIGHT;

	/** enum类型的数据,不同的数据值表示点击的是屏幕的那个区域. */
	private enum TouchLayer
	{
		GEOMETRY_LAYER, PAINT_LAYER, STICKER_BITMAP, STICKER_TOOL, VISIBLE_BTN
	}

	/** enum类型的数据,不同的数据值表示点击的是屏幕的那个区域. */
	private TouchLayer touchLayer;

	public DrawView(Context context, AttributeSet attributeSet)
	{
		super(context,attributeSet);
		this.context = context;	/** 上下文 */

		VISIBLE_BTN_WIDTH = DrawAttribute.screenWidth/8;
		VISIBLE_BTN_HEIGHT = DrawAttribute.screenHeight/21;

		/** getImageFormAssetsFile方法从assets文件夹中读取一个图像文件(这里是屏幕大小的纯白色图片)并按Bitmap形式返回 */
		backgroundBitmap = DrawAttribute.getImageFromAssetsFile(context,"bigpaper00.jpg",true);

		/** 设置画布的背景图片的左上角为(0,0). */
		backgroundBitmapLeftTopP = new PointF(0,0);

		/** 生成屏幕大小的图片--paintBitmap(画布绘制后数据自动保存到这个paintBitmap中) */
		paintBitmap = Bitmap.createBitmap(DrawAttribute.screenWidth,
				DrawAttribute.screenHeight, Bitmap.Config.ARGB_8888);

		/** 生成画布(画布绘制后数据自动保存到这个paintBitmap中) */
		paintCanvas = new Canvas(paintBitmap);

		/** 将画布填充成白色 */
		paintCanvas.drawARGB(0, 255, 255, 255);

		/** 生成 (存储 图贴列表 和 对图贴列表操作的方法) 的类 */
		stickerBitmapList = new StickerBitmapList(this);

		/** 这个类中包含--存储图片的图片数组(图片指的是当画图板用的Bitmap),以及对这些画板图片操作的方法 */
		undoAndRedo = new UndoAndRedo();

		/** 三个蓝色小点的图标 */
		Bitmap visibleBtnBitmap2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.drawvisiblebtn)).getBitmap();
		visibleBtnBitmap=Bitmap.createScaledBitmap(visibleBtnBitmap2,VISIBLE_BTN_WIDTH,VISIBLE_BTN_HEIGHT,false);

		/** 根据这个enum类型的DrawStatus的数据决定画画的区域DrawView的绘图样式 */
		/** 这里设置绘图样式为CASUAL_WATER(随意水彩样式) */
		this.drawStatus = DrawAttribute.DrawStatus.CASUAL_WATER;

		new Thread(this).start();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		try {
		canvas.drawColor(Color.WHITE);

		}
		catch(Exception e)
		{
			Toast.makeText(context,"1",1).show();
		}

		try {

		canvas.drawBitmap(backgroundBitmap, backgroundBitmapLeftTopP.x,
				backgroundBitmapLeftTopP.y, null);

		}
		catch(Exception e)
		{
			Toast.makeText(context,"2",1).show();
		}

		try {

		canvas.drawBitmap(paintBitmap, 0,0, null);

		}
		catch(Exception e)
		{
			Toast.makeText(context,"3",1).show();
		}

		try {

			if (basicGeometry != null) {
				basicGeometry.drawGraphic(canvas);
			}

		}
		catch(Exception e)
		{
			Toast.makeText(context,"4",1).show();
		}
		try {

			stickerBitmapList.drawStickerBitmapList(canvas);

		}
		catch(Exception e)
		{
			Toast.makeText(context,"5",1).show();
		}
		try {
			/** 将三个蓝色小点的图标画到画布上(屏幕右边的细边缘的位置). */
			canvas.drawBitmap(visibleBtnBitmap, DrawAttribute.screenWidth - VISIBLE_BTN_WIDTH, 0, null);
		}
		catch(Exception e)
		{
			Toast.makeText(context,"6",1).show();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			float x = event.getX();
			float y = event.getY();

			if (isClickOnVisibleBtn(x, y))
			{
				touchLayer = TouchLayer.VISIBLE_BTN;

				((DrawActivity) context).setUpAndButtomBarVisible(true);

				return true;
			}
			else
			{
				((DrawActivity) context).setUpAndButtomBarVisible(false);
			}
			int touchType = stickerBitmapList.getOnTouchType(x, y);

			switch (touchType) {
				case 1:
					touchLayer = TouchLayer.STICKER_TOOL;
					return true;
				case 0:
					touchLayer = TouchLayer.STICKER_BITMAP;
					break;
				case -1: /** 如果点击的是画布: */
					if (basicGeometry != null) {
						touchLayer = TouchLayer.GEOMETRY_LAYER;
					}
					else
					{
						touchLayer = TouchLayer.PAINT_LAYER;
					}
				}
			}
			if (touchLayer == TouchLayer.PAINT_LAYER) {
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					undoAndRedo.addBitmap(paintBitmap);
				}
			}
			else if (touchLayer == TouchLayer.GEOMETRY_LAYER) {
				if (basicGeometry != null)
					basicGeometry.onTouchEvent(event);
			}
			else if (touchLayer == TouchLayer.STICKER_BITMAP) {
				stickerBitmapList.onTouchEvent(event);
			}
		return true;
	}

	/** 如果点击的是屏幕的画图区域,返回true,否则返回false.(屏幕上下左右各有很细的边缘--不是画图区域) */
	private boolean isClickOnVisibleBtn(float x, float y)
	{
		if(x > DrawAttribute.screenWidth - VISIBLE_BTN_WIDTH && x < DrawAttribute.screenWidth
				&& y < VISIBLE_BTN_HEIGHT)
		{
			return true;
		}
		return false;
	}

	/** 设置如果当前UI线程没有被中断,则每隔0.01秒执行一次这个View的onDraw方法 */
	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			postInvalidate();
		}
	}

	public void setBackgroundBitmap(Bitmap bitmap, boolean isFromSystem)
	{
		if(isFromSystem)
		{
			backgroundBitmap = bitmap;
			backgroundBitmapLeftTopP.set(0, 0);
		}
		else
		{
			float scaleWidth = bitmap.getWidth() * 1.0f / DrawAttribute.screenWidth;
			float scaleHeight = bitmap.getHeight()* 1.0f / DrawAttribute.screenHeight;
			float scale = scaleWidth > scaleHeight?scaleWidth : scaleHeight;
			if(scale > 1.01)
			{
				backgroundBitmap = Bitmap.createScaledBitmap(bitmap,
						(int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), false);
			}
			else
			{
				backgroundBitmap = bitmap;
			}
			/** 设置画布背景图的左上角,使画布背景图显示在画布正中央. */
			backgroundBitmapLeftTopP.x = (DrawAttribute.screenWidth - backgroundBitmap.getWidth())/2;
			backgroundBitmapLeftTopP.y = (DrawAttribute.screenHeight - backgroundBitmap.getHeight())/2;
		}
	}

	public void setBasicGeometry(BasicGeometry geometry)
	{
		this.basicGeometry = geometry;
	}

	public void addStickerBitmap(Bitmap bitmap)
	{
		/** 设置 是否绘制工具栏的标识--(对图贴操作的工具的工具栏) 为false */
		stickerBitmapList.setIsStickerToolsDraw(false,null);

		if(basicGeometry!=null)
		{
			basicGeometry=null;
		}

		/** 用参数bitmap生成一个StickerBitmap(图贴类),将图贴添加到图贴列表中. */
		if (!stickerBitmapList.addStickerBitmap(new StickerBitmap(this, stickerBitmapList, bitmap)))
		{
			Toast.makeText(context, "贴图太多了！", Toast.LENGTH_SHORT).show();
		}
	}

	/** 返回DrawView(用于画图的View)中的画布. */
	public Canvas getPaintCanvas()
	{
		return paintCanvas;
	}

	public void cleanPaintBitmap()
	{
		/** DST_OUT表示取下层中 未与上层重合部分 的图片 */
		paintCanvas.drawColor(0xffffffff, PorterDuff.Mode.DST_OUT);
	}


	/**	释放backgroundBitmap占据的空间(用于绘制的画布的背景图).
		释放paintBitmap占据的空间(绘制到画布上的图被保存到paintBitmap中).
		存储图贴列表 中的所有Bitmap的资源全部释放.
		将图片数组中的图片(当画图板用的Bitmap)所占用的空间全部释放. */
	public void freeBitmaps()
	{
		/** 释放backgroundBitmap占据的空间(用于绘制的画布的背景图). */
		backgroundBitmap.recycle();

		/** 释放paintBitmap占据的空间(绘制到画布上的图被保存到paintBitmap中). */
		paintBitmap.recycle();

		/** 存储图贴列表 中的所有Bitmap的资源全部释放. */
		stickerBitmapList.freeBitmaps();

		/** 将图片数组中的图片(当画图板用的Bitmap)所占用的空间全部释放. */
		undoAndRedo.freeBitmaps();
	}
}
