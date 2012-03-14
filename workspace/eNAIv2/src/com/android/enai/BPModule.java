package com.android.enai;

import java.io.IOException;
import java.util.Arrays;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BPModule extends Activity {
	public static int maxPressure = 180;
	Server server = null;
	Context mContext;
	private Handler mHandler;
	private ProgressBar pBar;
	
	BPModule(Context context) {
		mContext = context;
		try {
			server = new Server(4569); // Use the same port number used in
										// ADK Main Board firmware
			server.start();
//			Log.d("UC Data", "Server OK");
			Toast.makeText(mContext, "Server OK", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Log.e("Seeeduino ADK", "Unable to start TCP server", e);
			System.exit(-1);
		}
		
		mHandler = new Handler();
		server.addListener(new AbstractServerListener() {

            @Override
            public void onReceive(org.microbridge.server.Client client,
                    byte[] data) {
            	Log.d("UC Data", data.length+" "+Arrays.toString(data));
                if (data.length < 2)
                    return;
                final int value = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
                
				mHandler.post(new Runnable() {
					public void run() {
		                pBar.setProgress(value);
					}
				});
			}
		});

	}
	
	public void start() {
		//TODO: this is for Nat
//		Toast.makeText(getApplicationContext(), "test "+maxPressure, Toast.LENGTH_LONG).show();
		
		Log.d("BPModule", "Max pressure set to "+maxPressure);
		try {
			server.send(new byte[]{1});
			Toast.makeText(mContext, "sent", Toast.LENGTH_SHORT).show();
		} catch(Exception e){
			e.printStackTrace();
			Toast.makeText(mContext, "not sent with exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setMaxPressure(int p) {
		maxPressure = p;
		pBar.setMax(p);
	}
	
	public void setProgressBar(ProgressBar pb) {
		pBar = pb;
	}
}
