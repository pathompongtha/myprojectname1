package com.android.Rx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RxPlotter extends Activity {

	static private int height, width;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();

		setContentView(R.layout.main);
		
		RelativeLayout screen = (RelativeLayout)findViewById(R.id.screen);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width-20,height-20);
		
		DrawView drawview = new DrawView(this, height, width);
		params.setMargins(10, 10, 10, 10);
		screen.addView(drawview,params);
//		
//		TextView tv = new TextView(this);
//		tv.setText("Plot below");
//		params.setMargins(0,0,0,0);
//		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		screen.addView(tv,params);

//		DrawView drawview = new DrawView(this, height, width);
//		setContentView(drawview);
	}

	public class Panel extends SurfaceView implements SurfaceHolder.Callback {

		private CanvasThread canvasthread;

		public Panel(Context context, AttributeSet attrs) {
			super(context, attrs);
			getHolder().addCallback(this);
			canvasthread = new CanvasThread(getHolder(), this);
			setFocusable(true);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			canvasthread.setRunning(true);
			canvasthread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			boolean retry = true;
			canvasthread.setRunning(false);
			while (retry) {
				try {
					canvasthread.join();
					retry = false;
				} catch (InterruptedException e) {
					// we will try it again and again...
				}
			}
		}

		@Override
		public void onDraw(Canvas canvas) {

			Paint paint = new Paint();

			Bitmap kangoo = BitmapFactory.decodeResource(getResources(),
					R.drawable.cat1);
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(kangoo, 10, 10, null);

		}

	}

	public class CustomDrawableView extends View {
		private ShapeDrawable mDrawable;

		public CustomDrawableView(Context context) {
			super(context);

			int x = 10, y = 10;
			int width = 300;
			int height = 50;

			mDrawable = new ShapeDrawable(new OvalShape());
			mDrawable.getPaint().setColor(0xff74AC23);
			mDrawable.setBounds(x, y, x + width, y + height);
		}

		protected void onDraw(Canvas canvas) {
			mDrawable.draw(canvas);
		}
	}
}