package com.test.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Hello extends Activity {
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_layout);
        Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
    }
}