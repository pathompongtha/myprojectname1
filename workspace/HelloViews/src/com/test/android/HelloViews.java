package com.test.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HelloViews extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] menus = getResources().getStringArray(R.array.menus_array);
        setListAdapter((ListAdapter) new ArrayAdapter<String>(this,R.layout.menu_item,menus));
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
            if(((TextView) view).getText().equals("AutoComplete"))
            	startActivity(new Intent(getApplicationContext(),AutoComplete.class));
            else if(((TextView) view).getText().equals("DatePicker"))
            	startActivity(new Intent(getApplicationContext(),MyDatePicker.class));
            else if(((TextView) view).getText().equals("Form"))
            	startActivity(new Intent(getApplicationContext(),MyForm.class)); 
          }
        });
    }
}