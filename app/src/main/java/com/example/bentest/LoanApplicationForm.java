package com.example.bentest;

import android.os.Bundle;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

//import com.example.ses.R;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoanApplicationForm extends AppCompatActivity {
    static String memid = "";
    static String guarantor = "";
    static String col1 = "";
    static String val1 = "";
    static String col2 = "";
    static String val2 = "";
    static String col3 = "";
    static String val3 = "";
    static String amount = "";
    static String account = "";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {

            R.drawable.details,
            R.drawable.guarantor,
            R.drawable.residence,
            R.drawable.loan
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application_form);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Tab1Fragment(), "Details");
        adapter.addFrag(new Tab2Fragment(), "Guarantor");
        adapter.addFrag(new Tab4Fragment(), "Residence");
        adapter.addFrag(new Tab3Fragment(), "Process");
        viewPager.setAdapter(adapter);
    }

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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public String adddates(int duration){
        Date date = new Date();
        SimpleDateFormat df  = new SimpleDateFormat("YYYY-MM-dd");
        Calendar c1 = Calendar.getInstance();
        String currentDate = df.format(date);// get current date here
        //   Toast.makeText(this, currentDate, Toast.LENGTH_SHORT).show();

        // now add 30 day in Calendar instance
        c1.add(Calendar.MONTH, duration);
        df = new SimpleDateFormat("yyyy-MM-dd");
        Date resultDate = c1.getTime();
        String     dueDate = df.format(resultDate);
        Toast.makeText(this, dueDate, Toast.LENGTH_LONG).show();
        return dueDate;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;}
}























