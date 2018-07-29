package com.tfboss.login;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;
import com.tfboss.login.net.Client;
import com.tfboss.login.util.Constants;
import com.tfboss.login.util.FingerPassword;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */


public class MyApplication extends Application
{
	private Client client;
	private static boolean isClientStart;

	@Override
	public void onCreate()
	{
		super.onCreate();

		client = new Client(Constants.Ip,Constants.port);

		SpeechUtility.createUtility(MyApplication.this, "appid=" + getString(R.string.app_id));
	}

	public boolean isClientStart()
	{
		return isClientStart;
	}

	public Client getClient() {
		return client;
	}

	public void setClientStart(boolean isClientStart) {
		this.isClientStart = isClientStart;
	}

}
