package android_programmers_guider.AndroidPhoneDialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AndroidPhoneDialer extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);
		final Button callButton = (Button) findViewById(R.id.callButton);
		callButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (validatePhoneNumber(phoneNumber.getText().toString())) {
					Intent CallIntent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + phoneNumber.getText()));
					CallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(CallIntent);
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
					builder.setMessage("Please enter a phone number in the X-XXX-XXX-XXXX format.");
					builder.setNeutralButton("Re-enter Number", null);
					AlertDialog alert = builder.create();
				}
			}
		});
	}

	public boolean validatePhoneNumber(String number) {
		return number.matches("\\d+-*\\d*-*\\d*-*\\d*");
	}
}