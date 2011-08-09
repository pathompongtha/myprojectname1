package com.android.Rx;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class RxImage extends Activity {
	private int curAlpha = 255;
	private int dAlpha = 10;
	private int H, W;
	private float scaleH, scaleW;
	private float rot = 0, dRot = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final RelativeLayout screen = (RelativeLayout) findViewById(R.id.screen);

		screen.setGravity(Gravity.CENTER);

		final Button alphaMinus = (Button) findViewById(R.id.button1);
		final Button alphaPlus = (Button) findViewById(R.id.button2);
		final Button sizeMinus = (Button) findViewById(R.id.button3);
		final Button sizePlus = (Button) findViewById(R.id.button4);
		final Button rotMinus = (Button) findViewById(R.id.button5);
		final Button rotPlus = (Button) findViewById(R.id.button6);

		final Bitmap pic = BitmapFactory.decodeResource(getResources(),
				R.drawable.moai);
		W = pic.getWidth();
		H = pic.getHeight();
		scaleH = scaleW = 1;

		final Matrix mat = new Matrix();
		mat.postScale(scaleH, scaleW);

		Bitmap rpic = Bitmap.createBitmap(pic, 0, 0, W, H, mat, true);
		BitmapDrawable bmd = new BitmapDrawable(rpic);

		final ImageView image = new ImageView(this);
		image.setImageDrawable(bmd);
		image.setScaleType(ScaleType.CENTER);

		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.ABOVE, R.id.sizeAdjust);
		screen.addView(image, params);

		sizePlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				scaleH = 1.01f;
				scaleW = 1.01f;
				screen.removeView(image);
				mat.postScale(scaleH, scaleW);
				image.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(
						pic, 0, 0, W, H, mat, true)));
				// image.setScaleType(ScaleType.CENTER);
				screen.addView(image, params);
			}
		});

		sizeMinus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				scaleH = 0.99f;
				scaleW = 0.99f;
				screen.removeView(image);
				mat.postScale(scaleH, scaleW);
				image.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(
						pic, 0, 0, W, H, mat, true)));
				screen.addView(image, params);
			}
		});

		alphaPlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				curAlpha += dAlpha;
				curAlpha = Math.min(255, curAlpha);
				image.setAlpha(curAlpha);
			}
		});

		alphaMinus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				curAlpha -= dAlpha;
				curAlpha = Math.max(0, curAlpha);
				image.setAlpha(curAlpha);
			}
		});

		rotPlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rot = dRot;
				screen.removeView(image);
				mat.postRotate(rot);
				image.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(
						pic, 0, 0, W, H, mat, true)));
				screen.addView(image, params);
			}
		});

		rotMinus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rot = -dRot;
				screen.removeView(image);
				mat.postRotate(rot);
				image.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(
						pic, 0, 0, W, H, mat, true)));
				screen.addView(image, params);
			}
		});

	}
}