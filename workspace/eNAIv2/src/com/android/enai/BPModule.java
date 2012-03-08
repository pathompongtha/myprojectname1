package com.android.enai;

import java.io.IOException;

import org.microbridge.server.Server;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class BPModule extends Activity {
	public static int maxPressure = 180;
	Server server = null;
	Context mContext;

	BPModule(Context context) {
		mContext = context;
		try {
			server = new Server(4570); // Use the same port number used in
										// ADK Main Board firmware
			server.start();
//			Log.d("UC Data", "Server OK");
			Toast.makeText(mContext, "Server OK", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Log.e("Seeeduino ADK", "Unable to start TCP server", e);
			System.exit(-1);
		}
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
	}
}
