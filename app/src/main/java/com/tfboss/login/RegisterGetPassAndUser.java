package com.tfboss.login;

import android.app.AlertDialog;
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

public class RegisterGetPassAndUser extends MyActivity implements View.OnClickListener
{
	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private Toast mToast;
	private AlertDialog.Builder builder;
	private EditText editText;
	private ButtonRectangle button;

	private String passwordType;
	private StringBuffer passwordBuffer;
	private String[] type;
	private String chooseType;

	private PassAdapter adapter;
	private User user;
	private String TAG;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.registergetuserandpass);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		in=client.getClientInputThread();
		out=client.getClientOutputThread();

		user=(User)getIntent().getSerializableExtra("user");
		TAG=(String)getIntent().getSerializableExtra("TAG");
		passwordType=user.getPasswordType();
		if(passwordType==null)
		{
			passwordType="";
		}
		passwordBuffer=new StringBuffer();
		passwordBuffer.append(passwordType.toString().trim());
		editText=(EditText)findViewById(R.id.RegisterGetUser);

		if((!("".equals(user.getName())))&&(user.getName()!=null))
		{
			editText.setText(user.getName());
		}

		button=(ButtonRectangle)findViewById(R.id.RegisterToGetPass);
		button.setOnClickListener(this);

		passWay=new ArrayList();
		passWay.add("声纹识别");
		passWay.add("人脸识别");
		passWay.add("手势识别");

		if((!("".equals(passwordType)))&&(passwordType!=null))
		{
			type=passwordType.split("__");
			for(String ans:type)
			{
				if(ans.equals("声纹识别"))
				{
					passWay.remove("声纹识别");
				}
				else if(ans.equals("人脸识别"))
				{
					passWay.remove("人脸识别");
				}
				else if(ans.equals("手势识别"))
				{
					passWay.remove("手势识别");
				}
			}
		}
		adapter =new PassAdapter();

		findViewById(R.id.getbackregister).setOnClickListener(this);
		findViewById(R.id.buttondirect).setOnClickListener(this);

		if(passWay.size()<3)
		{
			editText.setEnabled(false);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if(passWay.size()!=3&&type!=null&&(!type[0].equals("")))
		{
			user.setName(editText.getText().toString().trim());
			builder = new AlertDialog.Builder(this);
			builder.setTitle("请再选择一种身份验证方式");
			builder.setNegativeButton("取消", null);
			final View v1 = View.inflate(this, R.layout.passwaychoose, null);
			builder.setView(v1);
			final Spinner spinner = (Spinner) v1.findViewById(R.id.spinner);
			spinner.setAdapter(adapter);
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chooseType = passWay.get(spinner.getSelectedItemPosition());
					passwordBuffer.append(chooseType + "__");
					passwordType = passwordBuffer.toString().trim();
					user.setPasswordType(passwordType);
					handler.sendEmptyMessage(0);
				}
			});
			builder.create().show();
		}
	}

	@Override
	public void getMessage(TranObject o)
	{
		switch (o.getType())
		{
			case RegisterSuccess:
				showTip("注册成功");
				Intent intent=new Intent(RegisterGetPassAndUser.this,LoginGetCommand.class);
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
				case R.id.buttondirect:
					if(passWay.size()==3)
					{
						showTip("您尚未设置身份验证方式");
					}
					else {
						TranObject<User> tran = new TranObject(TranObjectType.Register);
						tran.setObject(user);
						out.setMsg(tran);
					}
					break;
				case R.id.getbackregister:

					AlertDialog.Builder buidlers = new AlertDialog.Builder(this);
					buidlers.setTitle("确定不在该口令中注册用户?");
					buidlers.setNegativeButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							handler.sendEmptyMessage(93);
						}
					});
					buidlers.setPositiveButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					buidlers.create().show();

					break;
				case R.id.RegisterToGetPass:
					if(type!=null&&(type[0].equals("")))
					{
						showTip("您已经完成所有种类身份验证的设置.");
					}
					else if (editText.getText().toString().trim().equals(""))
					{
						showTip("用户名不能为空");
					}
					else
					{
						user.setName(editText.getText().toString().trim());
						builder = new AlertDialog.Builder(this);
						if(passWay.size()!=3)
						{
							builder.setTitle("请再选择一种身份验证方式");
						}
						else
						{
							builder.setTitle("请选择身份验证方式");
						}
						builder.setNegativeButton("取消", null);
						final View v1 = View.inflate(this, R.layout.passwaychoose, null);
						builder.setView(v1);
						final Spinner spinner = (Spinner) v1.findViewById(R.id.spinner);
						spinner.setAdapter(adapter);
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								chooseType = passWay.get(spinner.getSelectedItemPosition());
								passwordBuffer.append(chooseType + "__");
								passwordType = passwordBuffer.toString().trim();
								user.setPasswordType(passwordType);
								handler.sendEmptyMessage(0);
							}
						});
						builder.create().show();
					}
					break;
			}
		}
		else
		{
			final MaterialDialog builder = new MaterialDialog(this);
			builder.setTitle("服务器尚未连接,点击确认连接服务器");
			builder.setNegativeButton("取消",
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							builder.dismiss();
						}
					});
			builder.setPositiveButton("确认", new View.OnClickListener()  {
				@Override
				public void onClick(View v) {
					handler.sendEmptyMessageDelayed(100, 1l);
					builder.dismiss();
				}
			});
			builder.show();
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
						Intent intent =new Intent(RegisterGetPassAndUser.this,SoundRegisterActivity.class);
						intent.putExtra("user",user);
						intent.putExtra("TAG",TAG);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else if(chooseType.equals("人脸识别"))
					{
						Intent intent =new Intent(RegisterGetPassAndUser.this,FaceRegisterActivity.class);
						intent.putExtra("user",user);
						intent.putExtra("TAG", TAG);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else if(chooseType.equals("手势识别"))
					{
						Intent intent =new Intent(RegisterGetPassAndUser.this,DrawActivity.class);
						intent.putExtra("user",user);
						intent.putExtra("TAG", TAG);
						intent.putExtra("function","register");
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					break;
				case 93:
					if(TAG!=null&&"RegisterCreateCommandActivity".equals(TAG)) {
						TranObject<User> tran = new TranObject(TranObjectType.CommandCreatedFail);
						tran.setObject(user);
						out.setMsg(tran);
						Intent intent = new Intent(RegisterGetPassAndUser.this,RegisterCreateCommandActivity.class);
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else if(TAG!=null&&"RegisterAddCommandActivity".equals(TAG))
					{
						TranObject<User> tran = new TranObject(TranObjectType.CommandCreatedFail);
						tran.setObject(user);
						out.setMsg(tran);
						Intent intent = new Intent(RegisterGetPassAndUser.this,RegisterAddCommandActivity.class);
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					break;
				case 100:
						client.setIsConnect(client.start());
						if(client.getIsConnect())
						{
							showTip("成功连接服务端");
							in = client.getClientInputThread();
							out = client.getClientOutputThread();
							Intent serviceBefore=new Intent(RegisterGetPassAndUser.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(RegisterGetPassAndUser.this, GetMsgService.class);
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

	Integer i;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			i=0;
			final MaterialDialog builders = new MaterialDialog(this);
			try {
				i=1;
				builders.setTitle("确定不在该口令中注册用户?");
				i=2;
				builders.setNegativeButton("确认", new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						i=3;
						if (TAG != null && "RegisterCreateCommandActivity".equals(TAG))
						{
							i=4;
							TranObject<User> tran = new TranObject(TranObjectType.CommandCreatedFail);
							i=5;
							tran.setObject(user);
							i=6;
							out.setMsg(tran);
							i=7;
							Intent intent = new Intent(RegisterGetPassAndUser.this, RegisterCreateCommandActivity.class);
							i=8;
							intent.putExtra("user", user);
							i=9;
							startActivity(intent);
							i=10;
							overridePendingTransition(R.anim.push_left_in,
									R.anim.push_left_out);
							i=11;
							finish();
						}
						else if (TAG != null && "RegisterAddCommandActivity".equals(TAG))
						{
							i=12;
							TranObject<User> tran = new TranObject(TranObjectType.CommandCreatedFail);
							i=13;
							tran.setObject(user);
							i=14;
							out.setMsg(tran);
							i=15;
							Intent intent = new Intent(RegisterGetPassAndUser.this, RegisterAddCommandActivity.class);
							i=16;
							intent.putExtra("user", user);
							i=17;
							startActivity(intent);
							i=18;
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							i=19;
							finish();
						}
					}
				});
				builders.setPositiveButton("取消", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						builders.dismiss();
					}
				});
				builders.show();
			}
			catch (Exception e)
			{
				Toast.makeText(this,e.toString()+" "+i,1).show();
			}
			return false;
		}
		return false;
	}
}