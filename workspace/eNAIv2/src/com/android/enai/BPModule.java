package com.android.enai;

import java.io.IOException;
import java.net.BindException;
import java.util.Arrays;
import java.util.HashMap;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BPModule extends Activity {
	public static int maxPressure = 140;
	public static Server server = null;
	Context mContext;
	private Handler mHandler;
	private ProgressBar pBar;
	private Dialog dialog;
	private TextView bpResult;
	private TextView statusMessage;
	private Button okButton;
	private HashMap<Integer, Byte> invMap = new HashMap<Integer, Byte>();
	private static HashMap<Integer, String> statusMap = new HashMap<Integer, String>();
	
	static {
		server = new Server(4569);
		try {
			server.start();
		} catch (BindException e) {
			Log.e("Seeeduino ADK", "BP Server BindException", e);
		} catch (IOException e) {
			Log.e("Seeeduino ADK", "Unable to start TCP server", e);
		}
	}

	BPModule(Context context, Dialog dialog) {
		mContext = context;
		this.dialog = dialog;

		invMap.put(240, (byte) 0);
		invMap.put(220, (byte) 1);
		invMap.put(200, (byte) 2);
		invMap.put(180, (byte) 3);
		invMap.put(160, (byte) 4);
		invMap.put(140, (byte) 5);

		statusMap.put(1, "BP Acquired");
		statusMap.put(2, "Error");
		statusMap.put(6, "Check Cuff Connection\nLoose connection");
		statusMap.put(7, "Check Cuff Connection\nLeakage occured");
		statusMap.put(9, "Measuring Time Exceeded:\nLess Oscillations Detect");
		statusMap.put(11, "Patient Needs to Relax\nToo much movement");
		statusMap.put(12, "Maximum Pressure Exceeded");
		statusMap.put(15, "Module Error:\nCall eNAI Hotline");

		if (dialog != null) {
			bpResult = (TextView) dialog.findViewById(R.id.dialog_bp_result);
			statusMessage = (TextView) dialog.findViewById(R.id.dialog_bp_status);
			okButton = (Button) dialog.findViewById(R.id.dialog_bp_done);
		}
		if (server == null) {

		}

		mHandler = new Handler();
		server.addListener(new AbstractServerListener() {

			@Override
			public void onReceive(org.microbridge.server.Client client,
					byte[] data) {
				Log.d("BP Data", data.length + " " + Arrays.toString(data));
				if (data.length < 2)
					return;
				final int value = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
				mHandler.post(new Runnable() {
					public void run() {
						pBar.setProgress(value);
					}
				});

				if (data.length > 2) {
					final int sys = (data[2] & 0xff) | ((data[3] & 0xff) << 8);
					final int dias = (data[4] & 0xff) | ((data[5] & 0xff) << 8);
					mHandler.post(new Runnable() {
						public void run() {
							bpResult.setText(sys + "/ " + dias);
							if(okButton != null) {
								okButton.setEnabled(true);
							}
						}
					});
				}
				if (data.length > 6) {
					final int statusCode = (data[6] & 0xff) | ((data[7] & 0xff) << 8);
					mHandler.post(new Runnable() {
						public void run() {
							statusMessage.setText(statusMap.get(statusCode));
						}
					});
				}
			}
		});

	}

	public void start() {

		Log.d("BPModule", "Max pressure set to " + maxPressure);
		try {
			server.send(new byte[] { 1, invMap.get(maxPressure) });
			Toast.makeText(mContext, "sent", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(mContext,
					"not sent with exception " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void stop() {
		try {
			server.send(new byte[] {2});
			Toast.makeText(mContext, "sent", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(mContext,
					"not sent with exception " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void setMaxPressure(int p) {
		maxPressure = p;
		pBar.setMax(p);
	}

	public void setComponents(ProgressBar pb, TextView result, TextView status) {
		pBar = pb;
		bpResult = result;
		statusMessage = status;
	}
}
