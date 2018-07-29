package com.tfboss.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.tfboss.login.client.User;
import com.tfboss.login.net.Client;
import com.tfboss.login.net.ClientInputThread;
import com.tfboss.login.net.ClientOutputThread;
import com.tfboss.login.util.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class DrawActivity extends MyActivity implements View.OnClickListener
{
	private DrawView drawView;

	private LinearLayout topBar;
	private LinearLayout bottomBar;

	private HorizontalScrollView backgroundList;

	private LinearLayout stickerList;
	private LinearLayout geometryTool;
	private LinearLayout wordTool;

	User user;
	String TAG;
	String function;

	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private Toast mToast;

	AlertDialog.Builder builder;
	AlertDialog.Builder builder2;

	Boolean isRegisterFirst;
	Boolean isRegisterSecond;
	Boolean isLogin;

	String fingerPassword;

	public static Boolean isChoose;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		isChoose=false;

		try {
			setContentView(R.layout.draw);/** 这个layout中包含多个其他layout */

			application = (MyApplication) getApplicationContext();
			client = application.getClient();
			in = client.getClientInputThread();
			out = client.getClientOutputThread();

			mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

			Intent intent = getIntent();
			user = (User) intent.getSerializableExtra("user");
			TAG = (String) intent.getSerializableExtra("TAG");
			function = (String) intent.getSerializableExtra("function");

			if ("register".equals(function)) {
				isRegisterFirst = true;
				isRegisterSecond = false;
				isLogin = false;
			} else if ("login".equals(function)) {
				isRegisterFirst = false;
				isRegisterSecond = false;
				isLogin = true;
			}

			/** topBar关联到id为drawTop的LinearLayout,drawTop是draw.xml中include的 包含(主菜单,保存,前进,后退)四种选项的LinearLayout */
			topBar = (LinearLayout) this.findViewById(R.id.drawtop);

			/** bottomBar关联到id为drawBottom的LinearLayout,drawBottom是draw.xml中include的 包含 不同画笔或工具 的LinearLayout */
			bottomBar = (LinearLayout) this.findViewById(R.id.drawbottom);

			/** 设置topBar的点击事件为空(此处有用处，切勿删除) */
			topBar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			/** 设置bottomBar的点击事件为空(此处有用处,切勿删除) */
			bottomBar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});

			/** drawView是draw.xml中的包含的 DrawView类型的 用于画画的View */
			drawView = (DrawView) this.findViewById(R.id.drawview);

			backgroundList = (HorizontalScrollView) this.findViewById(R.id.backgroundlist);/** 包含不同颜色画布背景ImageView的横向滚动View */
			stickerList = (LinearLayout) this.findViewById(R.id.stickerlist);/** 包含一个ViewPager的LinearLayout */
			wordTool = (LinearLayout) this.findViewById(R.id.wordtool);/** 显示一个TextView--"文字功能尚未开发" */
			geometryTool = (LinearLayout) this.findViewById(R.id.geometrytool);/** 包含不同的几何图形的ImageView,颜色选择和点击绘制的按钮 */

			geometryTool.setVisibility(View.INVISIBLE);
			backgroundList.setVisibility(View.INVISIBLE);
			stickerList.setVisibility(View.INVISIBLE);
			wordTool.setVisibility(View.VISIBLE);

			/** 将选择不同种类画笔的ImageView全部获取.(如:点击某个ImageView,在bottomBar中显示 蜡笔 滚动栏,点击另一个,显示橡皮滚动栏) */
			ImageView drawGraphicButton = (ImageView) this.findViewById(R.id.drawgraphicbtn);
			ImageView backgroundButton = (ImageView) this.findViewById(R.id.drawbackgroundbtn);
			ImageView stickerButton = (ImageView) this.findViewById(R.id.stickerbtn);
			ImageView wordsButton = (ImageView) this.findViewById(R.id.drawwordsbtn);

			/** 获取topBar中的所有按钮. */
			ImageButton drawbackButton = (ImageButton) this.findViewById(R.id.drawbackbtn);
			ImageButton saveButton = (ImageButton) this.findViewById(R.id.drawsavebtn);
			ImageButton visibleButton = (ImageButton) this.findViewById(R.id.drawvisiblebtn);

			/** 选择几何图的按钮,方法与水彩笔类似 */
			drawGraphicButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setBackgroundColor(DrawAttribute.backgroundOnClickColor);
							break;
						case MotionEvent.ACTION_UP:
							v.setBackgroundColor(0x00ffffff);
							geometryTool.setVisibility(View.VISIBLE);
							backgroundList.setVisibility(View.INVISIBLE);
							stickerList.setVisibility(View.INVISIBLE);
							wordTool.setVisibility(View.INVISIBLE);
							break;
						case MotionEvent.ACTION_CANCEL:
							v.setBackgroundColor(0x00ff0000);
					}
					return true;
				}
			});
			/** 选择壁纸按钮,方法与水彩笔类似 */
			backgroundButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setBackgroundColor(DrawAttribute.backgroundOnClickColor);
							break;
						case MotionEvent.ACTION_UP:
							v.setBackgroundColor(0x00ffffff);
							geometryTool.setVisibility(View.INVISIBLE);
							backgroundList.setVisibility(View.VISIBLE);
							stickerList.setVisibility(View.INVISIBLE);
							wordTool.setVisibility(View.INVISIBLE);
							break;
						case MotionEvent.ACTION_CANCEL:
							v.setBackgroundColor(0x00ff0000);
					}
					/*flawOperation();*/
					return true;
				}
			});
			/** 选择素材按钮，方法与水彩笔类似 */
			stickerButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setBackgroundColor(DrawAttribute.backgroundOnClickColor);
							break;
						case MotionEvent.ACTION_UP:
							v.setBackgroundColor(0x00ffffff);
							geometryTool.setVisibility(View.INVISIBLE);
							backgroundList.setVisibility(View.INVISIBLE);
							stickerList.setVisibility(View.VISIBLE);
							wordTool.setVisibility(View.INVISIBLE);
							break;
						case MotionEvent.ACTION_CANCEL:
							v.setBackgroundColor(0x00ff0000);
					}
				/*	flawOperation();*/
					return true;
				}
			});
			/** 点击文字按钮，方法与水彩笔类似 */
			wordsButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setBackgroundColor(DrawAttribute.backgroundOnClickColor);
							break;
						case MotionEvent.ACTION_UP:
							v.setBackgroundColor(0x00ffffff);
							geometryTool.setVisibility(View.INVISIBLE);
							backgroundList.setVisibility(View.INVISIBLE);
							stickerList.setVisibility(View.INVISIBLE);
							wordTool.setVisibility(View.VISIBLE);
							break;
						case MotionEvent.ACTION_CANCEL:
							v.setBackgroundColor(0x00ff0000);
					}
					/*flawOperation();*/
					return true;
				}
			});
			/** 设置每个几何图形的点击监听事件 */
			GeometryUtil graphicUtil = new GeometryUtil(this, drawView);
			graphicUtil.graphicPicSetOnClickListener();
			/** 设置每个背景图片ImageView点击的监听事件 */
			BackgroundUtil backgroundUtil = new BackgroundUtil(this, drawView);
			backgroundUtil.backgroundPicSetOnClickListener();
			/** 设置图贴点击的监听事件 */
			StickerUtil stickerUtil = new StickerUtil(this, drawView);
			stickerUtil.stickerPicSetOnClickListener();

			/** 设置顶部工具栏中按钮的点击事件监听器. */
			/** 点击返回按钮 */
			drawbackButton.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if ("register".equals(function)) {
						Intent intent = new Intent(DrawActivity.this, RegisterGetPassAndUser.class);
						String[] spank = user.getPasswordType().split("__");
						StringBuffer buffer = new StringBuffer();
						for (String spank2 : spank) {
							if (!"手势识别".equals(spank2)) {
								buffer.append(spank2 + "__");
							}
						}
						user.setPasswordType(buffer.toString().trim());
						intent.putExtra("user", user);
						intent.putExtra("TAG", TAG);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					} else if ("login".equals(function)) {
						Intent intent = new Intent(DrawActivity.this, LoginPassChoose.class);

						user.setPasswordType(user.getPasswordType() + "手势识别__");

						intent.putExtra("user", user);

						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
				}
			});
			try {
				builder2 = new AlertDialog.Builder(this);
				builder2.setTitle("与第一次效果不一致!是否再试一次?");
				builder2.setNegativeButton("再试一次", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						flawOperation();
						if (drawView.stickerBitmapList != null) {
							drawView.stickerBitmapList.deleteOnTouchStickerBitmap();
						}
						drawView.cleanPaintBitmap();
						drawView.setBackgroundBitmap(DrawAttribute.getImageFromAssetsFile(getApplicationContext(), "bigpaper00.jpg", true), true);

					}
				});
				builder2.setPositiveButton("重设验证", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						isRegisterFirst = true;
						isRegisterSecond = false;
						fingerPassword = "";
						if (drawView.stickerBitmapList != null) {
							drawView.stickerBitmapList.deleteOnTouchStickerBitmap();
						}
						drawView.cleanPaintBitmap();
						drawView.setBackgroundBitmap(DrawAttribute.getImageFromAssetsFile(getApplicationContext(), "bigpaper00.jpg", true), true);
						FingerPassword.setBack(0);
						FingerPassword.setForward(0);
						FingerPassword.setPic(0);
						FingerPassword.setRotate(0f);
						FingerPassword.setScale(1f);
					}
				});
			}
			catch (Exception e)
			{
				showTip(e.toString());
			}
			/** 点击保存按钮 */
			saveButton.setOnClickListener(new Button.OnClickListener()
			{
				public void onClick(View v)
				{
					if(isChoose)
					{
						if (isRegisterFirst == true) {
							fingerPassword = FingerPassword.getPassword();
							if (drawView.stickerBitmapList != null) {
								drawView.stickerBitmapList.deleteOnTouchStickerBitmap();
							}
							drawView.cleanPaintBitmap();
							drawView.setBackgroundBitmap(DrawAttribute.getImageFromAssetsFile(getApplicationContext(), "bigpaper00.jpg", true), true);
							showTip("请重复一次您刚设置的页面效果.");
							FingerPassword.setBack(0);
							FingerPassword.setForward(0);
							FingerPassword.setPic(0);
							FingerPassword.setRotate(0f);
							FingerPassword.setScale(1f);
							isRegisterFirst = false;
							DrawActivity.isChoose=false;
							isRegisterSecond = true;
							flawOperation();
						}
						else if (isRegisterSecond == true)
						{
							if (compare(fingerPassword, FingerPassword.getPassword()))
							{
								user.setFingerPassword(fingerPassword);
								handler.sendEmptyMessage(0);
							}
							else
							{
								try {
									builder2.show();
								}
								catch (Exception e)
								{
									showTip(e.toString());
								}
							}
						} else if (isLogin == true) {
							if (compare(user.getFingerPassword(), FingerPassword.getPassword())) {
								handler.sendEmptyMessage(0);
							} else {
								showTip("手势错误");
							}
						}
					}
					else
					{
						showTip("尚未选择图片，无法提交验证信息");
					}
				}
			});
			/**  点击隐形按钮 */
			visibleButton.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					/** 设置顶部工具栏不可视. */
					setUpAndButtomBarVisible(false);
				}
			});

			builder = new AlertDialog.Builder(this);
			builder.setTitle("是否设置其他形式的登陆验证?");
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handler.sendEmptyMessage(1);
				}
			});
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(DrawActivity.this, RegisterGetPassAndUser.class);
					intent.putExtra("user", user);
					intent.putExtra("TAG", TAG);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
				}
			});
		}
		catch(Exception e)
		{
			Toast.makeText(this,e.toString(),5).show();
		}
	}

	@Override
	public void getMessage(TranObject o)
	{
		switch (o.getType())
		{
			case RegisterSuccess:
				showTip("注册成功");
				Intent intent=new Intent(DrawActivity.this,LoginGetCommand.class);
				intent.putExtra("getCommand",user.getCommand());
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
				break;
			case RegisterFail:
				showTip("注册失败");
		}
	}

	@Override
	public void onClick(View v)
	{
		if(client.getIsConnect())
		{
			switch (v.getId())
			{
				default:
					break;
			}
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("服务器尚未连接,点击确认连接服务器");
			builder.setNegativeButton("取消", null);
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handler.sendEmptyMessageDelayed(100, 1l);
				}
			});
			builder.create().show();
		}
	}

	private void showTip(final String str)
	{
		mToast.setText(str);
		mToast.show();
	}

	private android.os.Handler handler = new android.os.Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					if(function.equals("register")) {
						builder.create().show();
					}
					else if(function.equals("login"))
					{
						Intent intent=new Intent(DrawActivity.this,LoginPassChoose.class);
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					break;
				case 1:
					TranObject<User> tran=new TranObject(TranObjectType.Register);
					tran.setObject(user);
					out.setMsg(tran);
					break;
				case 100:
					client.setIsConnect(client.start());
					if(client.getIsConnect())
					{
						showTip("成功连接服务端");
						in = client.getClientInputThread();
						out = client.getClientOutputThread();
						Intent serviceBefore=new Intent(DrawActivity.this,GetMsgService.class);
						stopService(serviceBefore);
						Intent service = new Intent(DrawActivity.this, GetMsgService.class);
						startService(service);
					}
					else
					{
						showTip("连接服务端失败");
					}
					break;
				default:
					break;
			}
		}
	};

	public void setUpAndButtomBarVisible(boolean isVisible)
	{
		if(isVisible)
		{
			topBar.setVisibility(View.VISIBLE);
			bottomBar.setVisibility(View.VISIBLE);
		}
		else {
			topBar.setVisibility(View.INVISIBLE);
			bottomBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		unbindDrawables(findViewById(R.id.drawroot));

		drawView.freeBitmaps();

		System.gc();
	}

	private void flawOperation()
	{
		if(drawView.basicGeometry!=null) {
			drawView.setBasicGeometry(null);
			DrawActivity.isChoose=false;
		}
	}

	private void unbindDrawables(View view)
	{
		if(view instanceof ViewGroup)
		{
			for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
			{
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}

			/** removeAllViews方法移除所有子View */
			((ViewGroup) view).removeAllViews();
		}
	}

	long exitTime=0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if((System.currentTimeMillis()-exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext(), "再按一次退出手势验证方式设置", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				if("register".equals(function))
				{
					Intent intent = new Intent(DrawActivity.this,RegisterGetPassAndUser.class);
					String [] spank=user.getPasswordType().split("__");
					StringBuffer buffer=new StringBuffer();
					for(String spank2:spank)
					{
						if(!"手势识别".equals(spank2))
						{
							buffer.append(spank2+"__");
						}
					}
					user.setPasswordType(buffer.toString().trim());
					intent.putExtra("user", user);
					intent.putExtra("TAG",TAG);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
				}
				else if ("login".equals(function))
				{
					Intent intent=new Intent(DrawActivity.this,LoginPassChoose.class);

					user.setPasswordType(user.getPasswordType()+"手势识别__");

					intent.putExtra("user", user);

					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();

				}
			}
			return false;
		}
		return true;
	}

	public boolean compare(String one,String two)
	{
		String ones[]=one.split("_");
		String twos[]=two.split("_");
		for(int i=1;i<10;i=i+2)
		{
			if (i == 1||i==3||i==5)
			{
				if(ones[i].equals(twos[i]))
				{

				}
				else
				{
					return false;
				}
			}
			else if(i==7)
			{
				if(Math.abs(Float.valueOf(ones[i])-Float.valueOf(twos[i]))<=30)	/** 设置旋转角度误差在30度之内 */
				{

				}
				else
				{
					return false;
				}
			}
			else if(i==9)
			{
				if(Math.abs(Float.valueOf(ones[i])-Float.valueOf(twos[i]))<=0.5) /** 设置图形大小变化误差：图形最初显示时的长的0.5倍之内 */
				{

				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
}
