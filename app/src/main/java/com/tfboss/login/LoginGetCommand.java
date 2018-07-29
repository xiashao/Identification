package com.tfboss.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.tfboss.login.client.Command;
import com.tfboss.login.client.User;
import com.tfboss.login.net.Client;
import com.tfboss.login.net.ClientInputThread;
import com.tfboss.login.net.ClientOutputThread;
import com.tfboss.login.util.Constants;
import com.tfboss.login.util.JsonParser;
import com.tfboss.login.util.TranObject;
import com.tfboss.login.util.TranObjectType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import me.drakeet.materialdialog.MaterialDialog;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class LoginGetCommand extends MyActivity implements View.OnClickListener
{
	private static String TAG = LoginGetCommand.class.getSimpleName();

	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private EditText editText;
	private ImageButton button1;
	private ButtonRectangle button3;
	private ButtonFlat button4;

	private SpeechRecognizer mIat;

	private RecognizerDialog mIatDialog;

	private HashMap<String, String> mIatResults = new LinkedHashMap();

	private EditText mResultText;
	private Toast mToast;

	private String mEngineType = SpeechConstant.TYPE_CLOUD;

	private ColorAdapter adapter;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.logingetcommand);
		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		if(client.getIsConnect()) {
			in = client.getClientInputThread();
			out = client.getClientOutputThread();
		}

		editText=(EditText)findViewById(R.id.iat_text2);
		button1=(ImageButton)findViewById(R.id.iat_recognize2);
		button3=(ButtonRectangle)findViewById(R.id.iat_cancel2);
		button4=(ButtonFlat)findViewById(R.id.spankings);
		button1.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editText.setText("");
			}
		});

		mIat = SpeechRecognizer.createRecognizer(LoginGetCommand.this, mInitListener);

		mIatDialog = new RecognizerDialog(LoginGetCommand.this, mInitListener);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mResultText = ((EditText) findViewById(R.id.iat_text2));

		color=new ArrayList();
		for(int i=1;i<=5;i++)
		{
			color.add(new Integer(i));
		}

		adapter=new ColorAdapter();

		String getCommand=(String)getIntent().getSerializableExtra("getCommand");

		if(getCommand!=null&&(!"".equals(getCommand)))
		{
			editText.setHint("最近使用口令:"+getCommand);
		}
	}

	private User user;

	@Override
	public void getMessage(TranObject o)
	{
		switch (o.getType())
		{
			case GetCommandFail:
				Toast.makeText(this,"口令或者颜色错误",1).show();
				break;
			case GetCommandSuccessful:
				Toast.makeText(this,"口令与颜色正确",1).show();
				user=(User)o.getObject();
				handler.sendEmptyMessageDelayed(1, 1l);
				break;
			case EndUse:

				in.setStart(false);
				out.setStart(false);
				Intent intent=new Intent(LoginGetCommand.this,GetMsgService.class);
				this.getApplicationContext().stopService(intent);

				Intent broadCast = new Intent();
				broadCast.setAction(Constants.ACTION);
				sendBroadcast(broadCast);

				break;
		}
	}

	Integer colorcharacter=0;
	int ret = 0;

	@Override
	public void onClick(View v)
	{
		if(client.getIsConnect()) {
			switch (v.getId()) {

				case R.id.spankings:
					handler.sendEmptyMessage(2);
					break;

				case R.id.iat_recognize2:
					FlowerCollector.onEvent(LoginGetCommand.this, "iat_recognize");

					mResultText.setText(null);
					mIatResults.clear();

					setParam();

					boolean isShowDialog = true;

					if (isShowDialog)
					{
						mIatDialog.setListener(mRecognizerDialogListener);
						mIatDialog.show();

						showTip(getString(R.string.text_begin));
					}
					else
					{
						ret = mIat.startListening(mRecognizerListener);

						if (ret != ErrorCode.SUCCESS)
						{
							showTip("听写失败,错误码：" + ret);
						}
						else
						{
							showTip(getString(R.string.text_begin));
						}
					}
					break;
				case R.id.iat_cancel2:

					if(editText.getText().toString().trim().equals(""))
					{
						Toast.makeText(this,"输入框不能为空",1).show();
					}
					else {
						/** 弹出窗口 */
						final MaterialDialog builder = new MaterialDialog(this);
						builder.setTitle("请选择自己的代表色");
						builder.setNegativeButton("取消",
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										builder.dismiss();
									}
								});
						final View v1 = View.inflate(this, R.layout.colorcharacter, null);
						builder.setView(v1);
						final ListView view = (ListView) v1.findViewById(R.id.listView);
						final TextView text=(TextView)v1.findViewById(R.id.textViews);
						view.setAdapter(adapter);
						view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								colorcharacter=position+1;
								if(colorcharacter==1)
								{
									text.setText("您选择代表色的蓝色");
								}
								else if(colorcharacter==2)
								{
									text.setText("您选择代表色的黄色");
								}
								else if(colorcharacter==3)
								{
									text.setText("您选择代表色的白色");
								}
								else if(colorcharacter==4)
								{
									text.setText("您选择代表色的红色");
								}
								else if(colorcharacter==5)
								{
									text.setText("您选择代表色的黑色");
								}
							}
						}
						);
						builder.setPositiveButton("确定", new View.OnClickListener()  {
							@Override
							public void onClick(View v) {
								if(colorcharacter==0)
								{
									handler.sendEmptyMessage(0);
									builder.dismiss();
								}
								else {
									TranObject<Command> tran = new TranObject(TranObjectType.GetCommand);
									Command command = new Command();
									command.setCommand(editText.getText().toString().trim());
									switch (colorcharacter) {
										case 1:
											command.setColor1(1);
											break;
										case 2:
											command.setColor2(1);
											break;
										case 3:
											command.setColor3(1);
											break;
										case 4:
											command.setColor4(1);
											break;
										case 5:
											command.setColor5(1);
											break;
									}
									tran.setObject(command);
									out.setMsg(tran);
									builder.dismiss();
								}
							}
						});
						builder.show();    /** 显示弹出框架 */
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

	ArrayList<Integer> color=null;

	class ColorAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return color.size();
		}

		@Override
		public Object getItem(int position) {
			return color.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView tv;
			if (convertView == null) {
				tv = new ImageView(getApplicationContext());
			}else{
				tv = (ImageView)convertView;
			}
			Integer t = color.get(position);
			if(t==1)
			{
				tv.setImageResource(R.drawable.blue);
			}
			else if(t==2)
			{
				tv.setImageResource(R.drawable.yellow);
			}
			else if(t==3)
			{
				tv.setImageResource(R.drawable.white);
			}
			else if(t==4)
			{
				tv.setImageResource(R.drawable.red);
			}
			else if(t==5)
			{
				tv.setImageResource(R.drawable.black);
			}
			return tv;
		}
	}

	private InitListener mInitListener = new InitListener()
	{
		@Override
		public void onInit(int code)
		{
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};

	private RecognizerListener mRecognizerListener = new RecognizerListener()
	{
		@Override
		public void onBeginOfSpeech()
		{
			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error)
		{
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast)
		{
			printResult(results);

			if (isLast) {
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	private void printResult(RecognizerResult results)
	{
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;

		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

		mResultText.setText(resultBuffer.toString());
		mResultText.setSelection(mResultText.length());
	}

	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener()
	{
		public void onResult(RecognizerResult results, boolean isLast)
		{
			printResult(results);
		}

		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	public void setParam()
	{
		mIat.setParameter(SpeechConstant.PARAMS, null);

		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = "mandarin";
		if (lag.equals("en_us"))
		{
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		}
		else
		{
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}

		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

		mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

		mIat.setParameter(SpeechConstant.ASR_PTT, "1");

		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIat.cancel();
		mIat.destroy();
	}

	@Override
	protected void onResume() {
		FlowerCollector.onResume(LoginGetCommand.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(LoginGetCommand.this);
		super.onPause();
	}

	private android.os.Handler handler = new android.os.Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					break;
				case 1:
					Intent intent =new Intent(LoginGetCommand.this,LoginPassChoose.class);
					intent.putExtra("user", user);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
					break;
				case 2:
					Intent intent2=new Intent(LoginGetCommand.this,RegisterActivity.class);
					startActivity(intent2);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
					break;
				case 100:
					client.setIsConnect(client.start());
					if(client.getIsConnect())
					{
						showTip("成功连接服务端");
						in = client.getClientInputThread();
						out = client.getClientOutputThread();
						Intent serviceBefore=new Intent(LoginGetCommand.this,GetMsgService.class);
						stopService(serviceBefore);
						Intent service = new Intent(LoginGetCommand.this, GetMsgService.class);
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

	long exitTime=0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if((System.currentTimeMillis()-exitTime) > 2000)
			{
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				if(client.getIsConnect())
				{
					TranObject<User> tran = new TranObject<>(TranObjectType.EndUse);
					out.setMsg(tran);
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("服务器尚未连接,是否连接服务器?");
					builder.setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(in!=null)
								in.setStart(false);
							if(out!=null)
								out.setStart(false);
							Intent intent=new Intent(LoginGetCommand.this,GetMsgService.class);
							application.stopService(intent);

							Intent broadCast = new Intent();
							broadCast.setAction(Constants.ACTION);
							sendBroadcast(broadCast);
						}
					});
					builder.setPositiveButton("确认连接", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							handler.sendEmptyMessageDelayed(100, 1l);
						}
					});
					builder.create().show();
				}
			}
			return false;
		}
		return false;
	}
}