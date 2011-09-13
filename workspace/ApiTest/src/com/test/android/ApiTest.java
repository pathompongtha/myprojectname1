package com.test.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ApiTest extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button hbutton = (Button)findViewById(R.id.hbutton);
        hbutton.setOnClickListener(myOnClickListener);
    }
    
    private OnClickListener myOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startActivity(new Intent(ApiTest.this, Hello.class));
			overridePendingTransition(0,0);
		}
	};
}