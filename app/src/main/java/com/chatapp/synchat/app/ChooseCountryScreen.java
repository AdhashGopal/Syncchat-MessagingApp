package com.chatapp.synchat.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.CPAdapter;
import com.chatapp.synchat.app.widget.AvnNextLTProRegEditText;
import com.chatapp.synchat.core.CoreActivity;

import java.util.ArrayList;

public class ChooseCountryScreen extends CoreActivity {

    /**
     * This class allows the user to choose the country while logging in
     */
    private ListView lv_country;
    static String[] country;
    static String[] code;
    private AvnNextLTProRegEditText country_picker_search;
    static CPAdapter countryPickerAdapter;
    private ArrayList<String> list;

    private ArrayList<String> codelist;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country_screen);
//        setTitle("Select country");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // assign string-array data source in my java code
        Context c = getApplicationContext();
        Resources res = c.getResources();

        country = res.getStringArray(R.array.country_list);

        code = res.getStringArray(R.array.country_code);

        list = new ArrayList<String>();
        codelist = new ArrayList<String>();

        for (int i = 0; i < country.length; i++) {
            list.add(country[i]);
            codelist.add(code[i]);
        }

        countryPickerAdapter = new CPAdapter(ChooseCountryScreen.this, list, codelist);
        country_picker_search = (AvnNextLTProRegEditText) findViewById(R.id.country_picker_search);
        lv_country = (ListView) findViewById(R.id.chooseCountryLv);
        lv_country.setAdapter(countryPickerAdapter);
        lv_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String message = list.get(position);
                String code1 = codelist.get(position);
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", message);
                intent.putExtra("CODE", code1);
                setResult(1, intent);
                finish();
            }

        });

        // Search Filter
        country_picker_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                int textlength = country_picker_search.getText().length();
                list.clear();
                codelist.clear();
                // codelist.clear();
                for (int i = 0; i < country.length; i++) {
                    if (textlength <= country[i].length()) {
                        if (country[i].toLowerCase().contains(
                                country_picker_search.getText().toString()
                                        .toLowerCase().trim())) {
                            list.add(country[i]);
                            codelist.add(code[i]);

                        }
                    }
                }
                lv_country.setAdapter(new CPAdapter(
                        ChooseCountryScreen.this, list, codelist));

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


}
