package com.android.enai;

import javax.swing.text.html.parser.Element;

import org.w3c.dom.html.HTMLElement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

public class ShowDetailsActivity extends Activity {

	private Handler handler;
	private WebView webview;
	private TextView tv;
	private int idx = 0;
	private int jdx = 0;
	private int jdxn = -1;
	private String[] visitLinks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		handler = new Handler();

		setContentView(R.layout.chits_view);

		tv = (TextView) findViewById(R.id.details_text);

		Button okButton = (Button) findViewById(R.id.details_ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						PartographActivity.class);
				startActivity(intent);
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
				Log.i("DEBUG", "page finished: " + url);
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
				final int i = idx;
				handler.post(new Runnable() {
					public void run() {
						tv.append(keys[i]+" = "+message+ "\n");
					}
				});
				
				if(idx == 4 && !message.equals("no")) {
					++idx;
					webview.loadUrl(message);
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
						handler.post(new Runnable() {
							public void run() {
								tv.append("\n\n");
							}
						});
						webview.loadUrl(visitLinks[jdx]);
					}
				} else if(idx == 7) {
					++jdx;
					if(jdx < jdxn) {
						webview.loadUrl(visitLinks[jdx]);
					}
				}
				return true;
			}
		});

		webview.loadUrl(fromIntent.getStringExtra("url"));

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
		"name", 
		"age/sex",
		"birthday",
		"patient ID",
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
						tv.append(keys[i]+" = "+result+"\n");
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
}
