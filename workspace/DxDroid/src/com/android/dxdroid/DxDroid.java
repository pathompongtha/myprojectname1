package com.android.dxdroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DxDroid extends Activity implements OnClickListener {
	static int ctr = 0;
	private int adcSensorValue = 10;
	DrawView drawview;

	// Create TCP server (based on MicroBridge LightWeight Server).
	// Note: This Server runs in a separate thread.
	Server server = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.spo2_layout);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		setContentView(R.layout.main);
		
		RelativeLayout screen = (RelativeLayout)findViewById(R.id.screen);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width-20,height-20);
		
		drawview = new DrawView(this, height, width);
		params.setMargins(10, 10, 10, 10);
		screen.addView(drawview,params);
	
		Button plus = (Button)findViewById(R.id.buttonPlus);
		Button minus = (Button)findViewById(R.id.buttonMinus);
		
		plus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawview.zoom *= 1.5;
			}
		});

		minus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawview.zoom /= 1.5;
			}
		});
		
		// Create TCP server (based on MicroBridge LightWeight Server)
		try {
			server = new Server(4568); // Use the same port number used in
										// ADK Main Board firmware
			server.start();
		} catch (IOException e) {
			Log.e("Seeeduino ADK", "Unable to start TCP server", e);
			System.exit(-1);
		}

		server.addListener(new AbstractServerListener() {

			@Override
			public void onReceive(org.microbridge.server.Client client,
					byte[] data) {

				if (data.length < 2)
					return;
				adcSensorValue = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
				
				// Any update to UI can not be carried out in a non UI thread
				// like the one used
				// for Server. Hence runOnUIThread is used.
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new UpdateData().execute(adcSensorValue);
					}
				});

			}

		});

	} // End of TCP Server code

	// UpdateData Asynchronously sends the value received from ADK Main Board.
	// This is triggered by onReceive()
	class UpdateData extends AsyncTask<Integer, Integer, String> {
		// Called to initiate the background activity
		@Override
		protected String doInBackground(Integer... sensorValue) {

			// Init SeeekBar Widget to display ADC sensor value in SeekBar
			// Max value of SeekBar is set to 1024
			return (String.valueOf(sensorValue[0])); // This goes to result

		}

		// Called when there's a status to be updated
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// Not used in this case
		}

		// Called once the background activity has completed
		@Override
		protected void onPostExecute(String result) {
			// Init TextView Widget to display ADC sensor value in numeric.
//			TextView tvAdcvalue = (TextView) findViewById(R.id.SPO2HeartRateValue);
//			tvAdcvalue.setText(String.valueOf(result));
			TextView tvAdcvalue = (TextView) findViewById(R.id.textView1);
			try {
				String[] ss = {""+0.02*(ctr++),"0",result};
				tvAdcvalue.setText(String.format("%.3f: %.3f",Float.parseFloat(ss[0]),Float.parseFloat(ss[2])));
				drawview.getData(ss);
			} catch(Exception e) {
				tvAdcvalue.setText("ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
		}
	}

	// Called when the LED button is clicked
	@Override
	public void onClick(View v) {

	}

}
