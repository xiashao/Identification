package com.tfboss.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.tfboss.login.util.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public abstract class MyActivity extends Activity
{
	private BroadcastReceiver MsgReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			TranObject msg = (TranObject) intent
					.getSerializableExtra(Constants.MSGKEY);
			if (msg != null)
			{
				getMessage(msg);
			}
			else
			{
				close();
			}
		}
	};

	public abstract void getMessage(TranObject msg);

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	public void close()
	{
		finish();
		System.exit(0);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION);
		registerReceiver(MsgReceiver, intentFilter);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		unregisterReceiver(MsgReceiver);
	}
}
