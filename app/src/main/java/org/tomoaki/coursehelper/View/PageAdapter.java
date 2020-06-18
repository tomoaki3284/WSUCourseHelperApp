package org.tomoaki.coursehelper.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.tomoaki.coursehelper.Model.Course;

import java.util.List;

class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private CoursesFragment coursesFragment;
    private ScheduleFragment scheduleFragment;

    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
        coursesFragment = new CoursesFragment();
        scheduleFragment = new ScheduleFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return coursesFragment;

            case 1:
                return scheduleFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
