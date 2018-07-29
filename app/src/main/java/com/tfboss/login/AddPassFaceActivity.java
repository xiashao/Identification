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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.tfboss.login.util.TranObjectType;

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

public class AddPassFaceActivity extends MyActivity implements View.OnClickListener
{
	MyApplication application;
	Client client;
	ClientInputThread in;
	ClientOutputThread out;

	private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;

	private Bitmap mImage = null;
	private byte[] mImageData = null;

	private String mAuthid = null;
	private Toast mToast;
	private ProgressDialog mProDialog;

	private File mPictureFile;

	private FaceRequest mFaceRequest;

	String name;
	User user;

	public void onCreate(Bundle savedInstanceState)
	{
		try {
			super.onCreate(savedInstanceState);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.addpassface);
			application = (MyApplication) getApplicationContext();
			client = application.getClient();
			in = client.getClientInputThread();
			out = client.getClientOutputThread();

			user = (User) getIntent().getSerializableExtra("user");
			name = user.getName();

			mAuthid = name;
			EditText edit=((EditText) findViewById(R.id.online_authid10));
			edit.setText(name);
			edit.setEnabled(false);

			SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));

			findViewById(R.id.online_pick10).setOnClickListener(this);
			findViewById(R.id.online_reg10).setOnClickListener(this);
			findViewById(R.id.online_camera10).setOnClickListener(this);
			findViewById(R.id.online_detect10).setOnClickListener(this);
			findViewById(R.id.online_align10).setOnClickListener(this);

			mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

			mProDialog = new ProgressDialog(this);
			mProDialog.setCancelable(true);
			mProDialog.setTitle("请稍后");

			mProDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (null != mFaceRequest) {
						mFaceRequest.cancel();
					}
				}
			});

			mFaceRequest = new FaceRequest(this);
		}
		catch (Exception e)
		{
			Toast.makeText(this,e.toString(),1).show();
		}
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
			case AddFacePass:
				showTip("添加密码成功");
				Intent intent=new Intent(AddPassFaceActivity.this,Function.class);
				intent.putExtra("user",user);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
				break;
			case AddPassFail:
				showTip("添加密码失败");
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.online_reg100)
		{
			Intent intents=new Intent(AddPassFaceActivity.this,Function.class);

			intents.putExtra("user",user);

			startActivity(intents);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			finish();

			return;
		}
		if(client.getIsConnect())
		{
			switch (v.getId())
			{
				case R.id.online_pick10:
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_PICK);
					startActivityForResult(intent, REQUEST_PICTURE_CHOOSE);
					break;

				case R.id.online_reg10:
					mAuthid = ((EditText) findViewById(R.id.online_authid10)).getText().toString();

					if (TextUtils.isEmpty(mAuthid))
					{
						showTip("用户名不能为空");
						return;
					}
					if (null != mImageData)
					{
						mProDialog.setMessage("注册中...");
						mProDialog.show();

						mFaceRequest.setParameter(SpeechConstant.AUTH_ID, CnToEnglish.getSpell(mAuthid));
						mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");

						mFaceRequest.sendRequest(mImageData, mRequestListener);
					}
					else
					{
						showTip("请选择图片后再注册");
					}

					break;

				case R.id.online_camera10:

					mPictureFile = new File(Environment.getExternalStorageDirectory(),
							"picture" + System.currentTimeMillis()/1000 + ".jpg");

					Intent mIntent = new Intent();
					mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
					mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
					startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
					break;

				case R.id.online_detect10:

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

				case R.id.online_align10:

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

			((ImageView) findViewById(R.id.online_img10)).setImageBitmap(mImage);
		}
	}


	private RequestListener mRequestListener = new RequestListener()
	{

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer)
		{
			if (null != mProDialog)
			{
				mProDialog.dismiss();
			}

			try
			{
				String result = new String(buffer, "utf-8");
				JSONObject object = new JSONObject(result);

				String type = object.optString("sst");

				if ("reg".equals(type))
				{
					register(object);
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

						handler.sendEmptyMessageDelayed(1, 1l);

						break;

					default:
						showTip(error.getPlainDescription(true));
						break;
				}
			}
		}
	};

	private void register(JSONObject obj) throws JSONException
	{
		int ret = obj.getInt("ret");
		if (ret != 0)
		{
			showTip("注册失败");
			return;
		}
		if ("success".equals(obj.get("rst")))
		{
			showTip("声纹注册成功");
			handler.sendEmptyMessageDelayed(1, 1l);
		}
		else
		{
			showTip("注册失败");
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

			((ImageView) findViewById(R.id.online_img10)).setImageBitmap(mImage);
		}
		else
		{
			showTip("检测失败");
		}
	}

	@SuppressWarnings("rawtypes")
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

			((ImageView) findViewById(R.id.online_img10)).setImageBitmap(mImage);
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
					break;
				case 1:
					TranObject<User> tran=new TranObject(TranObjectType.AddFacePass);
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
							Intent serviceBefore=new Intent(AddPassFaceActivity.this,GetMsgService.class);
							stopService(serviceBefore);
							Intent service = new Intent(AddPassFaceActivity.this, GetMsgService.class);
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
			Intent intents=new Intent(AddPassFaceActivity.this,Function.class);

			intents.putExtra("user",user);

			startActivity(intents);
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			finish();

			return false;
		}
		return true;
	}
}