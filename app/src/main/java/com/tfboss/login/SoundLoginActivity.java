package com.tfboss.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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

import me.drakeet.materialdialog.MaterialDialog;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class SoundLoginActivity extends MyActivity implements View.OnClickListener
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

	private TextView mAuthIdTextView;
	private TextView mShowPwdTextView;
	private TextView mShowMsgTextView;
	private TextView mShowRegFbkTextView;
	private TextView mRecordTimeTextView;
	private Toast mToast;

	User user=null;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.soundlogin);

		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		in=client.getClientInputThread();
		out=client.getClientOutputThread();

		initUi();

		mVerifier = SpeakerVerifier.createVerifier(this, new InitListener() {
			@Override
			public void onInit(int errorCode) {
				if (ErrorCode.SUCCESS == errorCode) {
					showTip("引擎初始化成功");
				} else {
					showTip("引擎初始化失败，错误码：" + errorCode);
				}
			}
		});

		user=(User)getIntent().getSerializableExtra("user");
		mAuthId=user.getName();
		mAuthIdTextView.setText(user.getName());
		mAuthIdTextView.setVisibility(View.INVISIBLE);
	}

	@SuppressLint("ShowToast")
	private void initUi()
	{
		mAuthIdTextView = (TextView) findViewById(R.id.txt_authorid2);
		mShowPwdTextView = (TextView) findViewById(R.id.showPwd2);
		mShowMsgTextView = (TextView) findViewById(R.id.showMsg2);
		mShowRegFbkTextView = (TextView) findViewById(R.id.showRegFbk2);
		mRecordTimeTextView = (TextView) findViewById(R.id.recordTime2);

		findViewById(R.id.isv_login2).setOnClickListener(this);		/** "验证"*/
		findViewById(R.id.isv_stop_record2).setOnClickListener(this);/** "停止录音"*/
		findViewById(R.id.isv_cancel2).setOnClickListener(this);		/** "取消"*/

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()== R.id.isv_cancel2)
		{
			Intent intent=new Intent(SoundLoginActivity.this,LoginPassChoose.class);

			user.setPasswordType(user.getPasswordType()+"声纹识别__");

			intent.putExtra("user", user);

			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			finish();

			return;
		}
		if(client.getIsConnect())
		{
			switch (v.getId())
			{
				case R.id.isv_login2:	/** 验证按钮 */

					((TextView) findViewById(R.id.showMsg2)).setText("");

					mVerifier.setParameter(SpeechConstant.PARAMS, null);

					mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");

					mVerifier = SpeakerVerifier.getVerifier();

					mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");


					mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);

					((TextView) findViewById(R.id.showPwd2)).setText("请读出："
								+ mTextPwd);

					mVerifier.setParameter(SpeechConstant.AUTH_ID, CnToEnglish.getSpell(mAuthId));

					mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);

					mVerifier.startListening(mVerifyListener);

					break;

				case R.id.isv_stop_record2:

					mVerifier.stopListening();

					mVerifier.cancel();

					initTextView();

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

	private VerifierListener mVerifyListener = new VerifierListener()
	{
		/** toast--"当前正在说话，音量大小:". */
		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onResult(VerifierResult result)
		{
			if (result.ret == 0) {
				handler.sendEmptyMessageDelayed(0,1l);
			}
			else
			{
				switch (result.err)
				{
					case VerifierResult.MSS_ERROR_IVP_GENERAL:
						mShowMsgTextView.setText("内核异常");
						break;
					case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
						mShowMsgTextView.setText("出现截幅");
						break;
					case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
						mShowMsgTextView.setText("太多噪音");
						break;
					case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
						mShowMsgTextView.setText("录音太短");
						break;
					case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
						mShowMsgTextView.setText("验证不通过，您所读的文本不一致");
						break;
					case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
						mShowMsgTextView.setText("音量太低");
						break;
					case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
						mShowMsgTextView.setText("音频长达不到自由说的要求");
						break;
					default:
						mShowMsgTextView.setText("验证不通过");
						break;
				}
			}
		}

		// 保留方法，暂不用
		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle arg3)
		{

		}

		@Override
		public void onError(SpeechError error)
		{
			switch (error.getErrorCode())
			{
				case ErrorCode.MSP_ERROR_NOT_FOUND:

					break;
				default:
					showTip("onError Code："	+ error.getPlainDescription(true));
					break;
			}
		}

		@Override
		public void onEndOfSpeech() {
			showTip("结束说话");
		}

		@Override
		public void onBeginOfSpeech() {
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
					Intent intent=new Intent(SoundLoginActivity.this,LoginPassChoose.class);
					intent.putExtra("user",user);
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
							Intent serviceBefore=new Intent(SoundLoginActivity.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(SoundLoginActivity.this, GetMsgService.class);
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
			Intent intent=new Intent(SoundLoginActivity.this,LoginPassChoose.class);

			user.setPasswordType(user.getPasswordType()+"声纹识别__");

			intent.putExtra("user", user);

			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			finish();

			return false;
		}
		return true;
	}
}
