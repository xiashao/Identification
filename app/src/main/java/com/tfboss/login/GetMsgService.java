package com.tfboss.login;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.tfboss.login.net.*;
import com.tfboss.login.util.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */


public class GetMsgService extends Service
{
	private MyApplication application;
	private Client client;

	private boolean isStart = false;

	public GetMsgService()
	{
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		application=(MyApplication) this.getApplicationContext();
		client=application.getClient();

	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);

		isStart = client.getIsConnect();
		application.setClientStart(isStart);

		if (isStart)
		{
			ClientInputThread in = client.getClientInputThread();
			in.setMessageListener
			(
				new MessageListener()
				{
					@Override
					public void Message(TranObject msg)
					{
						Intent broadCast = new Intent();
						broadCast.setAction(Constants.ACTION);
						broadCast.putExtra(Constants.MSGKEY, msg);
						sendBroadcast(broadCast);
					}
				}
			);
		}
	}
}
