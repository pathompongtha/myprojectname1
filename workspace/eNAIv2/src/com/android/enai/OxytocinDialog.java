package com.android.enai;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OxytocinDialog extends KeypadDialog {
	private Button selected;

	public OxytocinDialog(Context context, String title, Button selected) {
		super(context, title, selected);
		this.selected = selected; 
	}

	public OxytocinDialog(Context context, String title, Button selected, String start) {
		super(context, title, selected, start);
		this.selected = selected;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Button)findViewById(R.id.keypad_ok)).setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selected.setText(new SimpleDateFormat("HH:mm").format(new Date())+"/ "+getInput()+" ml");
				dismiss();
			}
		});
	}
}
