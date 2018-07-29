package com.tfboss.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.tfboss.login.client.User;
import com.tfboss.login.net.Client;
import com.tfboss.login.net.ClientInputThread;
import com.tfboss.login.net.ClientOutputThread;
import com.tfboss.login.util.CnToEnglish;
import com.tfboss.login.util.TranObject;
import com.tfboss.login.util.TranObjectType;

import me.drakeet.materialdialog.MaterialDialog;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class SoundRegisterActivity extends MyActivity implements View.OnClickListener
{
	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private static final int PWD_TYPE_TEXT = 1;

	private int mPwdType = PWD_TYPE_TEXT;

	private String mAuthId = "";

	private String mTextPwd = "芝麻开门";

	private SpeakerVerifier mVerifier;

	private EditText idEditText;
	private TextView mShowPwdTextView;
	private TextView mShowMsgTextView;
	private TextView mShowRegFbkTextView;
	private TextView mRecordTimeTextView;
	private Toast mToast;

	String name;
	User user;
	String TAG;

	AlertDialog.Builder builder;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.soundregister);

		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		in=client.getClientInputThread();
		out=client.getClientOutputThread();

		user=(User)getIntent().getSerializableExtra("user");
		TAG=(String)getIntent().getSerializableExtra("TAG");
		name=user.getName();

		initUi();

		mVerifier = SpeakerVerifier.createVerifier(SoundRegisterActivity.this, new InitListener()
		{
			@Override
			public void onInit(int errorCode)
			{
				if (ErrorCode.SUCCESS == errorCode)
				{

				}
				else
				{
					showTip("声纹识别引擎--初始化失败，错误码：" + errorCode);
				}
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setTitle("是否设置其他形式的登陆验证?");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				handler.sendEmptyMessage(1);
			}
		});
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent=new Intent(SoundRegisterActivity.this,RegisterGetPassAndUser.class);
				intent.putExtra("user",user);
				intent.putExtra("TAG",TAG);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
			}
		});
	}

	private void initUi()
	{
		idEditText=(EditText)findViewById(R.id.spankText);
		idEditText.setText(name);
		idEditText.setEnabled(false);

		mShowPwdTextView = (TextView) findViewById(R.id.showPwd);
		mShowMsgTextView = (TextView) findViewById(R.id.showMsg);
		mShowRegFbkTextView = (TextView) findViewById(R.id.showRegFbk);
		mRecordTimeTextView = (TextView) findViewById(R.id.recordTime);

		findViewById(R.id.isv_register).setOnClickListener(this);
		findViewById(R.id.isv_stop_record).setOnClickListener(this);
		findViewById(R.id.isv_cancel).setOnClickListener(this);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	private void initTextView()
	{
		mShowPwdTextView.setText("");
		mShowMsgTextView.setText("");
		mShowRegFbkTextView.setText("");
		mRecordTimeTextView.setText("");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void getMessage(TranObject o)
	{
		switch (o.getType())
		{
			case RegisterSuccess:
				showTip("注册成功");
				Intent intent=new Intent(SoundRegisterActivity.this,LoginGetCommand.class);
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
				case R.id.isv_register:	/** 注册按钮.*/

					mAuthId=idEditText.getText().toString();

					if(TextUtils.isEmpty(mAuthId))
					{
						showTip("用户名不能为空。");

						return;
					}

					mVerifier.setParameter(SpeechConstant.PARAMS, null);

					mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");

					if (mPwdType == PWD_TYPE_TEXT)
					{
						mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);

						mShowPwdTextView.setText("请读出：" + mTextPwd);

						mShowMsgTextView.setText("训练 第" + 1 + "遍，剩余4遍");
					}

					mVerifier.setParameter(SpeechConstant.AUTH_ID, CnToEnglish.getSpell(mAuthId));

					mVerifier.setParameter(SpeechConstant.ISV_SST, "train");

					mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);

					mVerifier.startListening(mRegisterListener);

					break;

				case R.id.isv_stop_record:

					mVerifier.stopListening();

					mVerifier.cancel();

					initTextView();

					break;

				case R.id.isv_cancel:

					Intent intent=new Intent(SoundRegisterActivity.this,RegisterGetPassAndUser.class);
					String [] spank=user.getPasswordType().split("__");
					StringBuffer buffer=new StringBuffer();
					for(String spank2:spank)
					{
						if(!"声纹识别".equals(spank2))
						{
							buffer.append(spank2+"__");
						}
					}
					user.setPasswordType(buffer.toString().trim());
					intent.putExtra("user", user);
					intent.putExtra("TAG",TAG);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();

					break;
				default:
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

	private VerifierListener mRegisterListener = new VerifierListener()
	{
		@Override
		public void onVolumeChanged(int volume, byte[] data)
		{
			showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onResult(VerifierResult result)
		{
			((TextView)findViewById(R.id.showMsg)).setText(result.source);

			if (result.ret == ErrorCode.SUCCESS)
			{
				switch (result.err)
				{
					case VerifierResult.MSS_ERROR_IVP_GENERAL:
						mShowMsgTextView.setText("内核异常");
						break;
					case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
						mShowRegFbkTextView.setText("训练达到最大次数");
						break;
					case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
						mShowRegFbkTextView.setText("出现截幅");
						break;
					case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
						mShowRegFbkTextView.setText("太多噪音");
						break;
					case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
						mShowRegFbkTextView.setText("录音太短");
						break;
					case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
						mShowRegFbkTextView.setText("训练失败，您所读的文本不一致");
						break;
					case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
						mShowRegFbkTextView.setText("音量太低");
						break;
					case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
						mShowMsgTextView.setText("音频长达不到自由说的要求");
					default:
						mShowRegFbkTextView.setText("");
						break;
				}

				if (result.suc == result.rgn)
				{
					mShowMsgTextView.setText("声纹注册成功");
					handler.sendEmptyMessageDelayed(0, 1l);
				}
				else
				{
					int nowTimes = result.suc + 1;
					int leftTimes = result.rgn - nowTimes;

					if (PWD_TYPE_TEXT == mPwdType)
					{
						mShowPwdTextView.setText("请读出：" + mTextPwd);
					}

					mShowMsgTextView.setText("训练 第" + nowTimes + "遍，剩余" + leftTimes + "遍");
				}
			}
			else
			{
				mShowMsgTextView.setText("注册失败，请重新开始。");
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onError(SpeechError error)
		{
			if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST)
			{
				showTip("该用户名对应的声纹模型已存在");
			}
			else
			{
				showTip("onError Code：" + error.getPlainDescription(true));
			}
		}

		@Override
		public void onEndOfSpeech()
		{
			showTip("结束说话");
		}

		@Override
		public void onBeginOfSpeech()
		{
			showTip("开始说话");
		}
	};

	@Override
	public void finish()
	{
		super.finish();
	}

	@Override
	protected void onDestroy()
	{
		if (null != mVerifier)
		{
			mVerifier.stopListening();
			mVerifier.destroy();
		}
		super.onDestroy();
	}

	private void showTip(final String str)
	{
		mToast.setText(str);
		mToast.show();
	}

	private android.os.Handler handler = new android.os.Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					builder.create().show();
					break;
				case 1:
					TranObject<User> tran=new TranObject(TranObjectType.Register);
					tran.setObject(user);
					out.setMsg(tran);
					break;
				case 100:
						client.setIsConnect(client.start());
						if(client.getIsConnect())
						{
							showTip("成功连接服务端");
							in = client.getClientInputThread();
							out = client.getClientOutputThread();
							Intent serviceBefore=new Intent(SoundRegisterActivity.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(SoundRegisterActivity.this, GetMsgService.class);
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
				Toast.makeText(getApplicationContext(), "再按一次退出声纹验证方式设置", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				Intent intent = new Intent(SoundRegisterActivity.this,RegisterGetPassAndUser.class);
				String [] spank=user.getPasswordType().split("__");
				StringBuffer buffer=new StringBuffer();
				for(String spank2:spank)
				{
					if(!"声纹识别".equals(spank2))
					{
						buffer.append(spank2+"__");
					}
				}
				user.setPasswordType(buffer.toString().trim());
				intent.putExtra("user", user);
				intent.putExtra("TAG",TAG);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
			}
			return false;
		}
		return true;
	}
}