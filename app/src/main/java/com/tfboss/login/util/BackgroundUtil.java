package com.tfboss.login.util;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.tfboss.login.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class BackgroundUtil {

	private DrawActivity drawActivity;
	private DrawView drawView;

	public BackgroundUtil(DrawActivity drawActivity,DrawView drawView){
		this.drawActivity = drawActivity;
		this.drawView = drawView;
	}

	public void backgroundPicSetOnClickListener()
	{
		/** 获取不同颜色的画布背景的ImageView. */
		ImageView[] backgrounds = new ImageView[47];
		backgrounds[0] = (ImageView)drawActivity.findViewById(R.id.smallpaper00);
		backgrounds[1] = (ImageView)drawActivity.findViewById(R.id.smallpaper01);
		backgrounds[2] = (ImageView)drawActivity.findViewById(R.id.smallpaper02);
		backgrounds[3] = (ImageView)drawActivity.findViewById(R.id.smallpaper03);
		backgrounds[4] = (ImageView)drawActivity.findViewById(R.id.smallpaper04);
		backgrounds[5] = (ImageView)drawActivity.findViewById(R.id.smallpaper05);
		backgrounds[6] = (ImageView)drawActivity.findViewById(R.id.smallpaper06);
		backgrounds[7] = (ImageView)drawActivity.findViewById(R.id.smallpaper07);
		backgrounds[8] = (ImageView)drawActivity.findViewById(R.id.smallpaper08);
		backgrounds[9] = (ImageView)drawActivity.findViewById(R.id.smallpaper09);
		backgrounds[10] = (ImageView)drawActivity.findViewById(R.id.smallpaper10);
		backgrounds[11] = (ImageView)drawActivity.findViewById(R.id.smallpaper11);
		backgrounds[12] = (ImageView)drawActivity.findViewById(R.id.smallpaper12);
		backgrounds[13] = (ImageView)drawActivity.findViewById(R.id.smallpaper13);
		backgrounds[14] = (ImageView)drawActivity.findViewById(R.id.smallpaper14);
		backgrounds[15] = (ImageView)drawActivity.findViewById(R.id.smallpaper15);
		backgrounds[16] = (ImageView)drawActivity.findViewById(R.id.smallpaper16);
		backgrounds[17] = (ImageView)drawActivity.findViewById(R.id.smallpaper17);
		backgrounds[18] = (ImageView)drawActivity.findViewById(R.id.smallpaper18);
		backgrounds[19] = (ImageView)drawActivity.findViewById(R.id.smallpaper19);
		backgrounds[20] = (ImageView)drawActivity.findViewById(R.id.smallpaper20);
		backgrounds[21] = (ImageView)drawActivity.findViewById(R.id.smallpaper21);
		backgrounds[22] = (ImageView)drawActivity.findViewById(R.id.smallpaper22);
		backgrounds[23] = (ImageView)drawActivity.findViewById(R.id.smallpaper23);
		backgrounds[24] = (ImageView)drawActivity.findViewById(R.id.smallpaper24);
		backgrounds[25] = (ImageView)drawActivity.findViewById(R.id.smallpaper25);
		backgrounds[26] = (ImageView)drawActivity.findViewById(R.id.smallpaper26);
		backgrounds[27] = (ImageView)drawActivity.findViewById(R.id.smallpaper27);
		backgrounds[28] = (ImageView)drawActivity.findViewById(R.id.smallpaper28);
		backgrounds[29] = (ImageView)drawActivity.findViewById(R.id.smallpaper29);
		backgrounds[30] = (ImageView)drawActivity.findViewById(R.id.smallpaper30);
		backgrounds[31] = (ImageView)drawActivity.findViewById(R.id.smallpaper31);
		backgrounds[32] = (ImageView)drawActivity.findViewById(R.id.smallpaper32);
		backgrounds[33] = (ImageView)drawActivity.findViewById(R.id.smallpaper33);
		backgrounds[34] = (ImageView)drawActivity.findViewById(R.id.smallpaper34);
		backgrounds[35] = (ImageView)drawActivity.findViewById(R.id.smallpaper35);
		backgrounds[36] = (ImageView)drawActivity.findViewById(R.id.smallpaper36);
		backgrounds[37] = (ImageView)drawActivity.findViewById(R.id.smallpaper37);
		backgrounds[38] = (ImageView)drawActivity.findViewById(R.id.smallpaper38);
		backgrounds[39] = (ImageView)drawActivity.findViewById(R.id.smallpaper39);
		backgrounds[40] = (ImageView)drawActivity.findViewById(R.id.smallpaper40);
		backgrounds[41] = (ImageView)drawActivity.findViewById(R.id.smallpaper41);
		backgrounds[42] = (ImageView)drawActivity.findViewById(R.id.smallpaper42);
		backgrounds[43] = (ImageView)drawActivity.findViewById(R.id.smallpaper43);
		backgrounds[44] = (ImageView)drawActivity.findViewById(R.id.smallpaper44);
		backgrounds[45] = (ImageView)drawActivity.findViewById(R.id.smallpaper45);
		backgrounds[46] = (ImageView)drawActivity.findViewById(R.id.smallpaper46);

		/** 设置画布背景类的点击监听事件类. */
		BackgroundOnTouchListener backgroundOnTouchListener = new BackgroundOnTouchListener();
		for(int i = 0;i < backgrounds.length;i++) {
			backgrounds[i].setOnTouchListener(backgroundOnTouchListener);
		}
	}

	/** 画布背景类的点击监听事件类. */
	class BackgroundOnTouchListener implements View.OnTouchListener
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN: /** 手指按下时 */
					v.setBackgroundColor(DrawAttribute.backgroundOnClickColor);/** 设置被点击的ImageView的背景色为橙黄色 */
					break;
				case MotionEvent.ACTION_UP:v.setBackgroundColor(0x00ffffff); /** 手指抬起时 */
					String s = "bigpaper00.jpg";
					switch(v.getId())  /** 根据被点击的ImageView图的id,确认用于做背景的图片的文件名. */
					{
						case R.id.smallpaper00:s = "bigpaper00.jpg";
							FingerPassword.setBack(0);
							break;
						case R.id.smallpaper01:s = "bigpaper01.jpg";
							FingerPassword.setBack(1);
							break;
						case R.id.smallpaper02:s = "bigpaper02.jpg";
							FingerPassword.setBack(2);
							break;
						case R.id.smallpaper03:s = "bigpaper03.jpg";
							FingerPassword.setBack(3);break;
						case R.id.smallpaper04:s = "bigpaper04.jpg";
							FingerPassword.setBack(4);break;
						case R.id.smallpaper05:s = "bigpaper05.jpg";
							FingerPassword.setBack(5);break;
						case R.id.smallpaper06:s = "bigpaper06.jpg";
							FingerPassword.setBack(6);break;
						case R.id.smallpaper07:s = "bigpaper07.jpg";
							FingerPassword.setBack(7);break;
						case R.id.smallpaper08:s = "bigpaper08.jpg";
							FingerPassword.setBack(8);break;
						case R.id.smallpaper09:s = "bigpaper09.jpg";
							FingerPassword.setBack(9);break;
						case R.id.smallpaper10:s = "bigpaper10.jpg";
							FingerPassword.setBack(10);break;
						case R.id.smallpaper11:s = "bigpaper11.jpg";
							FingerPassword.setBack(11);break;
						case R.id.smallpaper12:s = "bigpaper12.jpg";
							FingerPassword.setBack(12);break;
						case R.id.smallpaper13:s = "bigpaper13.jpg";
							FingerPassword.setBack(13);break;
						case R.id.smallpaper14:s = "bigpaper14.jpg";
							FingerPassword.setBack(14);break;
						case R.id.smallpaper15:s = "bigpaper15.jpg";
							FingerPassword.setBack(15);break;
						case R.id.smallpaper16:s = "bigpaper16.jpg";
							FingerPassword.setBack(16);break;
						case R.id.smallpaper17:s = "bigpaper17.jpg";
							FingerPassword.setBack(17);break;
						case R.id.smallpaper18:s = "bigpaper18.jpg";
							FingerPassword.setBack(18);break;
						case R.id.smallpaper19:s = "bigpaper19.jpg";
							FingerPassword.setBack(19);break;
						case R.id.smallpaper20:s = "bigpaper20.jpg";
							FingerPassword.setBack(20);break;
						case R.id.smallpaper21:s = "bigpaper21.jpg";
							FingerPassword.setBack(21);break;
						case R.id.smallpaper22:s = "bigpaper22.jpg";
							FingerPassword.setBack(22);break;
						case R.id.smallpaper23:s = "bigpaper23.jpg";
							FingerPassword.setBack(23);break;
						case R.id.smallpaper24:s = "bigpaper24.jpg";
							FingerPassword.setBack(24);break;
						case R.id.smallpaper25:s = "bigpaper25.jpg";
							FingerPassword.setBack(25);break;
						case R.id.smallpaper26:s = "bigpaper26.jpg";
							FingerPassword.setBack(26);break;
						case R.id.smallpaper27:s = "bigpaper27.jpg";
							FingerPassword.setBack(27);break;
						case R.id.smallpaper28:s = "bigpaper28.jpg";
							FingerPassword.setBack(28);break;
						case R.id.smallpaper29:s = "bigpaper29.jpg";
							FingerPassword.setBack(29);break;
						case R.id.smallpaper30:s = "bigpaper30.jpg";
							FingerPassword.setBack(30);break;
						case R.id.smallpaper31:s = "bigpaper31.jpg";
							FingerPassword.setBack(31);break;
						case R.id.smallpaper32:s = "bigpaper32.jpg";
							FingerPassword.setBack(32);break;
						case R.id.smallpaper33:s = "bigpaper33.jpg";
							FingerPassword.setBack(33);break;
						case R.id.smallpaper34:s = "bigpaper34.jpg";
							FingerPassword.setBack(34);break;
						case R.id.smallpaper35:s = "bigpaper35.jpg";
							FingerPassword.setBack(35);break;
						case R.id.smallpaper36:s = "bigpaper36.jpg";
							FingerPassword.setBack(36);break;
						case R.id.smallpaper37:s = "bigpaper37.jpg";
							FingerPassword.setBack(37);break;
						case R.id.smallpaper38:s = "bigpaper38.jpg";
							FingerPassword.setBack(38);break;
						case R.id.smallpaper39:s = "bigpaper39.jpg";
							FingerPassword.setBack(39);break;
						case R.id.smallpaper40:s = "bigpaper40.jpg";
							FingerPassword.setBack(40);break;
						case R.id.smallpaper41:s = "bigpaper41.jpg";
							FingerPassword.setBack(41);break;
						case R.id.smallpaper42:s = "bigpaper42.jpg";
							FingerPassword.setBack(42);break;
						case R.id.smallpaper43:s = "bigpaper43.jpg";
							FingerPassword.setBack(43);break;
						case R.id.smallpaper44:s = "bigpaper44.jpg";
							FingerPassword.setBack(44);break;
						case R.id.smallpaper45:s = "bigpaper45.jpg";
							FingerPassword.setBack(45);break;
						case R.id.smallpaper46:s = "bigpaper46.jpg";
							FingerPassword.setBack(46);
					}
					/** 设置DrawView的background. */
					drawView.setBackgroundBitmap(DrawAttribute.getImageFromAssetsFile(drawActivity,s,true), true);
					break;
				case MotionEvent.ACTION_CANCEL:
					/** 设置被点的ImageView的背景色为白色. */
					v.setBackgroundColor(0x00ff0000);
			}
			return true;
		}
	}
}
