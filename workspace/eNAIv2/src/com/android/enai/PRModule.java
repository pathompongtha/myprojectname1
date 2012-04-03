package com.android.enai;

import java.io.IOException;
import java.net.BindException;
import java.util.Arrays;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PRModule extends Activity {
	public static int maxPressure = 180;
	public static Server server = null;
	Context mContext;
	private Handler mHandler;
	private ProgressBar pBar;
	private Dialog dialog;
	private TextView spo2Val;
	private TextView bpmVal;
	private TextView prStatus;
	private Button okButton;
	private ProgressBar acquiringProgressBar;
	
	static {
		server = new Server(4570);
		try { 
			server.start();
		} catch (BindException e) {
			e.printStackTrace();
			Log.e("Seeeduino ADK", "PR Server BindException", e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Seeeduino ADK", "Unable to start TCP server", e);
		}
//		Toast.makeText(mContext, "PR Server is not null, not restarting",Toast.LENGTH_SHORT).show();
	}
	
	PRModule(Context context, Dialog dialog) {
		mContext = context;
		this.dialog = dialog;
		if(dialog != null) {
			spo2Val = (TextView)dialog.findViewById(R.id.spo2_val);
			bpmVal = (TextView)dialog.findViewById(R.id.bpm_val);
			prStatus = (TextView)dialog.findViewById(R.id.pr_status);
			okButton = (Button)dialog.findViewById(R.id.ok_button);
			acquiringProgressBar = (ProgressBar)dialog.findViewById(R.id.acquiring_data_progress_bar);
		}
		mHandler = new Handler();
		server.addListener(new AbstractServerListener() {

            @Override
            public void onReceive(org.microbridge.server.Client client,
                    byte[] data) {
            	Log.d("PR Data", data.length+" "+Arrays.toString(data));
                if (data.length < 2)
                    return;
                final int spo2 = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
                final int bpm = (data[2] & 0xff) | ((data[3] & 0xff) << 8);
                final int errorCode = (data[4] & 0xff) | ((data[5] & 0xff) << 8);
				if (spo2Val != null && bpmVal != null) {
					mHandler.post(new Runnable() {
						public void run() {
			                if(spo2 == 0 || bpm == 0) {
			                	spo2Val.setText("--");
								bpmVal.setText("--");	
			                } else {
								spo2Val.setText(spo2 + "");
								bpmVal.setText(bpm + "");
			                }
	                		acquiringProgressBar.setVisibility(View.GONE);
	                		okButton.setVisibility(View.VISIBLE);
			                switch(errorCode) {
			                case 0: {
			                	if(spo2 == 0 || bpm == 0) {
			                		prStatus.setText("Acquiring data... Please Wait");
			                		acquiringProgressBar.setVisibility(View.VISIBLE);
			                		okButton.setVisibility(View.GONE);
			                	} else {
			                		prStatus.setText("");	
			                	}
			                	break;
			                }
			                case 1: {
			                	prStatus.setText("Please check power settings of module.");
			                	break;
			                }
			                case 2: {
			                	prStatus.setText("Please connect the finger sensor to the module.");
			                	break;
			                }
			                case 3: {
			                	prStatus.setText("Please replace the finger sensor.");
			                	break;
			                }
			                case 4: {
			                	prStatus.setText("Please connect the finger sensor to the patient.");
			                	break;
			                }
			                case 5: {
			                	prStatus.setText("Please ask the patient to sit properly.");
			                	break;
			                }
			                case 6: {
			                	prStatus.setText("Please attach the sensor properly to the patient.");
			                	break;
			                }
			                case 7: {
			                	prStatus.setText("Please ask the patient to sit comfortably.");
			                	break;
			                }
			                }

							//prStatus.append("\nSpO2 = "+spo2+", bpm = " +bpm+", errorCode = "+errorCode);
						}
					});
				}
			}
		});

	}
	
	public void start() {
		//TODO: this is for Nat
//		Toast.makeText(getApplicationContext(), "test "+maxPressure, Toast.LENGTH_LONG).show();
		
		try {
			server.send(new byte[]{1});
			Toast.makeText(mContext, "sent", Toast.LENGTH_SHORT).show();
		} catch(Exception e){
			e.printStackTrace();
			Toast.makeText(mContext, "not sent with exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setMaxPressure(int p) {
		maxPressure = p;
		pBar.setMax(p);
	}
	
	public void setProgressBar(ProgressBar pb) {
		pBar = pb;
	}
}
