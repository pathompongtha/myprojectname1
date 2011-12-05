package com.android.RxSerial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class IntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_UMS_CONNECTED)) {
			Toast.makeText(context, "mounted", Toast.LENGTH_LONG).show();
			Intent myStarterIntent = new Intent(context, RxSerial.class);
			myStarterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(myStarterIntent);
		}
	}
}