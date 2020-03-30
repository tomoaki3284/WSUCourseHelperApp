package com.example.coursehelper.View;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import com.example.coursehelper.Model.Course;
import com.example.coursehelper.Model.courseArrayAdapter;
import com.example.coursehelper.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabItem tabCourses;
    private TabItem tabSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tablayout);
        tabCourses = findViewById(R.id.coursesTab);
        tabSchedule = findViewById(R.id.scheduleTab);
        viewPager = findViewById(R.id.viewPager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
        // sync the swipe move with tabs
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
    }

//    private class ReadCourses extends AsyncTask<Object, Void, List<Course>> {
//
//        @Override
//        protected List<Course> doInBackground(Object... objects) {
//
//        }
//
//        @Override
//        protected void onPostExecute(List<Course> courses) {
//
//        }
//    }
}
