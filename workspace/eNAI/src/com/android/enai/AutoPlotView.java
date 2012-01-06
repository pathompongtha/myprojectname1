package com.android.enai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import static java.lang.Float.*;

public class AutoPlotView extends View {
	private static final String TAG = "DrawView";
	// private Context mContext;
	private Handler mHandler;
	private LinkedList<Point> points;
	private Paint paint;
	private int height, width, off;
	private int ctr = 0;

	public AutoPlotView(Context context, int h, int w, int resource, Thread thread) {
		super(context);
		points = new LinkedList<Point>();
		// mContext = context;
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

//		InputStream inputStream = getResources().openRawResource(resource);
//		final BufferedReader br = new BufferedReader(new InputStreamReader(
//				inputStream));
//		mHandler = new Handler();
//		thread = new Thread(new Runnable() {
//			public void run() {
//				try {
//					while (true) {
//						final String s = br.readLine();
//						if (s == null)
//							break;
//						if (s.equals(""))
//							continue;
//						mHandler.post(new Runnable() {
//							public void run() {
//								getData(s);
//							}
//						});
//						Thread.sleep(100);
//					}
//				} catch (Exception e) {
//				}
//			}
//		});
//		thread.start();
	}

	public void getData(String s) {
		points.add(conv((parseFloat(s) - 100) / 100.0));
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

class Point {
	float x, y;

	public Point() {

	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}
}