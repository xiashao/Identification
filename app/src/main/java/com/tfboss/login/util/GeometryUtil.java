package com.tfboss.login.util;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.tfboss.login.*;
import com.tfboss.login.geometry.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

/** 几何图形点击监听类 */
public class GeometryUtil
{
	/** 用于决定绘制的几何图形的类型的enum. */
	private enum GraphicType{LINE, OVAL, RECTANGLE, ROUNDEDRECTANGLE, TRIANGLE, RIGHTTRIANGLE,
		DIAMOND, PENTAGON, HEXAGON, STAR}

	private DrawActivity drawActivity;
	private DrawView drawView;

	private ImageView selectedGraphic; /** 被选择的几何图形的ImageView. */
	private GraphicType graphicType; /** 用于决定绘制的几何图形的类型的enum. */
	private ImageView selectedColor; /** 显示被选择的颜色的ImageView */
	private SeekBar sizeSeekBar; /** 线条粗细选择的seekBar. */
	private CheckBox strokeCheckBox; /** 确定画线是否空心的CheckBox. */
	private ColorPickerDialog colorPickerDialog; /** 进行颜色选择的弹出框. */

	/** 构造方法. */
	public GeometryUtil(DrawActivity drawActivity,DrawView drawView){
		this.drawActivity = drawActivity;
		this.drawView = drawView;
		/** 初始化全局变量的各个控件,生成或者关联到layout. */
		selectedGraphic = (ImageView)drawActivity.findViewById(R.id.graphic_line);
		graphicType = GraphicType.LINE;

		/** 被选择的几何图形的ImageView背景颜色为黄色. */
		selectedGraphic.setBackgroundColor(DrawAttribute.backgroundOnClickColor);
		selectedColor = (ImageView)drawActivity.findViewById(R.id.graphiccolorview);
		sizeSeekBar = (SeekBar)drawActivity.findViewById(R.id.graphicsizeskb);
		strokeCheckBox = (CheckBox)drawActivity.findViewById(R.id.strokckb);
		colorPickerDialog = new ColorPickerDialog(drawActivity,selectedColor);
	}

