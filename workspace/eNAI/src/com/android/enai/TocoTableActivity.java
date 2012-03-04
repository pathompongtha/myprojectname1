package com.android.enai;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TocoTableActivity extends Activity {
	private static ArrayList<String> times;
	private static ArrayList<String> durs;
	private TableLayout tbl;
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tocotable);
        tbl = (TableLayout)findViewById(R.id.tocoTableLayout);
        if(times == null) times = new ArrayList<String>();
        if(durs == null) durs = new ArrayList<String>();
        show();
    }
	
	public void init() {
        if(times == null) times = new ArrayList<String>();
        if(durs == null) durs = new ArrayList<String>();
//        add("hello","world");
	}
	
	public void add(String time, String duration) {
		times.add(time);
		durs.add(duration);
	}
	
	public void show() {
		for(int i=0;i<times.size();i++) {
			String time = times.get(i);
			String duration = durs.get(i);
			TableRow newRow = new TableRow(this);
			
			TextView tvTime = new TextView(this);
			tvTime.setGravity(Gravity.CENTER_HORIZONTAL);
			tvTime.setText(time);
			newRow.addView(tvTime);
			
			TextView tvDur = new TextView(this);
			tvDur.setGravity(Gravity.CENTER_HORIZONTAL);
			tvDur.setText(duration);
			newRow.addView(tvDur);
			
			tbl.addView(newRow);	
		}
	}
}