package com.android.enai.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class PartographDatabaseSource {
	Context mContext;
	SQLiteDatabase db;
	PartographDatabaseHelper helper;
	
	
	public PartographDatabaseSource(Context context) {
		mContext = context;
		helper = new PartographDatabaseHelper(context);
	}
	
	public void clear() {
		db.execSQL("DELETE FROM " + PartographDatabaseHelper.TABLE_NAME + " WHERE _id >= 1");
	}
	
	public boolean isDbOpen() {
		return db.isOpen();
	}

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}
	
	public void addPartographData(HashMap<String, String> map) {
		ContentValues values = new ContentValues();
 
		values.put(PartographDatabaseHelper.TABLE_COLUMN_NAME, map.get(PartographDatabaseHelper.TABLE_COLUMN_NAME));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_AGE, map.get(PartographDatabaseHelper.TABLE_COLUMN_AGE));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_BDAY, map.get(PartographDatabaseHelper.TABLE_COLUMN_BDAY));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_PATIENT_ID, map.get(PartographDatabaseHelper.TABLE_COLUMN_PATIENT_ID));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_PARA, map.get(PartographDatabaseHelper.TABLE_COLUMN_PARA));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_GRAVIDA, map.get(PartographDatabaseHelper.TABLE_COLUMN_GRAVIDA));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_HOSPITAL_NO, map.get(PartographDatabaseHelper.TABLE_COLUMN_HOSPITAL_NO));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_ADMISSION_DATE, map.get(PartographDatabaseHelper.TABLE_COLUMN_ADMISSION_DATE));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_ADMISSION_TIME, map.get(PartographDatabaseHelper.TABLE_COLUMN_ADMISSION_TIME));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_RUPTURED_MEMBRANES, map.get(PartographDatabaseHelper.TABLE_COLUMN_RUPTURED_MEMBRANES));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_CERVIX_PLOT, map.get(PartographDatabaseHelper.TABLE_COLUMN_CERVIX_PLOT));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_HOURS_RUP_MEM, map.get(PartographDatabaseHelper.TABLE_COLUMN_HOURS_RUP_MEM));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_RAPID_ASSESSMENT, map.get(PartographDatabaseHelper.TABLE_COLUMN_RAPID_ASSESSMENT));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_VAGINAL_BLEEDING, map.get(PartographDatabaseHelper.TABLE_COLUMN_VAGINAL_BLEEDING));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_AMNIOTIC_FLUID, map.get(PartographDatabaseHelper.TABLE_COLUMN_AMNIOTIC_FLUID));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_CONTRACTIONS, map.get(PartographDatabaseHelper.TABLE_COLUMN_CONTRACTIONS));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_FETAL_HEART_RATE, map.get(PartographDatabaseHelper.TABLE_COLUMN_FETAL_HEART_RATE));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_URINE_VOIDED, map.get(PartographDatabaseHelper.TABLE_COLUMN_URINE_VOIDED));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_TEMPERATURE, map.get(PartographDatabaseHelper.TABLE_COLUMN_TEMPERATURE));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_PULSE, map.get(PartographDatabaseHelper.TABLE_COLUMN_PULSE));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_BLOOD_PRESSURE, map.get(PartographDatabaseHelper.TABLE_COLUMN_BLOOD_PRESSURE));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_CERVICAL_DILATATION, map.get(PartographDatabaseHelper.TABLE_COLUMN_CERVICAL_DILATATION));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_DELIVERY_PLACENTA, map.get(PartographDatabaseHelper.TABLE_COLUMN_DELIVERY_PLACENTA));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_OXYTOCIN, map.get(PartographDatabaseHelper.TABLE_COLUMN_OXYTOCIN));
		values.put(PartographDatabaseHelper.TABLE_COLUMN_PROBLEM_NOTE, map.get(PartographDatabaseHelper.TABLE_COLUMN_PROBLEM_NOTE));

		db.insert(PartographDatabaseHelper.TABLE_NAME, null, values);
	}
}