	public void graphicPicSetOnClickListener()
	{
		/** 获取各个几何图形的ImageView并将ImageView放入到刚生成的ImageView[]中. */
		ImageView[] graphics = new ImageView[10];
		graphics[0] = (ImageView)drawActivity.findViewById(R.id.graphic_line);
		graphics[1] = (ImageView)drawActivity.findViewById(R.id.graphic_oval);
		graphics[2] = (ImageView)drawActivity.findViewById(R.id.graphic_rectangle);
		graphics[3] = (ImageView)drawActivity.findViewById(R.id.graphic_roundedrectangle);
		graphics[4] = (ImageView)drawActivity.findViewById(R.id.graphic_triangle);
		graphics[5] = (ImageView)drawActivity.findViewById(R.id.graphic_righttriangle);
		graphics[6] = (ImageView)drawActivity.findViewById(R.id.graphic_diamond);
		graphics[7] = (ImageView)drawActivity.findViewById(R.id.graphic_pentagon);
		graphics[8] = (ImageView)drawActivity.findViewById(R.id.graphic_hexagon);
		graphics[9] = (ImageView)drawActivity.findViewById(R.id.graphic_star);

		/** 设置每一个ImageView点击时的监听类. */
		GraphicOnClickListener graphicOnTouchListener = new GraphicOnClickListener(graphics);
		for(int i = 0;i < graphics.length;i++) {
			graphics[i].setOnClickListener(graphicOnTouchListener);
		}

		/** 设置颜色选择按钮被点击时的监听方法:使用ColorPickerDialog类弹出颜色选择弹出框.*/
		Button chooseColorButton = (Button)drawActivity.findViewById(R.id.graphiccolorbtn);
		chooseColorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorPickerDialog.getDialog().show();
			}
		});

		/** 设置绘图按钮被点击时的监听方法:*/
		Button startDrawingbutton = (Button)drawActivity.findViewById(R.id.graphicdrawbtn);
		startDrawingbutton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				drawView.stickerBitmapList.deleteOnTouchStickerBitmap();
				drawView.cleanPaintBitmap()	;
				/** 将被选择的几何图形对应的类设置到DrawView中. */
				drawView.setBasicGeometry(GetGraphicClass(graphicType));

				DrawActivity.isChoose=true;

				FingerPassword.setPic(pic);
				FingerPassword.setForward(forward);
				FingerPassword.setRotate(rotate);
				FingerPassword.setScale(scale);
			}
		});
	}
	Integer pic;
	Integer forward;
	Float rotate;
	Float scale;

	/** 几何图形的ImageView被点击时的监听类. */
	class GraphicOnClickListener implements View.OnClickListener
	{
		private ImageView[] graphics; /** 每个ImageView代表一个几何图形 */

	public GraphicOnClickListener(ImageView[] graphics) {
		this.graphics = graphics;
	}

		@Override
		public void onClick(View v)
		{
			selectedGraphic.setBackgroundColor(0x00000000);

			/** 根据ImageView的id来确定选择的是哪一个几何图形. */
			switch(v.getId())
			{
				case R.id.graphic_line:
					selectedGraphic = graphics[0];
					graphicType = GraphicType.LINE;
					pic=149;
					forward=0;
					rotate=0f;
					scale=1f;
					break;
				case R.id.graphic_oval:
					selectedGraphic = graphics[1];
					graphicType = GraphicType.OVAL;
					pic=150;
					forward=0;
					rotate=0f;
					scale=1f;
					break;
				case R.id.graphic_rectangle:
					selectedGraphic = graphics[2];
					graphicType = GraphicType.RECTANGLE;
					pic=151;
					forward=0;
					rotate=0f;
					scale=1f;
					break;
				case R.id.graphic_roundedrectangle:
					selectedGraphic = graphics[3];
					graphicType = GraphicType.ROUNDEDRECTANGLE;
					pic=152;
					forward=0;
					rotate=0f;
					scale=1f;
					break;
				case R.id.graphic_triangle:
					selectedGraphic = graphics[4];
					graphicType = GraphicType.TRIANGLE;
					pic=153;
					forward=0;
					rotate=0f;
					scale=1f;
					break;
				case R.id.graphic_righttriangle:
					selectedGraphic = graphics[5];
					graphicType = GraphicType.RIGHTTRIANGLE;
					pic=154;
					forward=0;
					rotate=0f;
					scale=1f;
					break;
				case R.id.graphic_diamond:
					selectedGraphic = graphics[6];
					graphicType = GraphicType.DIAMOND;
					pic=155;
					forward=0;
					rotate=0f;
					scale=1f;break;
				case R.id.graphic_pentagon:
					selectedGraphic = graphics[7];
					graphicType = GraphicType.PENTAGON;
					pic=156;
					forward=0;
					rotate=0f;
					scale=1f;break;
				case R.id.graphic_hexagon:
					selectedGraphic = graphics[8];
					graphicType = GraphicType.HEXAGON;
					pic=157;
					forward=0;
					rotate=0f;
					scale=1f;break;
				case R.id.graphic_star:
					selectedGraphic = graphics[9];
					graphicType = GraphicType.STAR;
					pic=158;
					forward=0;
					rotate=0f;
					scale=1f;
			}
			/** 设置 被选择的几何图形的ImageView的背景色变成黄色. */
			selectedGraphic.setBackgroundColor(DrawAttribute.backgroundOnClickColor);
		}
	}

	/** 设置画笔的宽度,颜色等参数.
	 根据被选择的ImageView所代表的几何图形,生成该几何图形对应的类,并返回该类. */
	private BasicGeometry GetGraphicClass(GraphicType graphicType) {
		Paint paint = new Paint();
		paint.setColor(colorPickerDialog.getColor());
		paint.setStrokeWidth(sizeSeekBar.getProgress());
		if(strokeCheckBox.isChecked())paint.setStyle(Style.STROKE);
		if(graphicType == GraphicType.LINE)return new LineGeometry(paint);
		if(graphicType == GraphicType.OVAL)return new OvalGeometry(paint);
		if(graphicType == GraphicType.RECTANGLE)return new RectangleGeometry(paint);
		if(graphicType == GraphicType.ROUNDEDRECTANGLE)return new RoundedRectangleGeometry(paint);
		if(graphicType == GraphicType.TRIANGLE)return new TriangleGeometry(paint);
		if(graphicType == GraphicType.RIGHTTRIANGLE)return new RightTriangleGeometry(paint);
		if(graphicType == GraphicType.DIAMOND)return new DiamondGeometry(paint);
		if(graphicType == GraphicType.PENTAGON)return new PentagonGeometry(paint);
		if(graphicType == GraphicType.HEXAGON)return new HexagonGeometry(paint);
		if(graphicType == GraphicType.STAR)return new StarGeometry(paint);
		return null;
	}
}
