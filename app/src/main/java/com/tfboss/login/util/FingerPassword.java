package com.tfboss.login.util;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class FingerPassword
{
    private static Integer back=0;
    private static Integer pic=0;
    private static Integer forward=0;
    private static Float rotate=0f;
    private static Float scale=1f;

    public FingerPassword()
    {

    }

    public static void setForward(Integer forward)
    {
        FingerPassword.forward=forward;
    }

    public static Integer getForward()
    {
        return forward;
    }

    public static void setScale(Float scale)
    {
        FingerPassword.scale=scale;
    }

    public static Float getScale()
    {
        return FingerPassword.scale;
    }

    public static void setRotate(Float rotate)
    {
        FingerPassword.rotate=rotate;
    }

    public static void setPic(Integer pic)
    {
        FingerPassword.pic=pic;
    }

    public static void setBack(Integer back)
    {
        FingerPassword.back=back;
    }

    public static String getPassword()
    {
        return "back_"+FingerPassword.back+"_pic_"+FingerPassword.pic+"_forward_"+FingerPassword.forward
                + "_rotate_"+FingerPassword.rotate+"_scale_"+FingerPassword.scale;
    }
}
