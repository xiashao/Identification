package com.tfboss.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.tfboss.login.util.JsonParser;
import com.tfboss.login.util.TranObject;
import com.tfboss.login.util.TranObjectType;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import me.drakeet.materialdialog.MaterialDialog;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class RegisterCreateCommandActivity extends MyActivity implements View.OnClickListener
{
	private static String TAG = RegisterCreateCommandActivity.class.getSimpleName();

	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private EditText editText;
	private ImageButton button1;
	private ButtonRectangle button3;

	private SpeechRecognizer mIat;

	private RecognizerDialog mIatDialog;

	private HashMap<String, String> mIatResults = new LinkedHashMap();

	private Toast mToast;

	private String mEngineType = SpeechConstant.TYPE_CLOUD;

	private ColorAdapter adapter;
	private String commandString;
	private boolean isClearEditText;
	private User user;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.registercreatecommand);
		application = (MyApplication) getApplicationContext();
		client = application.getClient();
		in = client.getClientInputThread();
		out = client.getClientOutputThread();

		editText = (EditText) findViewById(R.id.iat_text);
		button1 = (ImageButton) findViewById(R.id.iat_recognize);
		button3 = (ButtonRectangle) findViewById(R.id.iat_cancel);
		button1.setOnClickListener(this);
		button3.setOnClickListener(this);

		findViewById(R.id.buttonGo2).setOnClickListener(this);

		isClearEditText=true;
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(isClearEditText) {
					editText.setText("");
					isClearEditText=false;
				}
			}
		});

		mIat = SpeechRecognizer.createRecognizer(RegisterCreateCommandActivity.this, mInitListener);

		mIatDialog = new RecognizerDialog(RegisterCreateCommandActivity.this, mInitListener);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		color = new ArrayList();
		for (int i = 1; i <= 5; i++) {
			color.add(new Integer(i));
		}

		adapter = new ColorAdapter();

		findViewById(R.id.iat_cancelansspanking).setOnClickListener(this);

		user=(User)getIntent().getSerializableExtra("user");
		if(user!=null)
		{
			editText.setHint("创建口令 "+user.getCommand()+" 失败!");
		}
	}

	@Override
	public void getMessage(TranObject o)
	{
		switch (o.getType())
		{
			case CommandCreatedFail:
				Toast.makeText(this,"团队口令创建失败",1).show();
				editText.setEnabled(true);
				break;
			case CommandCreatedSuccessful:	/** 在数据库中创建Command行,并插入Command和对应color. */
				Toast.makeText(this,"团队口令创建成功",1).show();
				editText.setEnabled(true);
				commandString=editText.getText().toString().trim();
				handler.sendEmptyMessageDelayed(1, 200l);
				break;
			case CommandHasBeenUsed:
				Toast.makeText(this,"该团队口令已经被占用",1).show();
				editText.setEnabled(true);
				break;
		}
	}

	Integer colorcharacter=0;
	int ret = 0;

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.buttonGo2)
		{
			Intent intent=new Intent(RegisterCreateCommandActivity.this,LoginGetCommand.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			finish();
			return;
		}
		if(client.getIsConnect()) {
			switch (v.getId())
			{
				case R.id.iat_cancelansspanking:
					Intent intent=new Intent(RegisterCreateCommandActivity.this,RegisterActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
					break;
				case R.id.iat_recognize:
					FlowerCollector.onEvent(RegisterCreateCommandActivity.this, "iat_recognize");

					editText.setText("");
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
				case R.id.iat_cancel:
					if(editText.getText().toString().trim().equals(""))
					{
						Toast.makeText(this,"输入框不能为空",1).show();
					}
					else
					{
						final MaterialDialog builder = new MaterialDialog(this);
						builder.setTitle("选择喜欢的颜色");
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
									text.setText("您选择代表色是蓝色");
								}
								else if(colorcharacter==2)
								{
									text.setText("您选择代表色是黄色");
								}
								else if(colorcharacter==3)
								{
									text.setText("您选择代表色是白色");
								}
								else if(colorcharacter==4)
								{
									text.setText("您选择代表色是红色");
								}
								else if(colorcharacter==5)
								{
									text.setText("您选择代表色是黑色");
								}
							}
						}
						);
						builder.setPositiveButton("确定", new View.OnClickListener()  {
							@Override
							public void onClick(View v) {
								if (colorcharacter == 0) {

								} else {
									TranObject<Command> tran = new TranObject(TranObjectType.CreateCommand);
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
									editText.setEnabled(false);
									out.setMsg(tran);
									builder.dismiss();
								}
							}
						});
						builder.show();
					}
					break;
			}
		}
		else
		{
			final MaterialDialog builder = new  MaterialDialog(this);
			builder.setTitle("服务器尚未连接,点击确认连接服务器");
			builder.setNegativeButton("取消",new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					builder.dismiss();
				}
			});
			builder.setPositiveButton("确认",new View.OnClickListener()
			{
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
			if(convertView==null){
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

		editText.setText(resultBuffer.toString());
		editText.setSelection(editText.length());
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
		FlowerCollector.onResume(RegisterCreateCommandActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(RegisterCreateCommandActivity.this);
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
					Intent intent =new Intent(RegisterCreateCommandActivity.this,RegisterGetPassAndUser.class);
					User user=new User();
					user.setCommand(commandString);
					user.setColor(colorcharacter);
					intent.putExtra("user", user);
					intent.putExtra("TAG",TAG);
					startActivity(intent);
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
							Intent serviceBefore=new Intent(RegisterCreateCommandActivity.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(RegisterCreateCommandActivity.this, GetMsgService.class);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			Intent intent=new Intent(RegisterCreateCommandActivity.this,RegisterActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			finish();
			return false;
		}
		return true;
	}
}