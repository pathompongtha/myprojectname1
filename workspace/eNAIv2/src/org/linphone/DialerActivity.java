/*
DialerActivity.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.linphone;

import org.linphone.LinphoneService.LinphoneGuiListener;
import org.linphone.core.LinphoneCall;
import org.linphone.core.Log;
import org.linphone.core.LinphoneCall.State;
import org.linphone.ui.AddressAware;
import org.linphone.ui.AddressText;
import org.linphone.ui.CallButton;
import org.linphone.ui.EraseButton;

import com.android.enai.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Dialer and main activity of Linphone Android.
 * 
 * Roles are:<ul>
 * <li>Display the numpad, call/accept, address buttons</li>
 * <li>Define preferences through the menu</li>
 * <li>React to bad preference / no account set</li>
 * <li>Manage first launch</li>
 * </ul>
 *
 */
public class DialerActivity extends Activity implements LinphoneGuiListener {
	
	private TextView mStatus;

	private AddressText mAddress;
	private CallButton mCall;

	private static DialerActivity instance;
	private boolean mPreventDoubleCallOnRotation;
	
	private static final String CURRENT_ADDRESS = "org.linphone.current-address"; 
	private static final String CURRENT_DISPLAYNAME = "org.linphone.current-displayname";
	private static final String PREVENT_DOUBLE_CALL = "prevent_call_on_phone_rotation";

	/**
	 * @return null if not ready yet
	 */
	public static DialerActivity instance() { 
		return instance;
	}

	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialer);

		mAddress = (AddressText) findViewById(R.id.SipUri); 
		((EraseButton) findViewById(R.id.Erase)).setAddressWidget(mAddress);
		
		// TODO: put comments
		// hardcoded address of recepient 
//		mAddress.setText("1001@one.telehealth.ph");
		mAddress.setText("toto@10.158.4.52");


		mCall = (CallButton) findViewById(R.id.Call);
		mCall.setAddressWidget(mAddress);

		mStatus =  (TextView) findViewById(R.id.status_label);

		AddressAware numpad = (AddressAware) findViewById(R.id.Dialer);
		if (numpad != null)
			numpad.setAddressWidget(mAddress);

		// call to super must be done after all fields are initialized
		// because it may call this.enterIncallMode
		super.onCreate(savedInstanceState);

		mPreventDoubleCallOnRotation=savedInstanceState != null
				&& savedInstanceState.getBoolean(PREVENT_DOUBLE_CALL, false);
		if (mPreventDoubleCallOnRotation) {
			Log.i("Prevent launching a new call on rotation");
		} else {
			checkIfOutgoingCallIntentReceived();
		}

		instance = this;
		super.onCreate(savedInstanceState);
	}


    private void checkIfOutgoingCallIntentReceived() {
    	if (getIntent().getData() == null) return;

    	if (!LinphoneService.isReady() || LinphoneManager.getLc().isIncall()) {
    		Log.w("Outgoing call aborted as LinphoneService"
    				+ " is not ready or we are already in call");
    		return;
    	}
    	
    	// Fix call from contact issue
    	if (getIntent().getData().getSchemeSpecificPart() != null)
    		getIntent().setAction(Intent.ACTION_CALL);
    	
    	newOutgoingCall(getIntent());
	}






	

	


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putCharSequence(CURRENT_ADDRESS, mAddress.getText());
		if (mAddress.getDisplayedName() != null)
			savedInstanceState.putString(CURRENT_DISPLAYNAME,mAddress.getDisplayedName());
		savedInstanceState.putBoolean(PREVENT_DOUBLE_CALL, mPreventDoubleCallOnRotation);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		CharSequence addr = savedState.getCharSequence(CURRENT_ADDRESS);
		if (addr != null && mAddress != null) {
			mAddress.setText(addr); 
		}
		mAddress.setDisplayedName(savedState.getString(CURRENT_DISPLAYNAME));
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance=null;
	}





	public void newOutgoingCall(Intent intent) {
		String scheme = intent.getData().getScheme();
		if (scheme.startsWith("imto")) {
			mAddress.setText("sip:" + intent.getData().getLastPathSegment());
		} else if (scheme.startsWith("call") || scheme.startsWith("sip")) {
			mAddress.setText(intent.getData().getSchemeSpecificPart());
		} else {
			Log.e("Unknown scheme: ",scheme);
			mAddress.setText(intent.getData().getSchemeSpecificPart());
		}

		mAddress.clearDisplayedName();
		intent.setData(null);
		// Setting data to null is no use when the activity is recreated
		// as the intent is immutable.
		// https://groups.google.com/forum/#!topic/android-developers/vrLdM5mKeoY
		mPreventDoubleCallOnRotation=true;
		setIntent(intent);

		LinphoneManager.getInstance().newOutgoingCall(mAddress);
	}

	
	public void setContactAddress(String aContact,String aDisplayName, Uri photo) {
		mAddress.setText(aContact);
		mAddress.setDisplayedName(aDisplayName);
		mAddress.setPictureUri(photo);
	}

	


	
	
	/***** GUI delegates for listener LinphoneServiceListener *************/
	@Override
	public void onDisplayStatus(String message) {
		mStatus.setText(message);
	}

	@Override
	public void onAlreadyInCall() {
		showToast(R.string.warning_already_incall);
	}

	@Override
	public void onCannotGetCallParameters() { 
		showToast(R.string.error_cannot_get_call_parameters,mAddress.getText());
	}

	@Override
	public void onWrongDestinationAddress() {
		showToast(R.string.warning_wrong_destination_address, mAddress.getText());
	}

	private void showToast(int id, String txt) {
		final String msg = String.format(getString(id), txt);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	private void showToast(int id, CharSequence txt) {
		showToast(id, txt.toString());
	}
	private void showToast(int id) {
		Toast.makeText(this, getString(id), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCallStateChanged(LinphoneCall call, State s, String m) {}
	
	public void onGlobalStateChangedToOn(String message) {
		mCall.setEnabled(!LinphoneManager.getLc().isIncall());

		if (getIntent().getData() != null) {
			checkIfOutgoingCallIntentReceived();
		}
	}

	@Override
	protected void onResume() {
		// When coming back from a video call, if the phone orientation is different
		// Android will destroy the previous Dialer and create a new one.
		// Unfortunately the "call end" status event is received in the meanwhile
		// and set to the to be destroyed Dialer.
		// Note1: We wait as long as possible before setting the last message.
		// Note2: Linphone service is in charge of instantiating LinphoneManager
		mStatus.setText(LinphoneManager.getInstance().getLastLcStatusMessage());

		super.onResume();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (LinphoneUtils.onKeyVolumeSoftAdjust(keyCode)) return true;
		return super.onKeyDown(keyCode, event);
	}

}
