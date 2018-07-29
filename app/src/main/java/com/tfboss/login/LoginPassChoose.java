package com.tfboss.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.gc.materialdesign.views.ButtonRectangle;
import com.tfboss.login.client.User;
import com.tfboss.login.net.Client;
import com.tfboss.login.net.ClientInputThread;
import com.tfboss.login.net.ClientOutputThread;
import com.tfboss.login.util.TranObject;
import com.tfboss.login.util.TranObjectType;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class LoginPassChoose extends MyActivity implements View.OnClickListener
{
	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private Context context;

	private EditText editText;
	private ButtonRectangle button;

	private String passwordType;
	private StringBuffer passwordBuffer;
	private String[] type;
	private String chooseType;

	private PassAdapter adapter;
	private User user;
	private Toast mToast;

	private ButtonRectangle button2;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		context=this;
		setContentView(R.layout.loginpasschoose);
		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		in=client.getClientInputThread();
		out=client.getClientOutputThread();

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		user=(User)getIntent().getSerializableExtra("user");
		passwordType=user.getPasswordType();
		if(passwordType==null)
		{
			passwordType="";
		}
		passwordBuffer=new StringBuffer();
		type=passwordType.split("__");
		passWay=new ArrayList();
		for(String ans:type)
		{
			passWay.add(ans);
		}
		adapter =new PassAdapter();

		editText=(EditText)findViewById(R.id.LoginNameEdit);
		if(passWay.size()==1&&type[0].equals(""))
		{
			editText.setText(user.getName());
			showTip("成功获取用户名:"+user.getName());
		}
		editText.setEnabled(false);

		button=(ButtonRectangle)findViewById(R.id.LoginToGetName);
		button.setOnClickListener(this);

		button2=(ButtonRectangle)findViewById(R.id.buttonLoginSpank);
		button2.setOnClickListener(this);

		findViewById(R.id.spankFriend).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(!type[0].equals(""))
		{
			if (passWay.size() == 3) {
				builder.setTitle("开始验证 请选择密码方式:");
			} else {
				builder.setTitle("您还需完成" + passWay.size() + "种验证方式 请选择");
			}
			final View v1 = View.inflate(this, R.layout.passwaychoose, null);
			builder.setView(v1);
			final Spinner spinner = (Spinner) v1.findViewById(R.id.spinner);
			spinner.setAdapter(adapter);
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chooseType = passWay.get(spinner.getSelectedItemPosition());
					for (String ans : type) {
						if (!chooseType.equals(ans))
							passwordBuffer.append(ans + "__");
					}
					passwordType = passwordBuffer.toString().trim();
					user.setPasswordType(passwordType);
					handler.sendEmptyMessage(0);
				}
			});
			builder.setNegativeButton("好友帮助", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handler.sendEmptyMessageDelayed(66,500l);
				}
			});
			builder.create().show();
		}
	}

	@Override
	public void getMessage(TranObject o)
	{

	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.spankFriend)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("申请好友帮助");
			final View v1 = View.inflate(this, R.layout.friendexplain, null);
			builder.setView(v1);
			final TextView textView = (TextView) v1.findViewById(R.id.textViews);
			textView.setText("当您的验证方式因为意外事件无法执行的时候，\n" +
							 "您可以请求好友帮助来进行好友验证登陆。\n" +
			   				 "解释："+"每一个口令中最多有5个用户，\n" +
							 "如果当前口令中不止您一个用户,且其他口令中的用户都都处于登陆状态:\n"+
							 "您可以发送一条信息给其他所有用户.\n"+
							 "如果你口令中的好友都同意您无密码登陆.您就可以直接登陆.");
			builder.setPositiveButton("执行", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					handler.sendEmptyMessageAtTime(2,200l);
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.create().show();

			return;
		}
		if(client.getIsConnect())
		{
			switch (v.getId())
			{
				case R.id.LoginToGetName:
					if(!type[0].equals(""))
					{
						MaterialDialog builder = new MaterialDialog(this);
						if (passWay.size() == 3)
						{
							builder.setTitle("开始验证 请选择密码方式:");
						}
						else
						{
							builder.setTitle("您还需完成" + passWay.size() + "种验证方式 请选择");
						}
						final View v1 = View.inflate(this, R.layout.passwaychoose, null);
						builder.setView(v1);
						final Spinner spinner = (Spinner) v1.findViewById(R.id.spinner);
						spinner.setAdapter(adapter);
						builder.setPositiveButton("确认",  new View.OnClickListener()  {
									@Override
									public void onClick(View v) {
										chooseType = passWay.get(spinner.getSelectedItemPosition());
										for (String ans : type) {
											if (!chooseType.equals(ans))
												passwordBuffer.append(ans + "__");
										}
										passwordType = passwordBuffer.toString().trim();
										user.setPasswordType(passwordType);
										handler.sendEmptyMessage(0);
									}
								}
						);
						builder.setNegativeButton("好友帮助",  new View.OnClickListener()  {
							@Override
							public void onClick(View v)
							{
								handler.sendEmptyMessageDelayed(66,500l);
							}
						});
						builder.show();
					}
					else
					{
						showTip("您已经获取用户名.");
					}
					break;
				case R.id.buttonLoginSpank:

					if(type[0].equals(""))
					{
						TranObject<User> trans=new TranObject<>(TranObjectType.LoginIn);
						trans.setObject(user);
						out.setMsg(trans);
						Intent intent=new Intent(LoginPassChoose.this,Function.class);
						user.setPasswordType("");
						intent.putExtra("user", user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else{
						showTip("请获取用户名");
					}

					break;
				default:
					break;
			}
		} else
		{
			showTip("服务器尚未连接!");
		}
	}

	ArrayList<String> passWay;
	class PassAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return passWay.size();
		}

		@Override
		public Object getItem(int position) {
			return passWay.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv;
			if(convertView==null){
				tv = new TextView(getApplicationContext());
			}else{
				tv = (TextView)convertView;
			}
			tv.setText(passWay.get(position));
			return tv;
		}
	}

	private android.os.Handler handler = new android.os.Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					if(chooseType.equals("声纹识别"))
					{
						Intent intent =new Intent(LoginPassChoose.this,SoundLoginActivity.class);
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else if(chooseType.equals("人脸识别"))
					{
						Intent intent =new Intent(LoginPassChoose.this,FaceLoginActivity.class);
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else if(chooseType.equals("手势识别"))
					{
						Intent intent=new Intent(LoginPassChoose.this,DrawActivity.class);
						intent.putExtra("function","login");
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					break;
				case 2:	/** 好友帮助 */
					Intent intents2=new Intent(LoginPassChoose.this,LoginFromCommandFriend.class);
					intents2.putExtra("user",user);
					startActivity(intents2);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
					break;
				case 66:
					AlertDialog.Builder builderss = new AlertDialog.Builder(context);
					builderss.setTitle("申请好友帮助");
					final View v1 = View.inflate(context, R.layout.friendexplain, null);
					builderss.setView(v1);
					final TextView textView = (TextView) v1.findViewById(R.id.textViews);
					textView.setText("当您的验证方式因为意外事件无法执行的时候，\n" +
							"您可以请求好友帮助来进行好友验证登陆。\n" +
							"解释："+"每一个口令中最多有5个用户，\n" +
							"如果当前口令中不止您一个用户,且其他口令中的用户都都处于登陆状态:\n"+
							"您可以发送一条信息给其他所有用户.\n"+
							"如果你口令中的好友都同意您无密码登陆.您就可以直接登陆.");
					builderss.setPositiveButton("执行", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							handler.sendEmptyMessageAtTime(2,200l);
						}
					});
					builderss.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					builderss.create().show();
					break;
				case 100:
						client.setIsConnect(client.start());
						if(client.getIsConnect())
						{
							showTip("成功连接服务端");
							in = client.getClientInputThread();
							out = client.getClientOutputThread();
							Intent serviceBefore=new Intent(LoginPassChoose.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(LoginPassChoose.this, GetMsgService.class);
							startService(service);
						}
						else
						{
							showTip("连接服务端失败");
						}
					break;
				case 20:
					dialogs=null;
					builders=null;
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

	public MaterialDialog builders;
	public MaterialDialog dialogs;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(builders==null) {
				builders = new MaterialDialog(this);
				builders.setTitle("返回登陆主页面?");
				builders.setNegativeButton("确认",  new View.OnClickListener()  {
					@Override
					public void onClick(View v) {
						Intent intents = new Intent(LoginPassChoose.this,LoginGetCommand.class);
						intents.putExtra("getCommand",user.getCommand());
						startActivity(intents);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
				});
				builders.setPositiveButton("取消", new View.OnClickListener()  {
					@Override
					public void onClick(View v) {
						handler.sendEmptyMessageDelayed(20,100l);
						builders.dismiss();
					}
				});
				builders.show();
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