package com.practice.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class HelloGallery extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Gallery g = (Gallery)findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(this));
        
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(HelloGallery.this, ""+position, Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private Integer[] mImageIds = {
                R.drawable.cow,
                R.drawable.haircut,
                R.drawable.hand,
                R.drawable.horoscope,
                R.drawable.mobile,
                R.drawable.tanga,
                R.drawable.tracks,
                R.drawable.trees
        };

        public ImageAdapter(Context c) {
            mContext = c;
            TypedArray a = obtainStyledAttributes(R.styleable.HelloGallery);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.HelloGallery_android_galleryItemBackground, 0);
            a.recycle();
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            i.setImageResource(mImageIds[position]);
            i.setLayoutParams(new Gallery.LayoutParams(150, 100));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            //i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            i.setBackgroundResource(mGalleryItemBackground);

            return i;
        }
    }
}