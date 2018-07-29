package com.tfboss.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import com.tfboss.login.net.*;
import com.tfboss.login.util.DrawAttribute;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class WelcomeActivity extends Activity
{
	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private Context context;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		DrawAttribute.screenWidth = dm.widthPixels;
		DrawAttribute.screenHeight = dm.heightPixels;
		DrawAttribute.paint.setColor(Color.LTGRAY);
		DrawAttribute.paint.setStrokeWidth(3);

		application=(MyApplication)getApplicationContext();
		client=application.getClient();

		context=this;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		handler.sendEmptyMessageDelayed(2,1000l);
	}

	private android.os.Handler handler = new android.os.Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 2:
					if(!client.getIsConnect())
					{
						client.setIsConnect(client.start());
						if(client.getIsConnect())
						{
							Toast.makeText(context, "成功连接服务端", Toast.LENGTH_SHORT).show();
							in = client.getClientInputThread();
							out = client.getClientOutputThread();
							Intent service = new Intent(WelcomeActivity.this, GetMsgService.class);
							startService(service);
							handler.sendEmptyMessage(1);
						}
						else
						{
							Toast.makeText(context, "连接服务端失败", Toast.LENGTH_SHORT).show();
							handler.sendEmptyMessage(1);
						}
					}
					break;
				case 1:
					Intent intent2=new Intent(WelcomeActivity.this,LoginGetCommand.class);
					startActivity(intent2);
					finish();
					break;
				default:
					break;
			}
		}
	};
}