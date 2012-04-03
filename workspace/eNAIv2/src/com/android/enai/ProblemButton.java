package com.android.enai;

import android.content.Context;
import android.widget.Button;

public class ProblemButton extends Button {
	private String content;
	
	public ProblemButton(Context context) {
		super(context);
	}
	
	public void setContent(String s) {
		content = s;
	}

	public String getContent() {
		return content;
	}
}
