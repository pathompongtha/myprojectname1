package com.android.enai;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class FhrDialog extends Dialog {
	private String title;
	private TextView result;
	private Spinner spinner;
	private Button selected;
	private FHRAutoPlotView fhrView;
	private int position = 0;
	private boolean dismissed = false;
	
	public FhrDialog(Context context, String title, Button selected, FHRAutoPlotView fhrView) {
		super(context);
		this.title = title;
		this.selected = selected;
		this.fhrView = fhrView;
	}
	
	public String getResult() {
		if(result != null && spinner != null) {
			return result.getText()+" "+spinner.getItemAtPosition(position);
		}
		return ""; 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dismissed = false;
		setContentView(R.layout.dialog_fhr);
		setTitle(title);
		
		result = (TextView)findViewById(R.id.dialog_fhr_result);
		if(fhrView != null) {
			result.setText(fhrView.getCurrentReading()+"");
		}
		
		spinner = (Spinner)findViewById(R.id.dialog_fhr_spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long row) {
				position = pos;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}

		});

		
		((Button)findViewById(R.id.dialog_fhr_ok)).setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selected.setText(getResult());
				dismissed = true;
				dismiss();
			}
		});
		
		final Handler handler = new Handler();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while(!dismissed) {
					try {
						handler.post(new Runnable() {
							public void run() {
								result.setText(fhrView.getCurrentReading() + "");
							}
						});
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
}
