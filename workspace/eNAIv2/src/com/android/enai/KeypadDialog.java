package com.android.enai;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class KeypadDialog extends Dialog {
	private String title;
	private TextView input;
	private Button selected;
	private String start;
	
	public KeypadDialog(Context context, String title, Button selected) {
		super(context);
		this.title = title;
		this.selected = selected;
	}
	
	public KeypadDialog(Context context, String title, Button selected, String start) {
		super(context);
		this.title = title;
		this.selected = selected;
		this.start = start;
	}
	
	public String getInput() {
		return input.getText().toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_keypad);
		setTitle(title);
		
		input = (TextView)findViewById(R.id.input);
		if(start != null) {
			input.setText(start);
		}

		((Button)findViewById(R.id.button1)).setOnClickListener(new NumberOnClickListener("1"));
		((Button)findViewById(R.id.button2)).setOnClickListener(new NumberOnClickListener("2"));
		((Button)findViewById(R.id.button3)).setOnClickListener(new NumberOnClickListener("3"));
		((Button)findViewById(R.id.button4)).setOnClickListener(new NumberOnClickListener("4"));
		((Button)findViewById(R.id.button5)).setOnClickListener(new NumberOnClickListener("5"));
		((Button)findViewById(R.id.button6)).setOnClickListener(new NumberOnClickListener("6"));
		((Button)findViewById(R.id.button7)).setOnClickListener(new NumberOnClickListener("7"));
		((Button)findViewById(R.id.button8)).setOnClickListener(new NumberOnClickListener("8"));
		((Button)findViewById(R.id.button9)).setOnClickListener(new NumberOnClickListener("9"));
		((Button)findViewById(R.id.button0)).setOnClickListener(new NumberOnClickListener("0"));
		((Button)findViewById(R.id.buttonPeriod)).setOnClickListener(new NumberOnClickListener("."));
		((Button)findViewById(R.id.buttonSlash)).setOnClickListener(new NumberOnClickListener("/"));
		
		((Button)findViewById(R.id.keypad_clear)).setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				input.setText("");
			}
		});
		
		((Button)findViewById(R.id.keypad_ok)).setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selected.setText(getInput());
				dismiss();
			}
		});
	}
	
	class NumberOnClickListener implements android.view.View.OnClickListener {
		String toPrint;
		
		public NumberOnClickListener(String s) {
			toPrint = s;
		}
		
		@Override
		public void onClick(View v) {
			input.append(toPrint);
		}
		
	}
}
