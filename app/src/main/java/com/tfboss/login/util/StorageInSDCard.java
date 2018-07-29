package com.tfboss.login.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;
import com.tfboss.login.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

/** 对SD卡存读的类 */
public class StorageInSDCard
{
	/** 检测外部设备是否可用可写 */
	public static boolean IsExternalStorageAvailableAndWriteable()
	{
		/** 表示外部存储设备是否可用 */
		boolean externalStorageAvailable;

		/** 表示外部存储设备是否可写 */
		boolean externalStorageWriteable;

		/** 获取外部存储设备的状态 */
		String state = Environment.getExternalStorageState();

		if(Environment.MEDIA_MOUNTED.equals(state))
		{
			/** 如果外部存储设备状态为可写: */

			externalStorageAvailable = externalStorageWriteable = true;
		}
		else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			/** 如果外部存储设备状态为只读: */

			externalStorageAvailable = true;
			externalStorageWriteable = false;
		}
		else
		{
			/** 如果外部存储设备状态为不可用: */

			externalStorageAvailable = externalStorageWriteable = false;
		}
		return externalStorageAvailable && externalStorageWriteable;
	}

	/** 将图片文件保存到指定文件路径. */
	public static void saveBitmapInExternalStorage(Bitmap bitmap,Context context)
	{
		try
		{
			/** 判断外部设备是否可用可写 */
			if(IsExternalStorageAvailableAndWriteable())
			{
				/** 获取外部存储设备中文件存储路径 */
				File extStorage = new File(Environment.getExternalStorageDirectory().getPath() +"/drawpics");

				if (!extStorage.exists())
				{
					/** 如果存储路径不存在,制造出该路径. */
					extStorage.mkdirs();
				}

				/** 用文件存储路径和当前时间(当前时间+.png作为文件名)生成文件File */
				File file = new File(extStorage, System.currentTimeMillis()+".png");

				/** 用该file获得输出流. */
				FileOutputStream fOut = new FileOutputStream(file);

				/** 确定该图片文件的输出格式,输出质量,输出流 */
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

				/** 用输出流把该文件输出到指定路径 */
				fOut.flush();
				fOut.close();

				/** Toast显示"已保存绘画图片" */
				Toast.makeText(context, R.string.save_bitmap, Toast.LENGTH_SHORT).show();
			}
			else
			{
				/** Toast显示"找不到SD卡，保存失败" */
				Toast.makeText(context, R.string.fail_save_bitmap, Toast.LENGTH_SHORT).show();
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
