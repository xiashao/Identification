package com.tfboss.login.geometry;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.tfboss.login.util.DrawAttribute;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class PentagonGeometry extends BasicGeometry {
	private float[] vertexs;
	private float[] vertexs_dst;
	private Path path = new Path();

	public PentagonGeometry(Paint paint) {
		super(paint);
		float sin18 = (float) Math.sin(Math.PI / 10);
		float cos18 = (float) Math.cos(Math.PI / 10);
		float sin54 = (float) Math.sin(Math.PI * 0.3);
		float cos54 = (float) Math.cos(Math.PI * 0.3);
		vertexs = new float[10];
		vertexs_dst = new float[10];
		vertexs[0] = DrawAttribute.screenWidth/2;
		vertexs[1] = (DrawAttribute.screenHeight-height)/2;
		vertexs[2] = vertexs[0]+width*cos18/2;
		vertexs[3] = vertexs[1]+height*(1-sin18)/2;
		vertexs[4] = vertexs[0]+width*cos54/2;
		vertexs[5] = vertexs[1]+height*(1+sin54)/2;
		vertexs[6] = vertexs[0]-width*cos54/2;
		vertexs[7] = vertexs[1]+height*(1+sin54)/2;
		vertexs[8] = vertexs[0]-width*cos18/2;
		vertexs[9] = vertexs[1]+height*(1-sin18)/2;
	}

	/** 在Canvas上画一个五边形. */
	@Override
	public void drawGraphic(Canvas canvas) {
		geometryMatrix.mapPoints(vertexs_dst, vertexs);
		path.reset();
		path.moveTo(vertexs_dst[0], vertexs_dst[1]);
		path.lineTo(vertexs_dst[2], vertexs_dst[3]);
		path.lineTo(vertexs_dst[4], vertexs_dst[5]);
		path.lineTo(vertexs_dst[6], vertexs_dst[7]);
		path.lineTo(vertexs_dst[8], vertexs_dst[9]);
		path.close();
		canvas.drawPath(path, paint);
	}

}
