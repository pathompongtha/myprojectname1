package com.android.enai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_GRAVIDA = "gravida";
	public static final String KEY_PARA = "para";
	public static final String KEY_ADMIT_TIME = "admit_time";
	public static final String KEY_FHR = "fhr";
	public static final String KEY_UC = "uc";
	public static final String KEY_BP = "bp";
	public static final String KEY_PR = "pr";
	public static final String[] COLUMN_NAMES = new String[] { KEY_ROWID,
			KEY_NAME, KEY_GRAVIDA, KEY_PARA, KEY_ADMIT_TIME, KEY_FHR, KEY_UC,
			KEY_BP, KEY_PR };
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "eNAI";
	private static final String DATABASE_TABLE = "records";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "create table titles ("
			+ "_id integer primary key autoincrement, "
			+ "name text not null, " + "gravida integer not null, "
			+ "para integer not null, " + "admit_time integer not null, "
			+ "fhr text not null, " + "uc text not null, "
			+ "bp text not null, " + "pr text not null);";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	private ContentValues bundleValues(String name, long gravida, long para,
			String admit_time, String fhr, String uc, String bp, String pr) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_GRAVIDA, gravida);
		values.put(KEY_PARA, para);
		values.put(KEY_ADMIT_TIME, admit_time);
		values.put(KEY_FHR, fhr);
		values.put(KEY_UC, uc);
		values.put(KEY_BP, bp);
		values.put(KEY_PR, pr);
		return values;
	}

	// ---insert a title into the database---
	public long insertRecord(String name, long gravida, long para,
			String admit_time, String fhr, String uc, String bp, String pr) {
		ContentValues initialValues = bundleValues(name, gravida, para,
				admit_time, fhr, uc, bp, pr);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// ---deletes a particular title---
	public boolean deleteRecord(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// ---retrieves all the titles---
	public Cursor getAllRecords() {
		return db.query(DATABASE_TABLE, COLUMN_NAMES, null, null, null, null,
				null);
	}

	// ---retrieves a particular title---
	public Cursor getRecord(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, COLUMN_NAMES, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a title---
	public boolean updateRecord(long rowId, String name, long gravida,
			long para, String admit_time, String fhr, String uc, String bp,
			String pr) {
		ContentValues args = bundleValues(name, gravida, para, admit_time, fhr,
				uc, bp, pr);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
			onCreate(db);
		}
	}
}