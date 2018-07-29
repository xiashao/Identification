package com.tfboss.login.geometry;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.tfboss.login.util.DrawAttribute;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class RightTriangleGeometry extends BasicGeometry {
	private float[] vertexs;
	private float[] vertexs_dst;
	private Path path = new Path();

	public RightTriangleGeometry(Paint paint) {
		super(paint);
		vertexs = new float[6];
		vertexs_dst = new float[6];
		vertexs[0] = (DrawAttribute.screenWidth-width)/2;
		vertexs[1] = (DrawAttribute.screenHeight-height)/2;
		vertexs[2] = vertexs[0];
		vertexs[3] = vertexs[1]+height;
		vertexs[4] = vertexs[0]+width;
		vertexs[5] = vertexs[3];
	}

	/** 在Canvas上画一个直角三角形. */
	@Override
	public void drawGraphic(Canvas canvas) {
		geometryMatrix.mapPoints(vertexs_dst, vertexs);
		path.reset();
		path.moveTo(vertexs_dst[0], vertexs_dst[1]);
		path.lineTo(vertexs_dst[2], vertexs_dst[3]);
		path.lineTo(vertexs_dst[4], vertexs_dst[5]);
		path.close();
		canvas.drawPath(path, paint);
	}

}
