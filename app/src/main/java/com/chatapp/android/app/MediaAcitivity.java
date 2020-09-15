package com.chatapp.android.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 3/10/2017.
 */
public class MediaAcitivity extends CoreActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AvnNextLTProDemiTextView toolbar_title;
    private ImageView toolbar_back_button;
    private String docid;
    private Typeface avnRegFont, avnDemiFont;

    /**
     * widget binding
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediomainactivity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (AvnNextLTProDemiTextView) findViewById(R.id.toolbar_title);
        toolbar_back_button = (ImageView) findViewById(R.id.toolbar_back_button);
        Bundle bundle = getIntent().getExtras();
        docid = bundle.getString("docid");
        String username = bundle.getString("username");
        toolbar_title.setText(username);
        toolbar_title.setTextSize(20);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        avnRegFont = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        avnDemiFont = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Session session = new Session(MediaAcitivity.this);
        session.putMediadocid(docid);
// set Fragmentclass Arguments
        createTabIcons();
        toolbar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Media adapter
     *
     * @param viewPager layout view
     */
    private void setupViewPager(ViewPager viewPager) {
        Bundle bundle_fragment = new Bundle();
        bundle_fragment.putString("docid", docid);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MediaFragment(), "Media");
        adapter.addFragment(new DocumentFragment(), "Documents");
//        adapter.addFragment(new LinkFragment(), "Links");
        viewPager.setAdapter(adapter);
    }

    /**
     * Media View PagerAdapter
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
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

    /**
     * Tab name
     */
    private void createTabIcons() {
        View tab1 = LayoutInflater.from(this).inflate(R.layout.count_on_tabs_view, null);
        TextView tab1Title = (TextView) tab1.findViewById(R.id.tvTitle);
        tab1Title.setText("MEDIA");
        tab1Title.setTypeface(avnRegFont);
        tabLayout.getTabAt(0).setCustomView(tab1);

        View tab2 = LayoutInflater.from(this).inflate(R.layout.count_on_tabs_view, null);
        TextView tab2Title = (TextView) tab2.findViewById(R.id.tvTitle);
        tab2Title.setText("DOCUMENTS");
        tab2Title.setTypeface(avnRegFont);
        tabLayout.getTabAt(1).setCustomView(tab2);

//        View tab3 = LayoutInflater.from(this).inflate(R.layout.count_on_tabs_view, null);
//        TextView tab3Title = (TextView) tab3.findViewById(R.id.tvTitle);
//        tab3Title.setText("LINKS");
//        tab3Title.setTypeface(avnRegFont);
//        tabLayout.getTabAt(2).setCustomView(tab3);

    }


}
