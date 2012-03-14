package com.android.enai;

import java.util.LinkedList;
import java.util.List;

import javax.swing.text.html.parser.Element;

import org.w3c.dom.html.HTMLElement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowDetailsActivity extends ListActivity {

	private static final int DIALOG_FETCHING_DATA = 0;
	private static final int DIALOG_NO_INTERNET = 2;
	private Handler handler;
	private WebView webview;
//	private TextView mainTextView;
	private TextView visitDetailsView;
	private int idx = 0;
	private int jdx = 0;
	private int jdxn = -1;
	private String[] visitLinks;
	PatientDetailsListAdapter patientDetails;
	VisitDetailsListAdapter visitDetails;
	private List<String> patientDetailsList;
	private List<VisitDetails> visitDetailsList;
	private ProgressDialog fetchingDataProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		handler = new Handler();

		setContentView(R.layout.records_view);
		
		showDialog(DIALOG_FETCHING_DATA);
		
		if(patientDetailsList == null) {
			patientDetailsList = new LinkedList<String>();
		}
		
		if(visitDetailsList == null) {
			visitDetailsList = new LinkedList<VisitDetails>();
		}

//		mainTextView = (TextView) findViewById(R.id.visit_details_text);
		visitDetailsView = (TextView) findViewById(R.id.visit_details_text);

		Button okButton = (Button) findViewById(R.id.details_ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						PartographActivity.class);
				intent.putExtra("name", patientDetailsList.get(0).split(": ")[1]);
				intent.putExtra("age", patientDetailsList.get(1).split(": ")[1]);
				intent.putExtra("birthday", patientDetailsList.get(3).split(": ")[1]);
				intent.putExtra("patientId", patientDetailsList.get(4).split(": ")[1]);
				
				startActivity(intent);
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.details_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});

		Intent fromIntent = getIntent();
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.setVisibility(View.GONE);
		webview.addJavascriptInterface(new JavaScriptInterface(this), "js");

		webview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.i("DEBUG", "page finished: " + url +" idx = "+ idx);
				if(idx == 0) view.loadUrl(urls[idx]);
				if(idx == 5) view.loadUrl(urls[idx]);
				if(idx == 7) {
//					webview.setVisibility(View.VISIBLE);
					view.loadUrl(urls[idx]);
				}
			
			}
		});
		
		webview.setWebChromeClient(new WebChromeClient(){
			
			@Override
			public boolean onJsAlert(WebView view, String url, final String message,
					JsResult result) {
				result.confirm();
//				final int i = idx;
//				handler.post(new Runnable() {
//					public void run() {
//						mainTextView.append(keys[i]+" = "+message+ "\n");
//					}
//				});
				
				if(idx == 4) {
					if(!message.equals("none")) {
						++idx;
						webview.loadUrl(message);
					} else {
						updateView();
					}
				} else if(idx == 5) {
					jdxn = Integer.parseInt(message);
					++idx;
					jdx = 0;
					webview.loadUrl(urls[idx]);
					visitLinks = new String[jdxn];
				} else if(idx == 6) {
					visitLinks[jdx] = message;
					++jdx;
					if(jdx == jdxn) {
						++idx;
						jdx = 0;
//						handler.post(new Runnable() {
//							public void run() {
//								mainTextView.append("\n\n");
//							}
//						});
						webview.loadUrl(visitLinks[jdx]);
					}
				} else if(idx == 7) {
					visitDetailsList.add(new VisitDetails(message));
					++jdx;
					if(jdx < jdxn) {
						webview.loadUrl(visitLinks[jdx]);
					} else {
						updateView();
					}
				}
				return true;
			}
		});

		if(isOnline()) {
			webview.loadUrl(fromIntent.getStringExtra("url"));
		} else {
			showDialog(DIALOG_NO_INTERNET);
		}

		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_FETCHING_DATA: {
			fetchingDataProgressDialog = new ProgressDialog(this);
			// uploadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			fetchingDataProgressDialog.setMessage("Fetching Data... Please wait.");
			fetchingDataProgressDialog.setCancelable(false);

			return fetchingDataProgressDialog;
		}
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		visitDetailsView.setText(visitDetailsList.get(position-7).fullData);
		int childCount = l.getChildCount();
		for(int i=0;i<childCount;i++) {
			View subView = l.getChildAt(i);
			if(subView.findViewById(R.id.icon) != null) {
				ImageView rightArrow = ((ImageView)subView.findViewById(R.id.icon));
				rightArrow.setImageResource(R.drawable.right_arrow);
			}
		}
		ImageView rightArrow = ((ImageView)v.findViewById(R.id.icon));
		rightArrow.setImageResource(R.drawable.right_arrow_click);
	}
	
	private void updateView() {
		VisitDetailsSectionListAdapter sectionList = new VisitDetailsSectionListAdapter(getApplicationContext());

		patientDetails = new PatientDetailsListAdapter(this, patientDetailsList);
		sectionList.addSection("Patient Details", patientDetails);

		visitDetails = new VisitDetailsListAdapter(this, visitDetailsList);
		sectionList.addSection("Visit Details", visitDetails);

		setListAdapter(sectionList);
		removeDialog(DIALOG_FETCHING_DATA);
	}

	private static final String[] urls = {
		"javascript:window.js.save(document.getElementsByClassName('library')[0].innerText);",
		"javascript:window.js.save(document.getElementsByTagName('table')[7].getElementsByTagName('b')[1].innerText);",
		"javascript:window.js.save(document.getElementsByTagName('table')[7].getElementsByTagName('b')[2].innerText);",
		"javascript:window.js.save(document.getElementsByTagName('table')[7].getElementsByTagName('b')[3].innerText);",
		"javascript:{var entries = document.getElementsByTagName('td');var found = false;var len = entries.length;var href = null;for(i=0;i<len;i++) {var cell = entries[i].getElementsByTagName('a');var cellLen = cell.length;for(j=0;j<cellLen;j++) {var text = cell[j].innerText;if(text == 'Maternal Care') {found = true;href = cell[j].href;}}}if(found) alert(href);else alert('none');};",
		"javascript:{var entries = document.getElementsByTagName('td');var found = false;for(i=0;i<entries.length;i++) {for(j=0;j<entries[i].childNodes.length;j++) {var child = entries[i].childNodes[j];if(child.tagName == 'B' && child.innerText == 'MATERNAL REGISTRY RECORDS') {alert(entries[i].getElementsByTagName('a').length);found = true;}}}if(!found) alert(0);}",
		"javascript:{var entries = document.getElementsByTagName('td');var found = false;for(i=0;i<entries.length;i++) {for(j=0;j<entries[i].childNodes.length;j++) {var child = entries[i].childNodes[j];if(child.tagName == 'B' && child.innerText == 'MATERNAL REGISTRY RECORDS') {var visits = entries[i].getElementsByTagName('a');for(k=0;k<visits.length;k++) {alert(visits[k].href);}}}}}",
		"javascript:{alert(document.getElementsByClassName('tinylight')[41].innerText)};",
		};
	
	private static final String[] keys = {
		"Name", 
		"Age/Sex",
		"Birthday",
		"Patient ID",
		"has maternal care",
		"number of maternal care visits",
		"visit #",
		"link #"
	};

	public class JavaScriptInterface {
		Context mContext;

		public JavaScriptInterface(Context context) {
			mContext = context;
		}
		
		public void save(final String result) {
			final int i = idx;
			handler.post(new Runnable() {
				public void run() {
					switch(i) {
					default:
//						mainTextView.append(keys[i]+" = "+result+"\n");
						if(i != 1) patientDetailsList.add(keys[i]+": "+result);
						else {
							final String[] arr = result.split("/");
							patientDetailsList.add("Age: "+arr[0]);
							patientDetailsList.add("Sex: "+arr[1]);
						}
					}
				}
			});
			if(++idx < urls.length)
				webview.loadUrl(urls[idx]);
		}
		
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected())
			return true;
		return false;
	}

}
