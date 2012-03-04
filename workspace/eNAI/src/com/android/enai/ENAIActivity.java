package com.android.enai;

import static android.content.Intent.ACTION_MAIN;

import org.linphone.AboutActivity;
import org.linphone.LinphoneService;
import org.linphone.core.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ENAIActivity extends Activity {
	private static final int DIALOG_LIQUOR = 0;
	private static final int DIALOG_MOLDING = 1;
	private static final int DIALOG_BP = 2;
	private static final int INTENT_EMAIL = 0;
	private static final int INTENT_VOIP = 1;
	private static boolean version_simple = true;

	private static final String[] itemsLiquor = { "I: membranes intact",
			"R: membranes ruptured", "C: membranes ruptured, clear fluid",
			"M: meconium-stained fluid", "B: blood-stained fluid" };

	private static final String[] itemsMolding = { "1: sutures apposed",
			"2: sutures overlapped but reducible",
			"3: sutures overlapped and not reducible" };

	private static final String[] tableRowNames = {
			"Hours since RupturedMembranes", "Rapid Assessment",
			"Vaginal Bleeding", "Amniotic Fluid", "Contractions in 10 minutes",
			"Fetal Heart Rate (beats/minute)", "Urine voided", "T(axillary)",
			"Pulse (beats/minute)", "Blood pressure (systolic/diastolic)",
			"Cervical Dilatation", "Delivery of placenta (time)",
			"Oxytocin (time/given)", "Problem-note onset/describe below" };

	private Thread FHRThread = null;
	private Thread UCThread = null;
	private Thread BPThread = null;
	private Thread PRThread = null;

	private LinearLayout liquorLayout;
	private LinearLayout moldingLayout;
	private LinearLayout oxytocinLayout;
	private LinearLayout dropsPerMinLayout;
	private LinearLayout temperatureLayout;

	private LinearLayout fetalHeartPlotArea;
	private LinearLayout uterineContractionPlotArea;
	private LinearLayout cervixPlotArea;
	private LinearLayout pulseRatePlotArea;
	private LinearLayout bloodPressurePlotArea;

	private FHRAutoPlotView fhrView;
	private BPModule bpModule;

	private TableLayout pts;
	private LinearLayout rnts;

	private static int btnID;

	private static ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(70,
			LayoutParams.WRAP_CONTENT);

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case INTENT_VOIP: {
			if (fhrView != null)
				fhrView.startRecording();
			break;
		}
		}
	}

	static ViewFlipper flipper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.partograph_new);
		flipper = (ViewFlipper) findViewById(R.id.flipper);

		if (bpModule == null)
			bpModule = new BPModule();

		// setContentView(R.layout.partograph_simplified);
		pts = (TableLayout) findViewById(R.id.PartographTable_simple);
		pts.setStretchAllColumns(true);
		rnts = (LinearLayout) findViewById(R.id.RowNamesTable_simple);

		{ // Table Column Headings
			TableRow head = new TableRow(getApplicationContext());
			TextView tv = newTableTextView("TIME");
			tv.setWidth(105);
			head.addView(tv);
			for (int i = 1; i <= 12; i++)
				head.addView(newTableTextView(i + ""));
			pts.addView(head);
			rnts.addView(newTableTextView("FINDINGS"));
		}

		for (String s : tableRowNames) {
			rnts.addView(newTableRowName(s));
			if (s.equals("Blood pressure (systolic/diastolic)"))
				pts.addView(newBPRowEditText());
			else
				pts.addView(newTableRowEditText());
		}

		final Button emailButton = (Button) findViewById(R.id.emailButton);
		emailButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						EmailActivity.class));
			}
		});

		final Button VoIPButton = (Button) findViewById(R.id.VoIPButton);
		VoIPButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (fhrView != null)
					fhrView.stopRecording();

				final Intent intent = new Intent(getApplicationContext(),
						org.linphone.LinphoneLauncherActivity.class);
				startActivityForResult(intent, INTENT_VOIP);
			}
		});

		// setContentView(R.layout.partograph);

		final Button tocoZeroButton = (Button) findViewById(R.id.tocoZero);

		liquorLayout = (LinearLayout) findViewById(R.id.AmnioticFluidLayout);
		moldingLayout = (LinearLayout) findViewById(R.id.MoldingLayout);
		oxytocinLayout = (LinearLayout) findViewById(R.id.OxytocinLayout);
		dropsPerMinLayout = (LinearLayout) findViewById(R.id.DropsPerMinLayout);
		temperatureLayout = (LinearLayout) findViewById(R.id.TemperatureLayout);

		fetalHeartPlotArea = (LinearLayout) findViewById(R.id.FetalHeartPlotArea);
		uterineContractionPlotArea = (LinearLayout) findViewById(R.id.UterineContractionPlotArea);
		pulseRatePlotArea = (LinearLayout) findViewById(R.id.PulseRatePlotArea);
		bloodPressurePlotArea = (LinearLayout) findViewById(R.id.BloodPressurePlotArea);

		fhrView = new FHRAutoPlotView(getApplicationContext(), 130, 600,
				R.raw.fhr, FHRThread);
		fetalHeartPlotArea.addView(fhrView);
		uterineContractionPlotArea.addView(new UCAutoPlotView(
				getApplicationContext(), 130, 600, R.raw.uc, UCThread,
				tocoZeroButton));
		pulseRatePlotArea.addView(new AutoPlotView(getApplicationContext(),
				130, 600, R.raw.uc, PRThread));
		bloodPressurePlotArea.addView(new AutoPlotView(getApplicationContext(),
				130, 600, R.raw.uc, BPThread));

		Button approve = (Button) findViewById(R.id.buttonApprove);
		cervixPlotArea = (LinearLayout) findViewById(R.id.CervixPlotArea);
		cervixPlotArea.addView(new DrawPlotView(getApplicationContext(), 200,
				600, approve));

		// fill in "Liquor"
		for (int i = 0; i < 30; i++) {
			Button btn = new Button(getApplicationContext());
			btn.setOnClickListener(new liquorOnClickListener());
			btn.setId(liquorLayout.getId() + i);
			btn.setWidth(40);
			liquorLayout.addView(btn);
		}

		// fill in "Molding"
		for (int i = 0; i < 30; i++) {
			Button btn = new Button(getApplicationContext());
			btn.setOnClickListener(new moldingOnClickListener());
			btn.setId(moldingLayout.getId() + i);
			btn.setWidth(40);
			moldingLayout.addView(btn);
		}

		fillWithEditTexts(oxytocinLayout);
		fillWithEditTexts(dropsPerMinLayout);
		fillWithEditTexts(temperatureLayout);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LIQUOR:
			return new AlertDialog.Builder(ENAIActivity.this).setItems(
					itemsLiquor, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							for (int i = 0; i < liquorLayout.getChildCount(); i++) {
								View v = liquorLayout.getChildAt(i);

								if (v.getId() == btnID) {
									((Button) v).setText(""
											+ itemsLiquor[item].charAt(0));
									return;
								}
							}
						}
					}).create();
		case DIALOG_MOLDING:
			return new AlertDialog.Builder(ENAIActivity.this).setItems(
					itemsMolding, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							for (int i = 0; i < moldingLayout.getChildCount(); i++) {
								View v = moldingLayout.getChildAt(i);

								if (v.getId() == btnID) {
									((Button) v).setText(""
											+ itemsMolding[item].charAt(0));
									return;
								}
							}
						}
					}).create();
		case DIALOG_BP: {

			Dialog dialog = new Dialog(ENAIActivity.this);
			dialog.setContentView(R.layout.dialog_bp);

			dialog.setTitle("BP Settings");

			Spinner spin = (Spinner) dialog
					.findViewById(R.id.dialog_bp_spinner);

			spin.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View v,
						int pos, long row) {
					// TODO Auto-generated method stub
					switch (pos) {
					case 0:
						bpModule.setMaxPressure(180);
						break;
					case 1:
						bpModule.setMaxPressure(160);
						break;
					case 2:
						bpModule.setMaxPressure(140);
						break;
					case 3:
						bpModule.setMaxPressure(120);
						break;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
				}

			});

			ImageView image = (ImageView) dialog
					.findViewById(R.id.dialog_bp_image);
			image.setImageResource(R.drawable.icon);

			final TextView bp_result = (TextView) dialog
					.findViewById(R.id.dialog_bp_result);

			final Button bpOkButton = (Button) dialog
					.findViewById(R.id.dialog_bp_done);
			bpOkButton.setVisibility(View.GONE);
			bpOkButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// dismissDialog(DIALOG_BP);
					removeDialog(DIALOG_BP);
				}
			});

			final Button bpNowButton = (Button) dialog
					.findViewById(R.id.dialog_bp_ok);
			bpNowButton.setVisibility(View.VISIBLE);
			bpNowButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					bpModule.start();
					bpNowButton.setVisibility(View.GONE);
					bpOkButton.setVisibility(View.VISIBLE);
					bp_result.setText("120/80");

				}
			});

			Button cancel = (Button) dialog.findViewById(R.id.dialog_bp_cancel);
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// dismissDialog(DIALOG_BP);
					removeDialog(DIALOG_BP);
				}
			});

			return dialog;
		}

		}
		return null;
	}

	public class liquorOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			btnID = v.getId();
			showDialog(DIALOG_LIQUOR);
		}
	}

	public class moldingOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			btnID = v.getId();
			showDialog(DIALOG_MOLDING);
		}
	}

	private TextView newTableTextView(String s) {
		TextView tv = new TextView(getApplicationContext());
		tv.setText(s);
		return tv;
	}

	private TextView newTableRowName(String s) {
		TextView tv = newTableTextView(s);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setHeight(72);
		return tv;
	}

	private TableRow newTableRowEditText() {
		TableRow row = new TableRow(getApplicationContext());
		row.addView(newTableTextView(" "));
		for (int i = 0; i < 12; i++) {
			EditText et = new EditText(getApplicationContext());
			et.setCursorVisible(false);
			et.setGravity(Gravity.CENTER);
			et.setInputType(InputType.TYPE_CLASS_NUMBER);
			et.setHeight(72);
			row.addView(et);
		}
		return row;
	}

	private TableRow newBPRowEditText() {
		TableRow row = new TableRow(getApplicationContext());
		row.addView(newTableTextView(" "));
		for (int i = 0; i < 12; i++) {
			// EditText et = new EditText(getApplicationContext());
			Button et = new Button(getApplicationContext());
			// et.setCursorVisible(false);
			// et.setGravity(Gravity.CENTER);
			// et.setInputType(InputType.TYPE_CLASS_NUMBER);
			et.setHeight(72);
			et.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDialog(DIALOG_BP);
				}
			});
			row.addView(et);
		}
		return row;
	}

	private void fillWithEditTexts(LinearLayout layout) {
		for (int i = 0; i < 30; i++) {
			EditText et = new EditText(getApplicationContext());
			et.setInputType(InputType.TYPE_CLASS_NUMBER);
			et.setLayoutParams(vl);
			et.setId(layout.getId());
			layout.addView(et);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.enai_activity_menu, menu);
		menu.findItem(R.id.toggle_partograph).setVisible(true);
		menu.findItem(R.id.enai_menu_exit).setVisible(true);
		menu.findItem(R.id.enai_menu_about).setVisible(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.toggle_partograph:
			flipper.showNext();
			return true;
		case R.id.enai_menu_exit:

			if (fhrView != null)
				fhrView.stopRecording();
			finish();
			return true;
		case R.id.enai_menu_about:
			return true;
		default:
			Log.e("Unknown menu item [", item, "]");
			break;
		}

		return false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}