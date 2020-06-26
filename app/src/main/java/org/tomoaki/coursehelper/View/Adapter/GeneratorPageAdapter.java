package org.tomoaki.coursehelper.View.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.View.Fragment.GeneratorAutomateFragment;
import org.tomoaki.coursehelper.View.Fragment.GeneratorOptionsFragment;
import org.tomoaki.coursehelper.View.Fragment.GeneratorScheduleFragment;

import java.util.HashMap;
import java.util.List;

public class GeneratorPageAdapter extends FragmentPagerAdapter {
    private int numOfTabs;
    private GeneratorAutomateFragment generatorAutomateFragment;
    private GeneratorOptionsFragment generatorOptionsFragment;

    private GeneratorScheduleFragment scheduleFragment;

    public void setCourses(List<Course> courses) {
        generatorAutomateFragment.setCourses(courses);
    }

    public void setLabBinder(HashMap<Course, List<Course>> labBindMap) {
        generatorAutomateFragment.setLabBinder(labBindMap);
    }

    public GeneratorPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm,numOfTabs);
        this.numOfTabs = numOfTabs;
        generatorAutomateFragment = new GeneratorAutomateFragment();
        generatorOptionsFragment = new GeneratorOptionsFragment();
        scheduleFragment = new GeneratorScheduleFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return generatorAutomateFragment;

            case 1:
                return generatorOptionsFragment;

            case 2:
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
