package com.tfboss.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class Function extends MyActivity implements View.OnClickListener
{
	private User user;
	String command;
	int color;
	String name;

	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private ButtonRectangle button1,button2,button3,button4;

	private String type[];
	private String passType;

	private String[] allPassWay;

	private Toast mToast;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.function);

		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		in=client.getClientInputThread();
		out=client.getClientOutputThread();

		user=(User)getIntent().getSerializableExtra("user");
		command=user.getCommand();
		color=user.getColor();
		name=user.getName();

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		button1=(ButtonRectangle)findViewById(R.id.buttonspankingss1);
		button2=(ButtonRectangle)findViewById(R.id.buttonspankingss2);
		button3=(ButtonRectangle)findViewById(R.id.buttonspankingss3);
		button4=(ButtonRectangle)findViewById(R.id.buttonspankingss4);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);

		allPassWay=new String[3];
		allPassWay[0]="声纹识别";
		allPassWay[1]="人脸识别";
		allPassWay[2]="手势识别";

		showTip("登陆成功！");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	String chooseType;
	@Override
	public void getMessage(TranObject o)
	{
		switch(o.getType())
		{
			case AddPassWay:
				User u1=(User)o.getObject();
				passType=u1.getPasswordType();
				type=passType.split("__");
				addPassWay=new ArrayList();
				spank1:
				for(String passOne:allPassWay)
				{
					for(String passTwo:type)
					{
						if(passOne.equals(passTwo))
						{
							continue spank1;
						}
					}
					addPassWay.add(passOne);
				}
				addAdapter=new AddPassAdapter();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("请选择要添加的密码方式");
				builder.setNegativeButton("取消", null);
				final View v1 = View.inflate(this, R.layout.passwaychoose, null);
				builder.setView(v1);
				final Spinner spinner = (Spinner) v1.findViewById(R.id.spinner);
				spinner.setAdapter(addAdapter);
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						chooseType=addPassWay.get(spinner.getSelectedItemPosition());
						handler.sendEmptyMessage(0);
					}
				});
				builder.create().show();
				break;
			case UpdatePassWay:
				User u2=(User)o.getObject();
				passType=u2.getPasswordType();
				type=passType.split("__");
				updatePassWay=new ArrayList();
				for(String passOne:allPassWay)
				{
					for(String passTwo:type)
					{
						if(passOne.equals(passTwo))
						{
							updatePassWay.add(passOne);
							break;
						}
					}
				}
				updateAdapter=new UpdatePassAdapter();
				AlertDialog.Builder builderss = new AlertDialog.Builder(this);
				builderss.setTitle("请选择要修改的密码方式");
				builderss.setNegativeButton("取消", null);
				final View v1ss = View.inflate(this, R.layout.passwaychoose, null);
				builderss.setView(v1ss);
				final Spinner spinners = (Spinner) v1ss.findViewById(R.id.spinner);
				spinners.setAdapter(updateAdapter);
				builderss.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						chooseType=updatePassWay.get(spinners.getSelectedItemPosition());
					}
				});
				builderss.create().show();
				break;
			case DeletePassWay:
				User u3=(User)o.getObject();
				passType=u3.getPasswordType();
				type=passType.split("__");
				deletePassWay=new ArrayList();
				for(String passOne:allPassWay)
				{
					for(String passTwo:type)
					{
						if(passOne.equals(passTwo))
						{
							deletePassWay.add(passOne);
							break;
						}
					}
				}
				deleteAdapter=new DeletePassAdapter();
				AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
				builder2.setTitle("请选择要删除的密码方式");
				builder2.setNegativeButton("取消", null);
				final View v2 = View.inflate(this, R.layout.passwaychoose, null);
				builder2.setView(v2);
				final Spinner spinner2 = (Spinner) v2.findViewById(R.id.spinner);
				spinner2.setAdapter(deleteAdapter);
				builder2.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						chooseType = deletePassWay.get(spinner2.getSelectedItemPosition());
						handler.sendEmptyMessage(1);
					}
				});
				builder2.create().show();
				break;
			case AlterPassWayFail:
				Toast.makeText(this,"发生不明错误",1).show();
				break;
			case DeleteFacePass:
				Toast.makeText(this,"删除人脸密码成功",1).show();
				break;
			case DeleteSoundPass:
				Toast.makeText(this,"删除声纹密码成功",1).show();
				break;
			case DeletePassFail:
				Toast.makeText(this,"剩余一种密码，不能执行删除",1).show();
				break;
			case FriendHelpRequest2:
				String message=(String)o.getObject();
				String[] messages=message.split("__");
				AlertDialog.Builder builders = new AlertDialog.Builder(this);
				builders.setTitle(messages[0] + "口令的第" + messages[1] + "种颜色的用户发来登陆帮助的请求信息");
				final View v1s = View.inflate(this, R.layout.helprequestinput, null);
				builders.setView(v1s);
				final EditText inputs = (EditText)v1s.findViewById(R.id.editTextsssss);
				inputs.setText(messages[2]);
				builders.setPositiveButton("同意对方无密码登陆", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessageDelayed(98, 1l);
					}
				});
				builders.setNegativeButton("不同意对方无密码登陆", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessageDelayed(99, 1l);
					}
				});
				builders.create().show();
				break;
			case LoginOutS:
				Intent intents1s=new Intent(Function.this,LoginGetCommand.class);
				intents1s.putExtra("getCommand",user.getCommand());
				startActivity(intents1s);
				overridePendingTransition(R.anim.my_scale_action,
						R.anim.my_alpha_action);
				finish();
				break;
		}
	}

	@Override
	public void onClick(View v)
	{
		if(client.getIsConnect()) {
			switch (v.getId())
			{
				case R.id.buttonspankingss1:
					TranObject<User> tran1 = new TranObject<>(TranObjectType.DeletePassWay);
					User user = new User();
					user.setCommand(command);
					user.setName(name);
					tran1.setObject(user);
					out.setMsg(tran1);
					break;
				case R.id.buttonspankingss2:
					TranObject<User> tran2 = new TranObject<>(TranObjectType.UpdatePassWay);
					User user2 = new User();
					user2.setCommand(command);
					user2.setName(name);
					tran2.setObject(user2);
					out.setMsg(tran2);
					break;
				case R.id.buttonspankingss3:
					TranObject<User> tran3 = new TranObject<>(TranObjectType.AddPassWay);
					User user3 = new User();
					user3.setCommand(command);
					user3.setName(name);
					tran3.setObject(user3);
					out.setMsg(tran3);
					break;
				case R.id.buttonspankingss4:
					TranObject<User> tran=new TranObject(TranObjectType.LoginOut);
					tran.setObject(this.user);
					out.setMsg(tran);
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

	ArrayList<String> addPassWay;
	AddPassAdapter addAdapter;
	class AddPassAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return addPassWay.size();
		}

		@Override
		public Object getItem(int position) {
			return addPassWay.get(position);
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
			tv.setText(addPassWay.get(position));
			return tv;
		}
	}

	ArrayList<String> updatePassWay;
	UpdatePassAdapter updateAdapter;
	class UpdatePassAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return updatePassWay.size();
		}

		@Override
		public Object getItem(int position) {
			return updatePassWay.get(position);
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
			tv.setText(updatePassWay.get(position));
			return tv;
		}
	}

	ArrayList<String> deletePassWay;
	DeletePassAdapter deleteAdapter;
	class DeletePassAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return deletePassWay.size();
		}

		@Override
		public Object getItem(int position) {
			return deletePassWay.get(position);
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
			tv.setText(deletePassWay.get(position));
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
					if(("声纹识别").equals(chooseType))
					{
						Intent intent=new Intent(Function.this,AddPassSoundActivity.class);
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else if("人脸识别".equals(chooseType))
					{
						Intent intent=new Intent(Function.this,AddPassFaceActivity.class);
						intent.putExtra("user",user);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						finish();
					}
					else if("手势识别".equals(chooseType))
					{

					}
					break;
				case 1:
					if("声纹识别".equals(chooseType))
					{
						TranObject<User> trans=new TranObject<>(TranObjectType.DeleteSoundPass);
						trans.setObject(user);
						out.setMsg(trans);
					}
					else if("人脸识别".equals(chooseType))
					{
						TranObject<User> trans=new TranObject<>(TranObjectType.DeleteFacePass);
						trans.setObject(user);
						out.setMsg(trans);
					}
					else if("手势识别".equals(chooseType))
					{

					}
					break;
				case 98:
					TranObject<User> TranObjec=new TranObject<>(TranObjectType.FriendHelpResponse);
					out.setMsg(TranObjec);
					break;
				case 99:
					TranObject<User> TranObjec2=new TranObject<>(TranObjectType.FriendHelpResponseRefuse);
					out.setMsg(TranObjec2);
					break;
				case 100:
						client.setIsConnect(client.start());
						if(client.getIsConnect())
						{
							showTip("成功连接服务端");
							in = client.getClientInputThread();
							out = client.getClientOutputThread();
							Intent serviceBefore=new Intent(Function.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(Function.this, GetMsgService.class);
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

	long exitTime=0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if((System.currentTimeMillis()-exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext(), "再按一次退出登陆", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				TranObject<User> tran=new TranObject(TranObjectType.LoginOut);
				tran.setObject(this.user);
				out.setMsg(tran);
			}
			return false;
		}
		return true;
	}
}