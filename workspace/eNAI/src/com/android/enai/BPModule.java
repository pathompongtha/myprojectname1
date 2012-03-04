package com.android.enai;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class BPModule extends Activity {
	public static int maxPressure = 180;
	
	BPModule() {}
	
	public void start() {
		//TODO: this is for Nat
//		Toast.makeText(getApplicationContext(), "test "+maxPressure, Toast.LENGTH_LONG).show();
		Log.d("BPModule", "Max pressure set to "+maxPressure);
	}
	
	public void setMaxPressure(int p) {
		maxPressure = p;
	}
}
