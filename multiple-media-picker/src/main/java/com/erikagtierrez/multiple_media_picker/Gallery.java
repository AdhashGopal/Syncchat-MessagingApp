package com.erikagtierrez.multiple_media_picker;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.erikagtierrez.multiple_media_picker.Fragments.OneFragment;
import com.erikagtierrez.multiple_media_picker.Fragments.TwoFragment;
import com.erikagtierrez.multiple_media_picker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int selectionTitle;
    public static String title;
    public static int maxSelection;
    public static int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnResult();
            }
        });

        title=getIntent().getExtras().getString("title");
        maxSelection=getIntent().getExtras().getInt("maxSelection");
        if (maxSelection==0) maxSelection = Integer.MAX_VALUE;
        mode=getIntent().getExtras().getInt("mode");
        setTitle(title);
        selectionTitle=0;

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        OpenGallery.selected.clear();
        OpenGallery.imagesSelected.clear();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(selectionTitle>0){
            setTitle(String.valueOf(selectionTitle));
        }
    }

    //This method set up the tab view for images and videos
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(mode==1 || mode==2) {
            adapter.addFragment(new OneFragment(), "Images");
        }
        if(mode==1||mode==3)
        adapter.addFragment(new TwoFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(androidx.fragment.app.FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
               returnResult();
                //Write your code if there's no result
            }
        }
    }

    private void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result",OpenGallery.imagesSelected);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
