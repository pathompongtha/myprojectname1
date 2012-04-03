package com.android.enai.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PartographDatabaseHelper extends SQLiteOpenHelper {
	private static final String marker = "$";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "PartographDatabase";
	
	public static final String TABLE_NAME = "PartographDetails";
	
	public static final String TABLE_COLUMN_ID = "_id";
	public static final String TABLE_COLUMN_NAME = "name";
	public static final String TABLE_COLUMN_AGE = "age";
	public static final String TABLE_COLUMN_BDAY = "bday";
	public static final String TABLE_COLUMN_PATIENT_ID = "patientId";
	public static final String TABLE_COLUMN_PARA = "para";
	public static final String TABLE_COLUMN_GRAVIDA = "gravida";
	public static final String TABLE_COLUMN_HOSPITAL_NO = "hospitalNo";
	public static final String TABLE_COLUMN_ADMISSION_DATE = "admissionDate";
	public static final String TABLE_COLUMN_ADMISSION_TIME = "admissionTime";
	public static final String TABLE_COLUMN_RUPTURED_MEMBRANES = "rupturedMembranes";
	
	public static final String TABLE_COLUMN_CERVIX_PLOT = "cervixPlot";
	public static final String TABLE_COLUMN_HOURS_RUP_MEM = "hoursRupturedMembrane";
	public static final String TABLE_COLUMN_RAPID_ASSESSMENT = "rapidAssessment";
	public static final String TABLE_COLUMN_VAGINAL_BLEEDING = "vaginalBleeding";
	public static final String TABLE_COLUMN_AMNIOTIC_FLUID = "amnioticFluid";
	public static final String TABLE_COLUMN_CONTRACTIONS = "contractions";
	public static final String TABLE_COLUMN_FETAL_HEART_RATE = "fetalHeartRate";
	public static final String TABLE_COLUMN_URINE_VOIDED = "urineVoided";
	public static final String TABLE_COLUMN_TEMPERATURE = "temperature";
	public static final String TABLE_COLUMN_PULSE = "pulse";
	public static final String TABLE_COLUMN_BLOOD_PRESSURE = "bloodPressure";
	public static final String TABLE_COLUMN_CERVICAL_DILATATION = "cervicalDilatation";
	public static final String TABLE_COLUMN_DELIVERY_PLACENTA = "deliveryPlacenta";
	public static final String TABLE_COLUMN_OXYTOCIN = "oxytocin";
	public static final String TABLE_COLUMN_PROBLEM_NOTE = "problemNote";
	
	
	public static final String TABLE_CREATE = "CREATE TABLE " +
		TABLE_NAME + " (" + 
		TABLE_COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		TABLE_COLUMN_NAME + " TEXT, "+
		TABLE_COLUMN_AGE +" TEXT, " +
		TABLE_COLUMN_BDAY +" TEXT, " +
		TABLE_COLUMN_PATIENT_ID +" TEXT, " +
		TABLE_COLUMN_PARA +" TEXT, " +
		TABLE_COLUMN_GRAVIDA +" TEXT, " +
		TABLE_COLUMN_HOSPITAL_NO +" TEXT, " +
		TABLE_COLUMN_ADMISSION_DATE +" TEXT, " +
		TABLE_COLUMN_ADMISSION_TIME +" TEXT, " +
		TABLE_COLUMN_RUPTURED_MEMBRANES +" TEXT, " +
		
		TABLE_COLUMN_CERVIX_PLOT +" TEXT, " +
		TABLE_COLUMN_HOURS_RUP_MEM +" TEXT, " +
		TABLE_COLUMN_RAPID_ASSESSMENT +" TEXT, " +
		TABLE_COLUMN_VAGINAL_BLEEDING +" TEXT, " +
		TABLE_COLUMN_AMNIOTIC_FLUID +" TEXT, " +
		TABLE_COLUMN_CONTRACTIONS +" TEXT, " +
		TABLE_COLUMN_FETAL_HEART_RATE +" TEXT, " +
		TABLE_COLUMN_URINE_VOIDED +" TEXT, " +
		TABLE_COLUMN_TEMPERATURE +" TEXT, " +
		TABLE_COLUMN_PULSE +" TEXT, " +
		TABLE_COLUMN_BLOOD_PRESSURE +" TEXT, " +
		TABLE_COLUMN_CERVICAL_DILATATION +" TEXT, " +
		TABLE_COLUMN_DELIVERY_PLACENTA +" TEXT, " +
		TABLE_COLUMN_OXYTOCIN +" TEXT, " +
		TABLE_COLUMN_PROBLEM_NOTE +" TEXT " +
			");";
	
	public String[] allColumns = {
		TABLE_COLUMN_ID, 
		TABLE_COLUMN_NAME,
		TABLE_COLUMN_AGE,
		TABLE_COLUMN_BDAY,
		TABLE_COLUMN_PATIENT_ID,
		TABLE_COLUMN_PARA,
		TABLE_COLUMN_GRAVIDA,
		TABLE_COLUMN_HOSPITAL_NO,
		TABLE_COLUMN_ADMISSION_DATE,
		TABLE_COLUMN_ADMISSION_TIME,
		TABLE_COLUMN_RUPTURED_MEMBRANES,
		
		TABLE_COLUMN_CERVIX_PLOT,
		TABLE_COLUMN_HOURS_RUP_MEM,
		TABLE_COLUMN_RAPID_ASSESSMENT,
		TABLE_COLUMN_VAGINAL_BLEEDING,
		TABLE_COLUMN_AMNIOTIC_FLUID,
		TABLE_COLUMN_CONTRACTIONS,
		TABLE_COLUMN_FETAL_HEART_RATE,
		TABLE_COLUMN_URINE_VOIDED,
		TABLE_COLUMN_TEMPERATURE,
		TABLE_COLUMN_PULSE,
		TABLE_COLUMN_BLOOD_PRESSURE,
		TABLE_COLUMN_CERVICAL_DILATATION,
		TABLE_COLUMN_DELIVERY_PLACENTA,
		TABLE_COLUMN_OXYTOCIN,
		TABLE_COLUMN_PROBLEM_NOTE,
	};
	
	
	public PartographDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("DEBUG", "command = " + TABLE_CREATE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("DEBUG", "on upgrade");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
