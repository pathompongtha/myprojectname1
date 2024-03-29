package com.practice.android;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class HelloDatePicker extends Activity {

	private TextView mDateDisplay;
	private Button mPickDate;
	private int mYear, mMonth, mDay;
	
	static final int DATE_DIALOG_ID = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //capture our View elements
        mDateDisplay = (TextView)findViewById(R.id.dateDisplay);
        mPickDate = (Button)findViewById(R.id.pickDate);
        
        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
        
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        // display the current date
        updateDisplay();
    }
    
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case DATE_DIALOG_ID:
    		return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
    	}
    	return null;
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
    	new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				updateDisplay();
			}
		};
    
    private void updateDisplay() {
    	mDateDisplay.setText(new StringBuilder().append(mMonth+1).append("-")
    			.append(mDay).append("-").append(mYear).append(" "));
    }
}