package com.tfboss.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.tfboss.login.client.User;
import com.tfboss.login.net.Client;
import com.tfboss.login.net.ClientInputThread;
import com.tfboss.login.net.ClientOutputThread;
import com.tfboss.login.util.TranObject;
import com.tfboss.login.util.TranObjectType;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class LoginFromCommandFriend extends MyActivity implements View.OnClickListener
{
	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private User user;
	private Toast mToast;
	private String inputs2;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginfromcommandfriend);

		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		in=client.getClientInputThread();
		out=client.getClientOutputThread();

		user=(User)getIntent().getSerializableExtra("user");
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		findViewById(R.id.buttonspsp1).setOnClickListener(this);
		findViewById(R.id.buttonspsp2).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void getMessage(TranObject o)
	{
		switch(o.getType())
		{
			case FriendHelpRequestFail:
				showTip("其他Command中的用户未全部上线");
				break;
			case FriendHelpRequestWait:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("其他用户均在线，请输入发送的信息");
				builder.setNegativeButton("取消", null);
				final View v1 = View.inflate(this, R.layout.helprequestinput, null);
				builder.setView(v1);
				final EditText inputs = (EditText)v1.findViewById(R.id.editTextsssss);

				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						inputs2 = inputs.getText().toString().trim();
						handler.sendEmptyMessageDelayed(3, 1l);
					}
				});
				builder.create().show();
				break;
			case FriendHelpFail:
				showTip("有用户不同意您无密码登陆");
				break;
			case FriendHelpSuccess:
				showTip("所有用户均同意登陆");
				Intent intent=new Intent(LoginFromCommandFriend.this,Function.class);
				user.setPasswordType("");
				intent.putExtra("user", user);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
				break;
		}
	}

	@Override
	public void onClick(View v)
	{
		if(client.getIsConnect()) {
			switch (v.getId()) {
				case R.id.buttonspsp1:
					TranObject<User> tran = new TranObject<>(TranObjectType.FriendHelpRequest);
					tran.setObject(user);
					out.setMsg(tran);
					break;
				case R.id.buttonspsp2:
					Intent intents = new Intent(LoginFromCommandFriend.this, LoginPassChoose.class);
					intents.putExtra("user", user);
					startActivity(intents);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
					break;
			}
		}
	}

	private android.os.Handler handler = new android.os.Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 3:
					TranObject<String> transs=new TranObject(TranObjectType.FriendHelpRequest2);
					String ans=new StringBuffer().append(user.getCommand()+"__"+user.getColor() +"__"+inputs2).toString().trim();
					transs.setObject(ans);
					out.setMsg(transs);
					break;
				case 100:
					client.setIsConnect(client.start());
					if(client.getIsConnect())
					{
						showTip("成功连接服务端");
						in = client.getClientInputThread();
						out = client.getClientOutputThread();
						Intent serviceBefore=new Intent(LoginFromCommandFriend.this,GetMsgService.class);
						stopService(serviceBefore);
						Intent service = new Intent(LoginFromCommandFriend.this, GetMsgService.class);
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

	private void showTip(final String str)
	{
		mToast.setText(str);
		mToast.show();
	}

	public AlertDialog.Builder builders;
	public AlertDialog dialogs;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(builders==null) {
				builders = new AlertDialog.Builder(this);
				builders.setTitle("退出好友帮助?");
				builders.setNegativeButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intents = new Intent(LoginFromCommandFriend.this,LoginPassChoose.class);
						intents.putExtra("user",user);
						startActivity(intents);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
				});
				builders.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				dialogs=builders.show();
				builders.create().show();
			}
			else
			{
				dialogs.dismiss();
				dialogs=null;
				builders=null;
			}
		}
		return false;
	}
}