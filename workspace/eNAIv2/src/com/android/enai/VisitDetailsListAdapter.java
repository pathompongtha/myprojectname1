package com.android.enai;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class VisitDetailsListAdapter extends ArrayAdapter<VisitDetails> {
	private static final int FNAME_MAX_LEN = 30;
	private final Context context;
	private final String[] detailTitles;
	private final VisitDetails[] details;

	public VisitDetailsListAdapter(Context context, List<VisitDetails> details) {
		super(context, R.layout.patient_details_list_item, details);
		this.context = context;
		this.detailTitles = new String[details.size()];
		this.details = new VisitDetails[details.size()];
		int i = 0;
		for(VisitDetails vd : details) {
			detailTitles[i] = "Visit #"+(i+1);
			this.details[i] = vd;
			i++;
		}
	}

	@Override
	public View getView(final int position, View rowView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(rowView == null){
			rowView = inflater.inflate(R.layout.visit_details_list_item, parent, false);
		}
		TextView tv = (TextView) rowView.findViewById(R.id.label);
		tv.setText(detailTitles[position]);
		
		TextView tv_sub = (TextView) rowView.findViewById(R.id.label_subname);
		tv_sub.setText("Registry ID: " + details[position].registryNumber);
		return rowView;
	}
}

