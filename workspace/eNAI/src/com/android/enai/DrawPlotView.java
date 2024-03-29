package com.android.enai;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class DrawPlotView extends View implements OnGestureListener{
	private static final String TAG = "DrawView";
	private Button approve;
	private Handler mHandler;
	private LinkedList<Point> points;
	private Point latest;
	private Paint blkPaint, bluePaint;

	public DrawPlotView(Context context, int h, int w, Button approve) {
		super(context);
		points = new LinkedList<Point>();
		points.add(new Point(0,h));
		
		setFocusable(true);
		setFocusableInTouchMode(true);
		blkPaint = new Paint();
		blkPaint.setColor(Color.BLACK);
		blkPaint.setStrokeWidth(5);
		blkPaint.setAntiAlias(true);
		blkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		bluePaint = new Paint();
		bluePaint.setColor(Color.BLUE);
		bluePaint.setStrokeWidth(5);
		bluePaint.setAntiAlias(true);
		bluePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		this.approve = approve;
		this.approve.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(latest != null) {
					points.add(latest);
					latest = null;
					invalidate();
				}
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch(e.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			latest = new Point((int)e.getX(),(int)e.getY());
			invalidate();
			break;
		}
		case MotionEvent.ACTION_UP: {
			
			break;
		}
		}
		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		Point prev = points.getFirst();
		for(Point p : points) { 
			canvas.drawLine(prev.x, prev.y, p.x, p.y, blkPaint);
			prev = p;
		}
		if(latest != null) canvas.drawLine(prev.x, prev.y, latest.x, latest.y, bluePaint);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
