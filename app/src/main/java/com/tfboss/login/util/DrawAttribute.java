package com.tfboss.login.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import java.io.IOException;
import java.io.InputStream;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class DrawAttribute
{
    /** 根据这个enum类型的DrawStatus的数据决定画画的区域DrawView的绘图样式 */
    public enum DrawStatus
    {
        CASUAL_WATER,
    }

    public final static int backgroundOnClickColor = 0xfff08d1e; /** 橙黄色 */

    public static Paint paint = new Paint();/** 在SplashActivity初始化时设置颜色,线宽的画笔 */

    public static int screenHeight; /** 用于存储屏幕高度 */
    public static int screenWidth;  /** 用于存储屏幕宽度 */

    public static Bitmap getImageFromAssetsFile(Context context,String fileName,boolean isBackground)
    {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if(isBackground)
        {
            image = Bitmap.createScaledBitmap(image, DrawAttribute.screenWidth, DrawAttribute.screenHeight, false);
        }
        return image;
    }
}
