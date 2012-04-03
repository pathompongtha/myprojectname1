package com.android.enai;

import com.bugsense.trace.BugSenseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ENAIActivity extends Activity {
	/***
		This is the first activity to be called by the app
		This loads the CHITS homepage (demo2010.chits.ph)
		This also attempts automatic login (user/user)
		At the patient details page, after pressing 'menu' and 'eNAI', the activity will call ShowDetailsActitivy
		
		When the device has no internet connection, it also prompts the user of the state.
		After pressing 'menu' and 'eNAI', the view will be redirected to the partograph view
	***/

	private static final int DIALOG_NO_INTERNET = 0;

	private String[] urls = {
		"https://demo2010.chits.ph/info/index.php",
		"javascript: {var x = document.getElementsByName('login')[0]; x.value ='user'; var x = document.getElementsByName('passwd')[0]; x.value ='user'; var x = document.getElementsByName('submitlogin')[0]; x.click(); };",
		"https://demo2010.chits.ph/info/index.php?page=PATIENTS&menu_id=657"	
	};
	
	private static boolean loginDone = false;
	private WebView webview;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chits_view);

		BugSenseHandler.setup(this, "fbd29224");
		
		intent = new Intent(this, PartographActivity.class);

		webview = (WebView) findViewById(R.id.webview);

		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		
		final Activity activity = this;
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				activity.setProgress(progress * 1000);
			}
		});
		

		webview.setWebViewClient(new WebViewClient() {

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// Users will be notified in case there's an error (i.e. no
				// internet connection)
				Toast.makeText(activity, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				if(urls[0].equals(url)) {
					if(!loginDone) {
						loginDone = true;
						view.loadUrl(urls[1]);
					}
				}
			}

			@Override
			public void onReceivedHttpAuthRequest(WebView view,
					HttpAuthHandler handler, String host, String realm) {
				// TODO Auto-generated method stub
				super.onReceivedHttpAuthRequest(view, handler, host, realm);
				Log.d("DEBUG", "onREceivedHttpAuthRequest " + host + " "
						+ realm);
			}
		});

		if(isOnline()) {
			webview.loadUrl(urls[0]);
		} else {
			showDialog(DIALOG_NO_INTERNET);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_NO_INTERNET: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("No Internet Connection");
			builder.setMessage("This app needs internet to fetch data");
			return builder.create();
		}
		}
		return null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "eNAI");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: {

			intent = new Intent(getApplicationContext(), ShowDetailsActivity.class);
			intent.putExtra("url", webview.getOriginalUrl());
			startActivity(intent);
			return true;
		}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
	}
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected())
			return true;
		return false;
	}

}
