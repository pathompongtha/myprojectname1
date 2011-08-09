package com.practice.android;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class HelloTimePicker extends Activity {
	
	private TextView mTimeDisplay;
	private Button mPickTime;
	private int mHour, mMinute;
	
	static final int TIME_DIALOG_ID = 0; 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTimeDisplay = (TextView)findViewById(R.id.timeDisplay);
        mPickTime = (Button)findViewById(R.id.pickDate);
        
        mPickTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});
        
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        updateDisplay();
    }
    
    private void updateDisplay() {
    	mTimeDisplay.setText(new StringBuilder().append(pad(mHour))
    			.append(":").append(pad(mMinute)));
    }
    
    private String pad(int c) {
    	if(c >= 10) return String.valueOf(c);
    	else return "0"+String.valueOf(c);
    }
    
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = 
    	new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				mHour = hourOfDay;
				mMinute = minute;
				updateDisplay();
			}
		};
    
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this,mTimeSetListener,mHour,mMinute,false);
		}
		return null;
	}
}