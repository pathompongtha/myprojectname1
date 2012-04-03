package com.android.enai;

import static java.lang.Float.parseFloat;

import java.io.IOException;
import java.net.BindException;
import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UCAutoPlotView extends View {
	private Handler mHandler;
	private static LinkedList<Point> points;
	private Paint paint;
	private int height, width, off;
	private int ctr = 0;
	private int adcSensorValue = 10;
	private int tocoDuration = 0;
	private TocoTableActivity tocoTableActivity;
	private Date time = new Date();
	private static boolean isReceiving;
	private int numberOfContractions = 0;
	
	static {
		isReceiving = false;
		points = new LinkedList<Point>();
	}
	
	// Create TCP server (based on MicroBridge LightWeight Server).
	// Note: This Server runs in a separate thread.
	Server server = null;

	public UCAutoPlotView(Context context, int h, int w, int resource, Thread thread, Button tocoZeroButton) {
		super(context);
		
		tocoTableActivity = new TocoTableActivity();
		tocoTableActivity.init();
//		tocoTableActivity.add("a","b");
		
		off = 20;
		height = h;
		width = w - off;

		setFocusable(true);
		setFocusableInTouchMode(true);
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		// Create TCP server (based on MicroBridge LightWeight Server)
		try {
			server = new Server(4568); // Use the same port number used in
										// ADK Main Board firmware
			server.start();
			
			server.send(new byte[]{1});
			
			Toast.makeText(getContext(), "UC Server OK", Toast.LENGTH_SHORT).show();
		} catch(BindException e) {
			Toast.makeText(getContext(), "BP Server already started", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(getContext(), "Error during startup of UC Server", Toast.LENGTH_LONG).show();
			Log.e("Seeeduino ADK", "Unable to start TCP server", e);
			e.printStackTrace();
//			System.exit(-1);
		}
		
		tocoZeroButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					server.send(new byte[]{1});
				} catch(Exception e){}
			}
		});
		
		mHandler = new Handler();
		server.addListener(new AbstractServerListener() {

            @Override
            public void onReceive(org.microbridge.server.Client client,
                    byte[] data) {
            	Log.d("UC Data", data.length+" "+Arrays.toString(data));
                if (data.length < 2)
                    return;
                final int adcSensorValue = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
                final int contractionLength = (data[2] & 0xff) | ((data[3] & 0xff) << 8);
                if(data.length > 4) numberOfContractions = (data[4] & 0xff) | ((data[5] & 0xff) << 8);
				mHandler.post(new Runnable() {
					public void run() {
//						Toast.makeText(getContext(), "Yes "+adcSensorValue, Toast.LENGTH_SHORT).show();
						getData(adcSensorValue+"");
					}
				});
			}

		});

	}

	public void getData(String s) {
		points.add(conv((parseFloat(s) - 600) / 100.0));
		invalidate();
		if (ctr == 600 - 20)
			while (600 - 20 - points.size() < 10)
				points.removeFirst();
		else if (ctr > 600 - 20)
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
		++ctr;
		return new Point(1.24f*(ctr%600), (float)(height *(1-c)));
	}

	public String getCurrentCount() {
		return numberOfContractions+"";
	}
}
