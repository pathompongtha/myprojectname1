package com.android.enai;

import static java.lang.Float.parseFloat;

import java.io.IOException;
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
	private LinkedList<Point> points;
	private Paint paint;
	private int height, width, off;
	private int ctr = 0;
	private int adcSensorValue = 10;
	private int tocoDuration = 0;
	private TocoTableActivity tocoTableActivity;
	private Date time = new Date();
	
	// Create TCP server (based on MicroBridge LightWeight Server).
	// Note: This Server runs in a separate thread.
	Server server = null;

	public UCAutoPlotView(Context context, int h, int w, int resource, Thread thread, Button tocoZeroButton) {
		super(context);
		
		tocoTableActivity = new TocoTableActivity();
		tocoTableActivity.init();
//		tocoTableActivity.add("a","b");
		
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
		
		// Create TCP server (based on MicroBridge LightWeight Server)
		try {
			server = new Server(4568); // Use the same port number used in
										// ADK Main Board firmware
			server.start();
//			Log.d("UC Data", "Server OK");
			Toast.makeText(getContext(), "Server OK", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Log.e("Seeeduino ADK", "Unable to start TCP server", e);
			System.exit(-1);
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
                int startFlag = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
                if(startFlag==0xfb){                    
                    int dataId = (data[2] & 0xff) | ((data[3] & 0xff) << 8);
                    if(dataId==0xa1){                        
                        adcSensorValue = (data[4] & 0xff) | ((data[5] & 0xff) << 8);
                        tocoDuration = (data[6] & 0xff) | ((data[7] & 0xff) << 8);
                        if (tocoDuration!=0){
//                            adcSensorValue = tocoDuration;
                            Time timestamp = new Time(time.getTime());//-tocoDuration*1000);
                            tocoTableActivity.add(timestamp+"", tocoDuration+"");
                        }
                    }                                
                }
				mHandler.post(new Runnable() {
					public void run() {
						getData(adcSensorValue+"");
					}
				});
			}

		});

	}

	public void getData(String s) {
		points.add(conv((parseFloat(s) - 630) / 100.0));
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
		return new Point((ctr+=3) % width + off / 2, (float)(height *(1-c)));
	}
}
