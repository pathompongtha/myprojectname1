package com.android.enai;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class VisitDetailsSectionListAdapter extends BaseAdapter {
	
	private static final int TYPE_SECTION_HEADER = 0;
	private static final int TYPE_LIST_ELEMENT = 1;
	
	public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	public final ArrayAdapter<String> headers;

	public VisitDetailsSectionListAdapter(Context context) {
		headers = new ArrayAdapter<String>(context,
				R.layout.visit_details_list_header);
	}

	public void addSection(String section, Adapter adapter) {
		headers.add(section);
		sections.put(section, adapter);
	}
	
	public Object getItem(int position) {
		for(Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			if(position == 0) return section;
			if(position < size) return adapter.getItem(position - 1);
			
			position -= size;
		}
		return null;
	}

	public int getCount() {
		int total = 0;
		for(Adapter adapter : sections.values()) {
			total += adapter.getCount() + 1;
		}
		return total;
	}
	
	public int getViewTypeCount() {
		/*
		int total = 0;
		for(Adapter adapter : sections.values()) {
			total += adapter.getViewTypeCount();
		}
		Log.i("RDEBUG", "TYPE COUNT = "+total);
		return total;
		*/
		return 2;
	}
	
	public int getItemViewType(int position) {
		int type = 1;
		for(Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			if(position == 0) return TYPE_SECTION_HEADER;
			if(position < size) return TYPE_LIST_ELEMENT;
			
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for(Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			if(position == 0) return headers.getView(sectionnum, convertView, parent);
			if(position < size) return adapter.getView(position-1, convertView, parent);
			
			position -= size;
			sectionnum++;
			
		}
		return null;
	}

}
