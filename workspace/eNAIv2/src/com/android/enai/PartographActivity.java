package com.android.enai;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.linphone.core.Log;

import com.android.enai.db.PartographDatabaseHelper;
import com.android.enai.db.PartographDatabaseSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

public class PartographActivity extends Activity {
	/***
		This is the activity responsible for the partograph view
	***/

	private static final int DIALOG_LIQUOR = 0;
	private static final int DIALOG_MOLDING = 1;
	private static final int DIALOG_BP = 2;
	private static final int DIALOG_PR = 3;
	private static final int DIALOG_DATE = 4;
	private static final int DIALOG_TIME = 5;
	private static final int DIALOG_HOURS_RM = 6;
	private static final int DIALOG_RAPID_ASSESSMENT = 7;
	private static final int DIALOG_VAGINAL_BLEEDING = 8;
	private static final int DIALOG_T_AXILLARY = 9;
	private static final int DIALOG_CERVICAL_DILATATION = 10;
	private static final int DIALOG_URINE_VOIDED = 11;
	private static final int DIALOG_FHR = 12;
	private static final int DIALOG_UC = 13;
	private static final int DIALOG_OXYTOCIN = 14;
	private static final int DIALOG_PLACENTA = 15;
	private static final int DIALOG_PROBLEM = 16;
	private static final int DIALOG_COUNT = 17;
	
	private static final int INTENT_EMAIL = 0;
	private static final int INTENT_VOIP = 1;
	
	private static final HashMap<Integer, Integer> mapIndexToPressure;
	
	static {
		mapIndexToPressure = new HashMap<Integer, Integer>();
		mapIndexToPressure.put(0, 240);
		mapIndexToPressure.put(1, 220);
		mapIndexToPressure.put(2, 200);
		mapIndexToPressure.put(3, 180);
		mapIndexToPressure.put(4, 160);
		mapIndexToPressure.put(5, 140);
	}
	
	private static Button currentSelectedButton;

	private static final String[] itemsLiquor = { "I: membranes intact",
			"R: membranes ruptured", "C: membranes ruptured, clear fluid",
			"M: meconium-stained fluid", "B: blood-stained fluid" };

	private static final String[] itemsRapidAssessment = {"B3", "B4", "B5", "B7"};
	private static final String[] itemsVaginalBleeding = {"0","+","++"};

	private static final String[] tableRowNames = {
			"Hours since Ruptured Membranes", "Rapid Assessment",
			"Vaginal Bleeding", "Amniotic Fluid", "Contractions in 10 minutes",
			"Fetal Heart Rate (beats/minute)", "Urine voided", "T(axillary)",
			"Pulse (beats/minute)", "Blood pressure (systolic/diastolic)",
			"Cervical Dilatation", "Delivery of placenta (time)",
			"Oxytocin (time/given)", "Problem-note onset/describe below" };

	private Thread FHRThread = null;
	private Thread UCThread = null;

	private LinearLayout fetalHeartPlotArea;
	private LinearLayout uterineContractionPlotArea;
	private LinearLayout cervixPlotArea;

	private FHRAutoPlotView fhrView;
	private UCAutoPlotView ucView;
	private BPModule bpModule;
	private PRModule prModule;

	private TableLayout pts;
	private LinearLayout rnts;

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

	private static EditText patientName;
	private static EditText patientAge;
	private static EditText patientBirthday;
	private static EditText patientId;
	private static EditText patientPara;
	private static EditText patientGravida;
	private static EditText patientHospitalNo;
	private static EditText patientRupturedMembranes;
	private Button dateOfAdmission;
	private Button timeOfAdmission;
	private DrawPlotView cervixDrawView;

