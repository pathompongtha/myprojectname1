package com.android.enai;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ProblemDialog extends Dialog {
	private String title;
	private ProblemButton selected;
	private String start;

	public ProblemDialog(Context context, String title, ProblemButton selected, String start) {
		super(context);
		this.title = title;
		this.selected = selected;
		this.start = start;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_problem);
		
		setTitle(title);
		
		final EditText input = (EditText)findViewById(R.id.dialog_problem_input);
		input.setText(start);
		
		Button okButton = (Button)findViewById(R.id.dialog_problem_ok);
		okButton.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String s = input.getText().toString();
				if(s == null || s.length() == 0) {
					selected.setText("");
					selected.setContent("");
				} else {
					selected.setText("O");	
					selected.setContent(s);
				}
				dismiss();
			}
		});
	}

}
