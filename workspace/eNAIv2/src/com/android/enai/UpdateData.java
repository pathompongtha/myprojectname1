package com.android.enai;

import android.os.AsyncTask;

public // UpdateData Asynchronously sends the value received from ADK Main Board.
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
		
	}
}