	static ViewFlipper flipper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.partograph_new);
		flipper = (ViewFlipper) findViewById(R.id.flipper);

		Intent intent = getIntent();

		// Log.d("DEBUG", intent.getStringExtra("name"));
		// Log.d("DEBUG", intent.getStringExtra("age"));
		// Log.d("DEBUG", intent.getStringExtra("birthday"));
		// Log.d("DEBUG", intent.getStringExtra("patientId"));
		
		(patientName = (EditText)findViewById(R.id.patientName)).setText(intent.getStringExtra("name"));
		(patientAge = (EditText)findViewById(R.id.patientAge)).setText(intent.getStringExtra("age"));
		(patientBirthday = (EditText)findViewById(R.id.patientBirthday)).setText(intent.getStringExtra("birthday"));
		(patientId = (EditText)findViewById(R.id.patientId)).setText(intent.getStringExtra("patientId"));
		
		patientGravida = (EditText)findViewById(R.id.patientGravida);
		patientPara = (EditText)findViewById(R.id.patientPara);
		patientHospitalNo = (EditText)findViewById(R.id.patientHospitalNo);
		patientRupturedMembranes = (EditText)findViewById(R.id.patientRupturedMembranes);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int cYear = cal.get(Calendar.YEAR);
		int cMonth = cal.get(Calendar.MONTH);
		int cDay = cal.get(Calendar.DAY_OF_MONTH);
		int cHour = cal.get(Calendar.HOUR);
		int cMin = cal.get(Calendar.MINUTE);
		
		dateOfAdmission = (Button)findViewById(R.id.patientDateOfAdmission);
		dateOfAdmission.setText(String.format("%02d-%02d-%04d", cMonth+1, cDay, cYear));
		
		dateOfAdmission.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_DATE);
			}
		});
		
		timeOfAdmission = (Button)findViewById(R.id.patientTimeOfAdmission);
		timeOfAdmission.setText(String.format("%02d:%02d %s", cHour%12, cMin, (cal.get(Calendar.AM_PM)==0?"AM":"PM")));
		timeOfAdmission.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_TIME);
			}
		});

		cervixPlotArea = (LinearLayout) findViewById(R.id.CervixPlotArea_simple);
		cervixPlotArea.addView(cervixDrawView = new DrawPlotView(getApplicationContext(), 200,600, timeOfAdmission));
		
		pts = (TableLayout) findViewById(R.id.PartographTable_simple);
		pts.setStretchAllColumns(true);
		rnts = (LinearLayout) findViewById(R.id.RowNamesTable_simple);

		{ // Table Column Headings
			TableRow head = new TableRow(getApplicationContext());
			TextView tv = newTableTextView("TIME");
			tv.setWidth(15);
			tv.setTextSize(20);
			tv.setGravity(Gravity.CENTER);
			head.addView(tv);
			for (int i = 1; i <= 12; i++) {
				TextView tvNum = newTableTextView(i + "");
				tvNum.setGravity(Gravity.CENTER);
				tvNum.setTextSize(20);
				head.addView(tvNum);
			}
			pts.addView(head);
			rnts.addView(newTableTextView("FINDINGS"));
		}

		for (String s : tableRowNames) {
			rnts.addView(newTableRowName(s));
			if (s.startsWith("Blood pressure"))
				pts.addView(newRowButtons(DIALOG_BP));
			else if(s.startsWith("Pulse"))
				pts.addView(newRowButtons(DIALOG_PR));
			else if(s.startsWith("Amniotic Fluid"))
				pts.addView(newRowButtons(DIALOG_LIQUOR));
			else if(s.startsWith("Hours"))
				pts.addView(newRowButtons(DIALOG_HOURS_RM));
			else if(s.startsWith("Rapid"))
				pts.addView(newRowButtons(DIALOG_RAPID_ASSESSMENT));
			else if(s.startsWith("Vaginal"))
				pts.addView(newRowButtons(DIALOG_VAGINAL_BLEEDING));
			else if(s.startsWith("T(axillary)"))
				pts.addView(newRowButtons(DIALOG_T_AXILLARY));
			else if(s.startsWith("Cervical"))
				pts.addView(newRowButtons(DIALOG_CERVICAL_DILATATION));
			else if(s.startsWith("Urine"))
				pts.addView(newRowButtons(DIALOG_URINE_VOIDED));
			else if(s.startsWith("Fetal"))
				pts.addView(newRowButtons(DIALOG_FHR));
			else if(s.startsWith("Contractions"))
				pts.addView(newRowButtons(DIALOG_UC));
			else if(s.startsWith("Oxytocin"))
				pts.addView(newRowButtons(DIALOG_OXYTOCIN));
			else if(s.startsWith("Delivery"))
				pts.addView(newRowButtons(DIALOG_PLACENTA));
			else if(s.startsWith("Problem"))
				pts.addView(newRowProblemButtons(DIALOG_PROBLEM));
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

		fetalHeartPlotArea = (LinearLayout) findViewById(R.id.FetalHeartPlotArea);
		uterineContractionPlotArea = (LinearLayout) findViewById(R.id.UterineContractionPlotArea);
		
		LinearLayout fhrSideValues = (LinearLayout) findViewById(R.id.fhr_side_values);
		if (fhrSideValues.getChildCount() == 0) {
			for (int i = 200; i >= 100; i -= 10) {
				TextView tv = new TextView(this);
				tv.setText(i+"");
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.RIGHT);
				tv.setHeight(20);
				fhrSideValues.addView(tv);
			}
		}
		LinearLayout ucSideValues = (LinearLayout) findViewById(R.id.uc_side_values);
		if(ucSideValues.getChildCount() == 0) {
			for (int i = 100; i >= 0; i -= 10) {
				TextView tv = new TextView(this);
				tv.setText(i+"");
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.RIGHT);
				tv.setHeight(20);
				ucSideValues.addView(tv);
			}
		}

		if(fhrView == null) {
			fhrView = new FHRAutoPlotView(getApplicationContext(), 200, 850, R.raw.fhr, FHRThread);
			fetalHeartPlotArea.addView(fhrView);
		}
		if(ucView == null) {
			ucView = new UCAutoPlotView(getApplicationContext(), 200, 850, R.raw.uc, UCThread,tocoZeroButton);
			uterineContractionPlotArea.addView(ucView);
		}
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LIQUOR:
			return new AlertDialog.Builder(PartographActivity.this).setItems(
					itemsLiquor, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if(currentSelectedButton != null) {
								currentSelectedButton.setText(itemsLiquor[item].charAt(0)+"");
							}
						}
					}).setTitle("Amniotic Fluid").create();
		case DIALOG_RAPID_ASSESSMENT:
			return new AlertDialog.Builder(PartographActivity.this).setItems(
					itemsRapidAssessment, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if(currentSelectedButton != null) {
								currentSelectedButton.setText(itemsRapidAssessment[item]);
							}
						}
					}).setTitle("Rapid Assessment").create();
		case DIALOG_VAGINAL_BLEEDING:
			return new AlertDialog.Builder(PartographActivity.this).setItems(
					itemsVaginalBleeding, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if(currentSelectedButton != null) {
								currentSelectedButton.setText(itemsVaginalBleeding[item]);
							}
						}
					}).setTitle("Vaginal Bleeding").create();
		case DIALOG_BP: {
			final Dialog dialog = new Dialog(PartographActivity.this);
			dialog.setContentView(R.layout.dialog_bp);

			bpModule = new BPModule(getApplicationContext(), dialog);

			dialog.setTitle("BP Settings");
			
			ProgressBar bpPressureBar = (ProgressBar) dialog.findViewById(R.id.progressBar1);
			
			Spinner spin = (Spinner) dialog.findViewById(R.id.dialog_bp_spinner);
			spin.setSelection(3);

			spin.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View v,
						int pos, long row) {
					bpModule.setMaxPressure(mapIndexToPressure.get(pos));
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
				}

			});

			final TextView bp_result = (TextView) dialog
					.findViewById(R.id.dialog_bp_result);
			final TextView status = (TextView) dialog
				.findViewById(R.id.dialog_bp_status);

			bpModule.setComponents(bpPressureBar, bp_result, status);
			final Button bpOkButton = (Button) dialog
					.findViewById(R.id.dialog_bp_done);
			bpOkButton.setVisibility(View.GONE);
			bpOkButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeDialog(DIALOG_BP);
					currentSelectedButton.setText(((TextView)dialog.findViewById(R.id.dialog_bp_result)).getText());
				}
			});

			final Button bpNowButton = (Button) dialog.findViewById(R.id.dialog_bp_ok);
			bpNowButton.setVisibility(View.VISIBLE);
			bpNowButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					bpModule.start();
					bpNowButton.setVisibility(View.GONE);
					bpOkButton.setVisibility(View.VISIBLE);
					bpOkButton.setEnabled(false);
				}
			});

			Button cancel = (Button) dialog.findViewById(R.id.dialog_bp_cancel);
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					bpModule.stop();
					// dismissDialog(DIALOG_BP);
					removeDialog(DIALOG_BP);
				}
			});


			return dialog;
		}
		case DIALOG_PR: {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_pr);
			dialog.setTitle("Pulse Rate");
			
			final TextView spo2Val = (TextView)dialog.findViewById(R.id.spo2_val);
			final TextView bpmVal = (TextView)dialog.findViewById(R.id.bpm_val);
			
			Button okButton = (Button)dialog.findViewById(R.id.ok_button);
			okButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDialog(DIALOG_PR);
					prModule.start();
					currentSelectedButton.setText(spo2Val.getText()+"% "+bpmVal.getText()+" bpm");
				}
			});
			prModule = new PRModule(getApplicationContext(), dialog);
			return dialog;
		}
		case DIALOG_DATE: {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			int cYear = c.get(Calendar.YEAR);
			int cMonth = c.get(Calendar.MONTH);
			int cDay = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, cYear, cMonth, cDay);
		}
		case DIALOG_TIME: {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			int cHour = c.get(Calendar.HOUR_OF_DAY);
			int cMin = c.get(Calendar.MINUTE);
			return new TimePickerDialog(this,mTimeSetListener,cHour,cMin,false);
		}
		case DIALOG_HOURS_RM: {
			return new KeypadDialog(this, "Hours since ruptured membranes", currentSelectedButton, currentSelectedButton.getText().toString());
		}
		case DIALOG_T_AXILLARY: {
			return new KeypadDialog(this, "T (axillary)", currentSelectedButton, currentSelectedButton.getText().toString());
		}
		case DIALOG_CERVICAL_DILATATION: {
			return new KeypadDialog(this, "Cervical Dilatation", currentSelectedButton, currentSelectedButton.getText().toString());
		}
		case DIALOG_URINE_VOIDED: {
			return new KeypadDialog(this, "Urine voided", currentSelectedButton, currentSelectedButton.getText().toString());
		}
		case DIALOG_FHR: {
			return new FhrDialog(this, "Fetal Heart Rate", currentSelectedButton, fhrView);
		}
		case DIALOG_UC: {
			return new UcDialog(this, "Contractions", currentSelectedButton, ucView);
		}
		case DIALOG_OXYTOCIN: {
			return new OxytocinDialog(this, "Oxytocin", currentSelectedButton, currentSelectedButton.getText().toString());
		}
		case DIALOG_PLACENTA: {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			int cHour = c.get(Calendar.HOUR_OF_DAY);
			int cMin = c.get(Calendar.MINUTE);
			return new TimePickerDialog(this,mPlacentaTimeSetListener,cHour,cMin,false);
		}
		case DIALOG_PROBLEM: {
			return new ProblemDialog(this, "Problem", (ProblemButton)currentSelectedButton, ((ProblemButton)currentSelectedButton).getContent());
		}
		}
		return null;
	}
	
	private TimePickerDialog.OnTimeSetListener mPlacentaTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if(currentSelectedButton != null) {
				currentSelectedButton.setText(formatTime(hourOfDay, minute));
			}
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if(timeOfAdmission != null) {
				timeOfAdmission.setText(formatTime(hourOfDay, minute));
			}
		}
	};
	
	private String formatTime(int hourOfDay, int minute) {
		if(hourOfDay >= 13) {
			return String.format("%02d:%02d PM",hourOfDay-12, minute);
		} else if(hourOfDay >= 12){
			return String.format("%02d:%02d PM",hourOfDay, minute);
		} else {
			return String.format("%02d:%02d AM",hourOfDay, minute);
		}
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if(dateOfAdmission != null) {
				dateOfAdmission.setText(String.format("%02d-%02d-%04d", monthOfYear+1,dayOfMonth,year));
			}
		}
	};

	private TextView newTableTextView(String s) {
		TextView tv = new TextView(getApplicationContext());
		tv.setText(s);
		tv.setTextSize(20);
		tv.setTextColor(Color.BLACK);
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
			et.setMaxWidth(22);
			row.addView(et);
		}
		return row;
	}

	private TableRow newRowButtons(final int dialogId) {
		TableRow row = new TableRow(getApplicationContext());
		row.addView(newTableTextView(" "));
		for (int i = 0; i < 12; i++) {
			final Button et = new Button(getApplicationContext());
			et.setHeight(72);
			et.setMaxWidth(22);
			et.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeDialog(dialogId);
					currentSelectedButton = et;
					showDialog(dialogId);
				}
			});
			row.addView(et);
		}
		return row;
	}

	private TableRow newRowProblemButtons(final int dialogId) {
		TableRow row = new TableRow(getApplicationContext());
		row.addView(newTableTextView(" "));
		for (int i = 0; i < 12; i++) {
			final Button et = new ProblemButton(getApplicationContext());
			et.setHeight(72);
			et.setMaxWidth(22);
			et.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeDialog(dialogId);
					currentSelectedButton = et;
					showDialog(dialogId);
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
			et.setId(layout.getId()+i);
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
		super.onPause();
		if(fhrView != null) {
			fhrView.requestStop();
			fhrView.stopSaving();
		}
		for(int i=0;i<DIALOG_COUNT;i++) {
			try {
				dismissDialog(i);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		saveInDatabase();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private String getValues(TableRow row) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<row.getChildCount();i++) {
			try {
				sb.append(((Button)row.getChildAt(i)).getText().toString());
			} catch(Exception e) {
				sb.append(((TextView)row.getChildAt(i)).getText().toString());
			}
			sb.append("$");
		}
		return sb.toString();
	}
	
	private void saveInDatabase() {
		PartographDatabaseSource src = new PartographDatabaseSource(getApplicationContext());
		HashMap<String, String> map = new HashMap<String, String>();

		// Log.d("DEBUG ", "saving in database ...");
		String temp = "";
		map.put(PartographDatabaseHelper.TABLE_COLUMN_NAME, temp = patientName.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_AGE, temp = patientAge.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_BDAY, temp = patientBirthday.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_PATIENT_ID, temp = patientId.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_PARA, temp = patientPara.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_GRAVIDA, temp = patientGravida.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_HOSPITAL_NO, temp = patientHospitalNo.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_ADMISSION_DATE, temp = dateOfAdmission.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_ADMISSION_TIME, temp = timeOfAdmission.getText().toString());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_RUPTURED_MEMBRANES, temp = patientRupturedMembranes.getText().toString());
		
		map.put(PartographDatabaseHelper.TABLE_COLUMN_CERVIX_PLOT, temp = cervixDrawView.getValues());
		map.put(PartographDatabaseHelper.TABLE_COLUMN_HOURS_RUP_MEM, temp = getValues((TableRow)(pts.getChildAt(1))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_RAPID_ASSESSMENT, temp = getValues((TableRow)(pts.getChildAt(2))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_VAGINAL_BLEEDING, temp = getValues((TableRow)(pts.getChildAt(3))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_AMNIOTIC_FLUID, temp = getValues((TableRow)(pts.getChildAt(4))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_CONTRACTIONS, temp = getValues((TableRow)(pts.getChildAt(5))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_FETAL_HEART_RATE, temp = getValues((TableRow)(pts.getChildAt(6))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_URINE_VOIDED, temp = getValues((TableRow)(pts.getChildAt(7))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_TEMPERATURE, temp = getValues((TableRow)(pts.getChildAt(8))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_PULSE, temp = getValues((TableRow)(pts.getChildAt(9))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_BLOOD_PRESSURE, temp = getValues((TableRow)(pts.getChildAt(10))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_CERVICAL_DILATATION, temp = getValues((TableRow)(pts.getChildAt(11))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_DELIVERY_PLACENTA, temp = getValues((TableRow)(pts.getChildAt(12))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_OXYTOCIN, temp = getValues((TableRow)(pts.getChildAt(13))));
		map.put(PartographDatabaseHelper.TABLE_COLUMN_PROBLEM_NOTE, temp = getValues((TableRow)(pts.getChildAt(14))));
		
		src.open();
		src.addPartographData(map);
		src.clear();

		Log.d("DEBUG >>", "saving in database: DONE");
	}
}