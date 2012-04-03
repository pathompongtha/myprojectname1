package com.android.enai;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class DrawPlotView extends View implements OnGestureListener{
	private Calendar cal = Calendar.getInstance();
	private static final String TAG = "DrawView";
	private Handler mHandler;
	private TreeMap<Integer, Integer> points;
	private Paint blkPaint, textPaint;
	private static Bitmap bmp;
	private static final double dx = 62.5;
	private static final double dy = 53;
	private static final int ox = 56;
	private static final int oy = 18;
	private static Button timeOfAdmission;
	
	public DrawPlotView(Context context, int h, int w, Button timeOfAdmission) {
		super(context);
		
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.x);
		points = new TreeMap<Integer, Integer>();
		
		this.timeOfAdmission = timeOfAdmission;
		
		setFocusable(true);
		setFocusableInTouchMode(true);
		blkPaint = new Paint();
		blkPaint.setColor(Color.BLACK);
		blkPaint.setStrokeWidth(5);
		blkPaint.setAntiAlias(true);
		blkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		textPaint = new Paint();
		textPaint.setColor(Color.BLUE);
		textPaint.setStrokeWidth(1);
		textPaint.setTextSize(15);
		textPaint.setAntiAlias(true);
		textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}
	
	public String getValues() {
		StringBuilder sb = new StringBuilder();
		for(int x : points.keySet()) {
			sb.append("("+x+","+points.get(x)+")");
		}
		return sb.toString();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch(e.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			break;
		}
		case MotionEvent.ACTION_UP: {
			int px = (int)((e.getX()-dx/2)/dx);
			int py = (int)((e.getY())/dy);
			px = (int)(ox + dx*px);
			py = (int)(oy + dy*py);
			if(points.containsKey(px) && points.get(px) == py) {
				points.remove(px);
			} else {
				points.put(px, py);
			}
			invalidate();
			break;
		}
		}
		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		Point prev = null;
		int xFirst = -1;
		for(int x : points.keySet()) { 
			int y = points.get(x);
			Rect r = new Rect(x-10, y-10, x+10, y+10);
			canvas.drawBitmap(bmp, null, r, null);

			if(prev == null) {
				
				canvas.drawText(timeOfAdmission.getText().toString().replace(" ", ""), x-35, oy+(float)(6.2*dy), textPaint);
				xFirst = x;
			} else {
				try {
					cal.setTime(new SimpleDateFormat("KK:mm aa").parse(timeOfAdmission.getText().toString()));
					cal.add(Calendar.HOUR, (int)((x-xFirst)/dx+0.5));
					
					canvas.drawText(formatTime(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE)), x-35, oy+(float)(6.2*dy), textPaint);
				} catch(ParseException e) {
					e.printStackTrace();
				}
			}
			
			if(prev != null) canvas.drawLine(prev.x, prev.y, x, y, blkPaint);
			prev = new Point(x,y);
		}
//		if(latest != null) canvas.drawLine(prev.x, prev.y, latest.x, latest.y, bluePaint);
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
	
	private String formatTime(int hourOfDay, int minute) {
		if(hourOfDay >= 13) {
			return String.format("%02d:%02dPM",hourOfDay-12, minute);
		} else if(hourOfDay >= 12){
			return String.format("%02d:%02dPM",hourOfDay, minute);
		} else {
			return String.format("%02d:%02dAM",hourOfDay, minute);
		}
	}
}
