package com.practice.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HelloFormStuff extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HelloFormStuff.this, "Beep bop", Toast.LENGTH_SHORT).show();
			}
		});
        
        final EditText edittext = (EditText)findViewById(R.id.edittext);
        edittext.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == KeyEvent.ACTION_DOWN &&
					keyCode == KeyEvent.KEYCODE_ENTER){
					Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_LONG).show();
					return true;
				}
				return false;
			}
		});
        
        final CheckBox checkbox = (CheckBox)findViewById(R.id.checkbox);
        checkbox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox)v).isChecked()) {
					Toast.makeText(HelloFormStuff.this, "Selected", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(HelloFormStuff.this, "Not selected", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        final RadioButton radio_red = (RadioButton)findViewById(R.id.radio_red);
        final RadioButton radio_blue = (RadioButton)findViewById(R.id.radio_blue);
        radio_red.setOnClickListener(radio_listener);
        radio_blue.setOnClickListener(radio_listener);
        
        final ToggleButton togglebutton = (ToggleButton)findViewById(R.id.togglebutton);
        togglebutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(togglebutton.isChecked()) {
					Toast.makeText(HelloFormStuff.this, "Vibrate on", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(HelloFormStuff.this, "Vibrate off", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        final RatingBar ratingbar = (RatingBar)findViewById(R.id.ratingbar);
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				// TODO Auto-generated method stub
				Toast.makeText(HelloFormStuff.this, "New Rating: "+rating, Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    private OnClickListener radio_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RadioButton rb = (RadioButton)v;
			Toast.makeText(HelloFormStuff.this, rb.getText(),Toast.LENGTH_SHORT).show();
		}
	};
}