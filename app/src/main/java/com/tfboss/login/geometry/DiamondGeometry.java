package com.tfboss.login.geometry;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.tfboss.login.util.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class DiamondGeometry extends BasicGeometry {
	private float[] vertexs;
	private float[] vertexs_dst;
	private Path path = new Path();
	
	public DiamondGeometry(Paint paint) {
		super(paint);
		vertexs = new float[8];
		vertexs_dst = new float[8];
		vertexs[0] = DrawAttribute.screenWidth/2;
		vertexs[1] =(DrawAttribute.screenHeight-height)/2;
		vertexs[2] = vertexs[0]+width/2;
		vertexs[3] = vertexs[1]+height/2;
		vertexs[4] = vertexs[0];
		vertexs[5] = vertexs[1]+height;
		vertexs[6] = vertexs[0]-width/2;
		vertexs[7] = vertexs[1]+height/2;
	}

	/** 在Canvas上画一个四边钻石型. */
	@Override
	public void drawGraphic(Canvas canvas) {
		geometryMatrix.mapPoints(vertexs_dst, vertexs);
		path.reset();
		path.moveTo(vertexs_dst[0], vertexs_dst[1]);
		path.lineTo(vertexs_dst[2], vertexs_dst[3]);
		path.lineTo(vertexs_dst[4], vertexs_dst[5]);
		path.lineTo(vertexs_dst[6], vertexs_dst[7]);
		path.close();
		canvas.drawPath(path, paint);

	}

}
