package com.android.RxSerial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class RxSerial extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// File a = getFilesDir();
		// Toast.makeText(getApplicationContext(), a + "", Toast.LENGTH_SHORT)
		// .show();
		//
		// String FILENAME = "hello_file";
		// String string = "hello world!";
		//
		// try {
		// FileOutputStream fos = openFileOutput(FILENAME,
		// Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
		// fos.write(string.getBytes());
		// fos.close();
		// } catch (Exception e) {
		// }
		//
		// String s[] = fileList();
		// for (String ss : s) {
		// Toast.makeText(getApplicationContext(), ss, Toast.LENGTH_SHORT)
		// .show();
		// StringBuilder sb = new StringBuilder("");
		// try {
		// FileInputStream in = openFileInput(ss);
		// while (true) {
		// int x = in.read();
		// if (x == -1) {
		// in.close();
		// break;
		// }
		// sb.append((char) x);
		// }
		// } catch (Exception e) {
		// }
		// Toast.makeText(getApplicationContext(), sb.toString(),
		// Toast.LENGTH_SHORT).show();
		// }

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
			mExternalStorageAvailable = !(mExternalStorageWriteable = false);
		else
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		Toast.makeText(
				getApplicationContext(),
				"External Storage " + (mExternalStorageAvailable ? "" : "not ")
						+ " available\n" + "External Storage "
						+ (mExternalStorageWriteable ? "" : "not ")
						+ " writeable", Toast.LENGTH_SHORT).show();

		// File path =
		// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
		File path = Environment.getExternalStorageDirectory();
		path = new File(path, "Android");
		path.mkdirs();
		path = new File(path, "data");
		path.mkdirs();
		path = new File(path, "com.android.RxSerial");
		path.mkdirs();
		path = new File(path, "files");
		path.mkdirs();

		File file = new File(path, "test.txt");

		// try {
		// OutputStream fos = new FileOutputStream(file);
		// for (char c : "this is a new test message".toCharArray()) {
		// fos.write((int) c);
		// }
		// fos.close();
		// Toast.makeText(getApplicationContext(), "Success: write",
		// Toast.LENGTH_SHORT).show();
		// } catch (Exception e) {
		// Toast.makeText(getApplicationContext(), "Error: write",
		// Toast.LENGTH_SHORT)
		// .show();
		// }
		try {
			FileInputStream in = new FileInputStream(file);
			StringBuilder sb = new StringBuilder("");
			while (true) {
				int x = in.read();
				if (x == -1)
					break;
				sb.append((char) x);
			}
			Toast.makeText(getApplicationContext(),
					"Success: read\n" + sb.toString(), Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "No file found",
					Toast.LENGTH_SHORT).show();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}