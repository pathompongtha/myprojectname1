package com.android.enai;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class UcDialog extends Dialog {
	private String title;
	private TextView result;
	private Button selected;
	private UCAutoPlotView ucView;
	private int position = 0;
	
	public UcDialog(Context context, String title, Button selected, UCAutoPlotView ucView) {
		super(context);
		this.title = title;
		this.selected = selected;
		this.ucView = ucView;
	}
	
	public String getResult() {
		if(result != null) {
			return result.getText().toString();
		}
		return ""; 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_uc);
		setTitle(title);
		
		result = (TextView)findViewById(R.id.dialog_uc_result);
		if(ucView != null) {
			result.setText(ucView.getCurrentCount()+"");
		}
		
		((Button)findViewById(R.id.dialog_uc_ok)).setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selected.setText(getResult());
				dismiss();
			}
		});
	}
}
