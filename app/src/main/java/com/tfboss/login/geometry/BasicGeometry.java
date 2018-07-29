package com.tfboss.login.geometry;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.Toast;

import com.tfboss.login.DrawView;
import com.tfboss.login.util.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public abstract class BasicGeometry
{
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode;
    private float lastXDrag;
    private float lastYDrag;
    private PointF onDownZoomMidPoint= new PointF();
    private float onDownZoomDist;
    private float onDownZoomRotation = 0;

    private Matrix onDownMatrix;
    private Matrix onMoveMatrix;
    protected Matrix geometryMatrix;

    protected float width;
    protected float height;
    protected Paint paint;

    public BasicGeometry(Paint paint)
    {
        width = 256;
        height = 256;

        geometryMatrix = new Matrix();
        onDownMatrix = new Matrix();
        onMoveMatrix = new Matrix();

        this.paint = paint;
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                lastXDrag = event.getX();
                lastYDrag = event.getY();

                onDownMatrix.set(geometryMatrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                mode = ZOOM;

                onDownZoomDist = spacing(event);

                onDownZoomRotation = rotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

                onDownMatrix.set(geometryMatrix);

                midPoint(onDownZoomMidPoint, event);
                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == ZOOM)
                {
                        onMoveMatrix.set(onDownMatrix);

                        float rotation = rotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
                                - onDownZoomRotation;

                        float scale = spacing(event) / onDownZoomDist;

                        int widthMid = DrawAttribute.screenWidth / 2, heightMid = DrawAttribute.screenHeight / 2;
                        onMoveMatrix.postScale(scale, scale, widthMid, heightMid);
                        onMoveMatrix.postRotate(rotation, widthMid, heightMid);
                        FingerPassword.setRotate(rotation);
                        FingerPassword.setScale(scale);

                        geometryMatrix.set(onMoveMatrix);
                }
                else if (mode == DRAG)
                {

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }

    /**  计算两个触碰点之间的距离 */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**  取两个触碰点的中间点 */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** 通过两个触碰点,计算两触碰点的连线与X轴正方向的角度 */
    protected float rotation(float x0, float y0, float x1, float y1)
    {
        double delta_x = (x0 - x1);
        double delta_y = (y0 - y1);

        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);
    }

    public abstract void drawGraphic(Canvas canvas);
}
