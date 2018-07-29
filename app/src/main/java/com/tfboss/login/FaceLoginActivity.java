package com.tfboss.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.tfboss.login.client.User;
import com.tfboss.login.face.FaceUtil;
import com.tfboss.login.net.Client;
import com.tfboss.login.net.ClientInputThread;
import com.tfboss.login.net.ClientOutputThread;
import com.tfboss.login.util.CnToEnglish;
import com.tfboss.login.util.TranObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;

import me.drakeet.materialdialog.MaterialDialog;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */


public class FaceLoginActivity extends MyActivity implements View.OnClickListener
{
	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private TextView view;

	private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;

	private Bitmap mImage = null;
	private byte[] mImageData = null;

	private String mAuthid = null;
	private Toast mToast;
	private ProgressDialog mProDialog;

	private File mPictureFile;

	private FaceRequest mFaceRequest;

	User user=null;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.facelogin);
		application = (MyApplication) getApplicationContext();
		client=application.getClient();
		in=client.getClientInputThread();
		out=client.getClientOutputThread();

		SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));

		findViewById(R.id.online_pick2).setOnClickListener(this);
		findViewById(R.id.online_verify2).setOnClickListener(this);
		findViewById(R.id.online_camera2).setOnClickListener(this);
		findViewById(R.id.online_detect2).setOnClickListener(this);
		findViewById(R.id.online_align2).setOnClickListener(this);
		findViewById(R.id.ansans).setOnClickListener(this);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mProDialog = new ProgressDialog(this);
		mProDialog.setCancelable(true);
		mProDialog.setTitle("请稍后");

		/** 弹出进度条 被关闭时,取消正在执行的人脸识别操作.*/
		mProDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				if (null != mFaceRequest)
				{
					mFaceRequest.cancel();
				}
			}
		});
		mFaceRequest = new FaceRequest(this);

		user=(User)getIntent().getSerializableExtra("user");

		mAuthid=user.getName();

		view=(TextView)findViewById(R.id.online_authid2);

		view.setText(mAuthid);

		view.setVisibility(View.INVISIBLE);
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
		if(v.getId()==R.id.ansans)
		{
			Intent intentss=new Intent(FaceLoginActivity.this,LoginPassChoose.class);

			user.setPasswordType(user.getPasswordType() + "声纹识别__");

			intentss.putExtra("user", user);

			startActivity(intentss);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			finish();

			return;
		}
		if(client.getIsConnect())
		{
			switch (v.getId())
			{
				case R.id.online_pick2:
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_PICK);
					startActivityForResult(intent, REQUEST_PICTURE_CHOOSE);
					break;

				case R.id.online_verify2:

					if (null != mImageData)
					{
						mProDialog.setMessage("验证中...");
						mProDialog.show();

						mFaceRequest.setParameter(SpeechConstant.AUTH_ID, CnToEnglish.getSpell(mAuthid));
						mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");

						mFaceRequest.sendRequest(mImageData, mRequestListener);
					}
					else
					{
						showTip("请选择图片");
					}
					break;
				case R.id.online_detect2:

					if (null != mImageData)
					{
						mProDialog.setMessage("检测中...");
						mProDialog.show();

						mFaceRequest.setParameter(SpeechConstant.WFR_SST, "detect");

						mFaceRequest.sendRequest(mImageData, mRequestListener);
					}
					else
					{
						showTip("请选择图片后再检测");
					}
					break;
				case R.id.online_align2:
					if (null != mImageData)
					{
						mProDialog.setMessage("聚焦中...");
						mProDialog.show();

						mFaceRequest.setParameter(SpeechConstant.WFR_SST, "align");

						mFaceRequest.sendRequest(mImageData, mRequestListener);
					}
					else
					{
						showTip("请选择图片后再聚集");
					}
					break;
				case R.id.online_camera2:
					mPictureFile = new File(Environment.getExternalStorageDirectory(),
							"picture" + System.currentTimeMillis()/1000 + ".jpg");

					Intent mIntent = new Intent();
					mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
					mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
					startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
		{
			return;
		}

		String fileSrc;
		if (requestCode == REQUEST_PICTURE_CHOOSE)
		{
			if ("file".equals(data.getData().getScheme()))
			{
				fileSrc = data.getData().getPath();
			}
			else
			{
				String[] proj = {MediaStore.Images.Media.DATA};
				Cursor cursor = getContentResolver().query(data.getData(), proj,
						null, null, null);
				cursor.moveToFirst();
				int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				fileSrc = cursor.getString(idx);
				cursor.close();
			}
			FaceUtil.cropPicture(this, Uri.fromFile(new File(fileSrc)));
		}
		else if (requestCode == REQUEST_CAMERA_IMAGE)
		{
			if (null == mPictureFile)
			{
				showTip("拍照失败，请重试");
				return;
			}

			fileSrc = mPictureFile.getAbsolutePath();

			updateGallery(fileSrc);

			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
		}
		else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE)
		{
			Bitmap bmp = data.getParcelableExtra("data");

			if(null != bmp){
				FaceUtil.saveBitmapToFile(this, bmp);
			}

			fileSrc = FaceUtil.getImagePath(this);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			mImage = BitmapFactory.decodeFile(fileSrc, options);

			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			mImage = BitmapFactory.decodeFile(fileSrc, options);

			if(null == mImage) {
				showTip("图片信息无法正常获取！");
				return;
			}

			int degree = FaceUtil.readPictureDegree(fileSrc);
			if (degree != 0)
			{
				mImage = FaceUtil.rotateImage(degree, mImage);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			mImageData = baos.toByteArray();

			((ImageView) findViewById(R.id.online_img2)).setImageBitmap(mImage);
		}
	}

	private RequestListener mRequestListener = new RequestListener() {

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer)
		{
			try
			{
				String result = new String(buffer, "utf-8");
				JSONObject object = new JSONObject(result);

				String type = object.optString("sst");

				if ("verify".equals(type))
				{
					verify(object);
				}
				else if ("detect".equals(type))
				{
					detect(object);
				}
				else if ("align".equals(type))
				{
					align(object);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onCompleted(SpeechError error)
		{
			if (null != mProDialog)
			{
				mProDialog.dismiss();
			}

			if (error != null)
			{
				switch (error.getErrorCode())
				{
					case ErrorCode.MSP_ERROR_ALREADY_EXIST:
						showTip("authid已经被注册，请更换后再试");
						break;

					default:
						showTip(error.getPlainDescription(true));
						break;
				}
			}
		}
	};

	private void verify(JSONObject obj) throws JSONException {
		int ret = obj.getInt("ret");
		if (ret != 0) {
			showTip("验证失败");
			return;
		}
		if ("success".equals(obj.get("rst")))
		{
			if (obj.getBoolean("verf"))
			{
				showTip("验证通过");
				handler.sendEmptyMessageDelayed(0,1l);
			}
		} else {
			showTip("验证失败");
		}
	}

	private void detect(JSONObject obj) throws JSONException
	{
		int ret = obj.getInt("ret");
		if (ret != 0) {
			showTip("检测失败");
			return;
		}

		if ("success".equals(obj.get("rst")))
		{
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);

			Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
					mImage.getHeight(), Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmap);

			canvas.drawBitmap(mImage, new Matrix(), null);

			JSONArray faceArray = obj.getJSONArray("face");

			for (int i = 0; i < faceArray.length(); i++)
			{
				float x1 = (float) faceArray.getJSONObject(i)
						.getJSONObject("position").getDouble("left");
				float y1 = (float) faceArray.getJSONObject(i)
						.getJSONObject("position").getDouble("top");
				float x2 = (float) faceArray.getJSONObject(i)
						.getJSONObject("position").getDouble("right");
				float y2 = (float) faceArray.getJSONObject(i)
						.getJSONObject("position").getDouble("bottom");

				paint.setStyle(Paint.Style.STROKE);

				canvas.drawRect(new Rect((int)x1, (int)y1, (int)x2, (int)y2),
						paint);
			}

			mImage = bitmap;

			((ImageView) findViewById(R.id.online_img2)).setImageBitmap(mImage);
		}
		else
		{
			showTip("检测失败");
		}
	}

	private void align(JSONObject obj) throws JSONException
	{
		int ret = obj.getInt("ret");
		if (ret != 0)
		{
			showTip("聚焦失败");
			return;
		}

		if ("success".equals(obj.get("rst")))
		{
			Paint paint = new Paint();
			paint.setColor(Color.BLUE);
			paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);

			Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
					mImage.getHeight(), Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmap);

			canvas.drawBitmap(mImage, new Matrix(), null);

			JSONArray faceArray = obj.getJSONArray("result");

			for (int i = 0; i < faceArray.length(); i++)
			{
				JSONObject landmark = faceArray.getJSONObject(i).getJSONObject("landmark");

				Iterator it = landmark.keys();

				while (it.hasNext())
				{
					String key = (String) it.next();
					JSONObject postion = landmark.getJSONObject(key);
					canvas.drawPoint((float) postion.getDouble("x"),
							(float) postion.getDouble("y"), paint);
				}
			}

			mImage = bitmap;

			((ImageView) findViewById(R.id.online_img2)).setImageBitmap(mImage);
		}
		else
		{
			showTip("聚焦失败");
		}
	}

	@Override
	public void finish() {
		if (null != mProDialog)
		{
			mProDialog.dismiss();
		}
		super.finish();
	}

	private void updateGallery(String filename)
	{
		MediaScannerConnection.scanFile(this, new String[]{filename}, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {

					}
				});
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
					Intent intent=new Intent(FaceLoginActivity.this,LoginPassChoose.class);
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
							Intent serviceBefore=new Intent(FaceLoginActivity.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(FaceLoginActivity.this, GetMsgService.class);
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
			Intent intent=new Intent(FaceLoginActivity.this,LoginPassChoose.class);

			user.setPasswordType(user.getPasswordType()+"人脸识别__");

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