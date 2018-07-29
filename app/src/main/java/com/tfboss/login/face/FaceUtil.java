package com.tfboss.login.face;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class FaceUtil
{
	public final static int REQUEST_CROP_IMAGE = 3;

	public static void cropPicture(Activity activity, Uri uri) {
		Intent innerIntent = new Intent("com.android.camera.action.CROP");
		innerIntent.setDataAndType(uri, "image/*");
		innerIntent.putExtra("crop", "true");	//才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
		innerIntent.putExtra("aspectX", 1); 	//放大缩小比例的X
		innerIntent.putExtra("aspectY", 1);		//放大缩小比例的X   这里的比例为：   1:1
		innerIntent.putExtra("outputX", 320);  	//这个是限制输出图片大小
		innerIntent.putExtra("outputY", 320); 
		innerIntent.putExtra("return-data", true);
		innerIntent.putExtra("scale", true);
		innerIntent.putExtra("scaleUpIfNeeded", true);
		File imageFile = new File(getImagePath(activity.getApplicationContext()));
		innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
		innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		activity.startActivityForResult(innerIntent, REQUEST_CROP_IMAGE);
	}

	public static String getImagePath(Context context)
	{
		String path;
		
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			path = context.getFilesDir().getAbsolutePath();
		}
		else
		{
			path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/";
		}
		if(!path.endsWith("/")) {
			path += "/";
		}
		File folder = new File(path);
		if (folder != null && !folder.exists())
		{
			folder.mkdirs();
		}
		path += "ifd.jpg";
		return path;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotateImage(int angle, Bitmap bitmap)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		return resizedBitmap;
	}

	public static void saveBitmapToFile(Context context,Bitmap bmp){
		String file_path = getImagePath(context);
		File file = new File(file_path);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
