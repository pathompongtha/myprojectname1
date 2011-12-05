package com.practice.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HelloWebView extends Activity {
    /** Called when the activity is first created. */
	
	WebView mWebView; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.loadUrl("http://www.google.com");
        mWebView.setWebViewClient(new HelloWebViewClient());
        
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if((keyCode == KeyEvent.KEYCODE_BACK) && (mWebView.canGoBack())) {
    		mWebView.goBack();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    private class HelloWebViewClient extends WebViewClient {
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		view.loadUrl(url);
    		return true;
    	}
    }
}