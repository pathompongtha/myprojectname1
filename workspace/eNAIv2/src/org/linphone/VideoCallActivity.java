/*
VideoCallActivity.java
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



import org.linphone.LinphoneSimpleListener.LinphoneOnCallStateChangedListener;
import org.linphone.core.LinphoneCall;
import org.linphone.core.Log;
import org.linphone.core.LinphoneCall.State;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import com.android.enai.R;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

/**
 * For Android SDK >= 5
 * @author Guillaume Beraudo
 *
 */
public class VideoCallActivity extends Activity implements LinphoneOnCallStateChangedListener {
	private SurfaceView mVideoViewReady;
	private SurfaceView mVideoCaptureViewReady;
	public static boolean launched = false;
	private LinphoneCall videoCall;
	private WakeLock mWakeLock;
	private Handler refreshHandler = new Handler();
	
	AndroidVideoWindowImpl androidVideoWindowImpl;
	private Runnable mCallQualityUpdater;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LinphoneManager.isInstanciated() || LinphoneManager.getLc().getCallsNb() == 0) {
			Log.e("No service running: avoid crash by finishing ", getClass().getName());
			// super.onCreate called earlier
			finish();
			return;
		}

		setContentView(R.layout.videocall);

		SurfaceView videoView = (SurfaceView) findViewById(R.id.video_surface); 

		//((FrameLayout) findViewById(R.id.video_frame)).bringChildToFront(findViewById(R.id.imageView1));
		
		SurfaceView captureView = (SurfaceView) findViewById(R.id.video_capture_surface);
		captureView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		/* force surfaces Z ordering */
		fixZOrder(videoView, captureView);
	
		androidVideoWindowImpl = new AndroidVideoWindowImpl(videoView, captureView);
		androidVideoWindowImpl.setListener(new AndroidVideoWindowImpl.VideoWindowListener() {
			
			public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
				LinphoneManager.getLc().setVideoWindow(vw);
				mVideoViewReady = surface;
			}
			
			public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {
				Log.d("VIDEO WINDOW destroyed!\n");
				LinphoneManager.getLc().setVideoWindow(null);
			}
			
			public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
				mVideoCaptureViewReady = surface;
				LinphoneManager.getLc().setPreviewWindow(mVideoCaptureViewReady);
			}
			
			public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {
				// Remove references kept in jni code and restart camera
				// LinphoneManager.getLc().setPreviewWindow(null);
				// Commented to remove flicker.
			}
		});
		
		androidVideoWindowImpl.init();
		
		videoCall = LinphoneManager.getLc().getCurrentCall();
		if (videoCall != null) {
			updatePreview(videoCall.cameraEnabled());
		}

			
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,Log.TAG);
		mWakeLock.acquire();
		
	}
	
	void updateQualityOfSignalIcon(float quality)
	{
		ImageView qos = (ImageView) findViewById(R.id.QoS);
		if (quality >= 4) // Good Quality
		{
			qos.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_signal_4));
		}
		else if (quality >= 3) // Average quality
		{
			qos.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_signal_3));
		}
		else if (quality >= 2) // Low quality
		{
			qos.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_signal_2));
		}
		else if (quality >= 1) // Very low quality
		{
			qos.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_signal_1));
		}
		else // Worst quality
		{
			qos.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_signal_0));
		}
	}
	
	void updatePreview(boolean cameraCaptureEnabled) {
		mVideoCaptureViewReady = null;
		if (cameraCaptureEnabled) {
			findViewById(R.id.imageView1).setVisibility(View.INVISIBLE);
			findViewById(R.id.video_capture_surface).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.video_capture_surface).setVisibility(View.INVISIBLE);
			findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
		}
		findViewById(R.id.video_frame).requestLayout();
	}
	
	void fixZOrder(SurfaceView video, SurfaceView preview) {
		video.setZOrderOnTop(false);
		preview.setZOrderOnTop(true);
	}

 
	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoViewReady != null)
			((GLSurfaceView)mVideoViewReady).onResume();
		launched=true;
		LinphoneManager.addListener(this);
		refreshHandler.postDelayed(mCallQualityUpdater=new Runnable(){
			LinphoneCall mCurrentCall=LinphoneManager.getLc().getCurrentCall();
			public void run() {
				if (mCurrentCall==null){
					mCallQualityUpdater=null;
					return;
				}
				int oldQuality = 0;
				float newQuality = mCurrentCall.getCurrentQuality();
				if ((int) newQuality != oldQuality){
					updateQualityOfSignalIcon(newQuality);
				}
				if (launched){
					refreshHandler.postDelayed(this, 1000);
				}else mCallQualityUpdater=null;
			}
		},1000);
	}


	private void rewriteToggleCameraItem(MenuItem item) {
		if (LinphoneManager.getLc().getCurrentCall().cameraEnabled()) {
			item.setTitle(getString(R.string.menu_videocall_toggle_camera_disable));
		} else {
			item.setTitle(getString(R.string.menu_videocall_toggle_camera_enable));
		}
	}


	private void rewriteChangeResolutionItem(MenuItem item) {
		if (BandwidthManager.getInstance().isUserRestriction()) {
			item.setTitle(getString(R.string.menu_videocall_change_resolution_when_low_resolution));
		} else {
			item.setTitle(getString(R.string.menu_videocall_change_resolution_when_high_resolution));
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.videocall_activity_menu, menu);

		rewriteToggleCameraItem(menu.findItem(R.id.videocall_menu_toggle_camera));
		rewriteChangeResolutionItem(menu.findItem(R.id.videocall_menu_change_resolution));

		if (!AndroidCameraConfiguration.hasSeveralCameras()) {
			menu.findItem(R.id.videocall_menu_switch_camera).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.videocall_menu_back_to_dialer:
			finish();
			break;
		case R.id.videocall_menu_change_resolution:
			LinphoneManager.getInstance().changeResolution();
			// previous call will cause graph reconstruction -> regive preview window
			if (mVideoCaptureViewReady != null)
				LinphoneManager.getLc().setPreviewWindow(mVideoCaptureViewReady);
			rewriteChangeResolutionItem(item);
			break;
		case R.id.videocall_menu_terminate_call:
			LinphoneManager.getInstance().terminateCall();
			break;
		case R.id.videocall_menu_toggle_camera:
			boolean camEnabled = LinphoneManager.getInstance().toggleEnableCamera(); 
			updatePreview(camEnabled);
			Log.e("winwow camera enabled: " + camEnabled);
			rewriteToggleCameraItem(item);
			// previous call will cause graph reconstruction -> regive preview window
			if (camEnabled) {
				if (mVideoCaptureViewReady != null)
					LinphoneManager.getLc().setPreviewWindow(mVideoCaptureViewReady);
			} else
				LinphoneManager.getLc().setPreviewWindow(null);
			break;
		case R.id.videocall_menu_switch_camera:
			int id = LinphoneManager.getLc().getVideoDevice();
			id = (id + 1) % AndroidCameraConfiguration.retrieveCameras().length;
			LinphoneManager.getLc().setVideoDevice(id);
			CallManager.getInstance().updateCall();
			// previous call will cause graph reconstruction -> regive preview window
			if (mVideoCaptureViewReady != null)
				LinphoneManager.getLc().setPreviewWindow(mVideoCaptureViewReady);
			break;
		default:
			Log.e("Unknown menu item [",item,"]");
			break;
		} 
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (LinphoneUtils.onKeyVolumeSoftAdjust(keyCode)) return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (androidVideoWindowImpl != null) { // Prevent linphone from crashing if correspondent hang up while you are rotating
			androidVideoWindowImpl.release();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.d("onPause VideoCallActivity (isFinishing:", isFinishing(), ", inCall:", LinphoneManager.getLc().isIncall(),")");
		LinphoneManager.removeListener(this);
		if (isFinishing()) {
			videoCall = null; // release reference
		}
		launched=false;
		synchronized (androidVideoWindowImpl) {
			/* this call will destroy native opengl renderer
			 * which is used by androidVideoWindowImpl
			 */
			LinphoneManager.getLc().setVideoWindow(null);
		}

		if (mCallQualityUpdater!=null){
			refreshHandler.removeCallbacks(mCallQualityUpdater);
			mCallQualityUpdater=null;
		}
		
		if (mWakeLock.isHeld())	mWakeLock.release();
		super.onPause();
		if (mVideoViewReady != null)
			((GLSurfaceView)mVideoViewReady).onPause();
	}

	@Override
	public void onCallStateChanged(LinphoneCall call, State state,
			String message) {
		if (call == videoCall && state == State.CallEnd) {
			BandwidthManager.getInstance().setUserRestriction(false);
			LinphoneManager.getInstance().resetCameraFromPreferences();
			finish();
		}
	}
}
