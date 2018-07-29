package com.tfboss.login.geometry;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.tfboss.login.util.DrawAttribute;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class LineGeometry extends BasicGeometry {
	private float[] vertexs;
	private float[] vertexs_dst;
	
	public LineGeometry(Paint paint) {
		super(paint);
		vertexs = new float[4];
		vertexs_dst = new float[4];
		vertexs[0] = (DrawAttribute.screenWidth-width)/2;
		vertexs[1] = (DrawAttribute.screenHeight-height)/2;
		vertexs[2] = vertexs[0]+width;
		vertexs[3] = vertexs[1]+height;
	}

	/** 在Canvas上画一条直线 */
	@Override
	public void drawGraphic(Canvas canvas) {
		geometryMatrix.mapPoints(vertexs_dst,vertexs);
		canvas.drawLine(vertexs_dst[0], vertexs_dst[1], vertexs_dst[2], vertexs_dst[3], paint);
	}

}
