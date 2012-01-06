package com.android.enai;

import static java.lang.Float.parseFloat;

import java.io.IOException;
import java.util.LinkedList;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class FHRAutoPlotView extends View {
	private Handler mHandler;
	private LinkedList<Point> points;
	private Paint paint;
	private int height, width, off;
	private int ctr = 0;
	private int adcSensorValue = 10;
	private int tocoDuration = 0;
	
	// Create TCP server (based on MicroBridge LightWeight Server).
	// Note: This Server runs in a separate thread.
	Server server = null;

	public FHRAutoPlotView(Context context, int h, int w, int resource, Thread thread) {
		super(context);
		points = new LinkedList<Point>();
		off = 20;
		height = h;
		width = w - off;

		setFocusable(true);
		setFocusableInTouchMode(true);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		mHandler = new Handler();
		

	}

	public void getData(String s) {
		points.add(conv((parseFloat(s) - 780.0) / 240.0));
		invalidate();
		if (ctr == width)
			while (width - points.size() < 10)
				points.removeFirst();
		else if (ctr > width)
			points.removeFirst();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(points.isEmpty()) return;
		Point prev = points.getFirst();
		for (final Point point : points) {
			if (prev.x < point.x)
				canvas.drawLine(prev.x, prev.y, point.x, point.y, paint);
			else
				canvas.drawPoint(point.x, point.y, paint);
			prev = point;
		}
	}

	public Point conv(double c) {
		return new Point((ctr+=5) % width + off / 2, (float)(height / 2 - 30 * c));
	}
}
