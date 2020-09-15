package com.chatapp.android.app;

/**
 * created by  Adhash Team on 12/17/2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.WallpaperAdapter;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.Session;

public class WallpaperColor extends CoreActivity {
    Session session;
    final Context context = this;
    public String[] mThumbIds = {
            "#212F3C", "#839192", "#CA6F1E", "#F5B041", "#82E0AA", "#5DADE2", "#A569BD", "#F5B7B1", "#D98880", "#0B5345"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solid_colors);
        getSupportActionBar().show();
        setTitle("Solid Color");
        session = new Session(WallpaperColor.this);


        final GridView gridview = (GridView) findViewById(R.id.gridview);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String itemValue = mThumbIds[position];

                System.out.print("------------------------------------->" + itemValue);
                session.putgalleryPrefs("");
                session.putColor(itemValue);
                Toast.makeText(WallpaperColor.this,"Wallpaper Set",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        gridview.setAdapter(new WallpaperAdapter(context, mThumbIds));

    }


}