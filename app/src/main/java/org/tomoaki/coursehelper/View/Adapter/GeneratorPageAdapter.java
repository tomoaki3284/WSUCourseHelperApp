package org.tomoaki.coursehelper.View.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.View.Fragment.GeneratorAutomateFragment;
import org.tomoaki.coursehelper.View.Fragment.GeneratorOptionsFragment;
import org.tomoaki.coursehelper.View.Fragment.GeneratorScheduleFragment;
import org.tomoaki.coursehelper.View.Fragment.ScheduleFragment;

import java.util.List;

public class GeneratorPageAdapter extends FragmentPagerAdapter {
    private int numOfTabs;
    private GeneratorAutomateFragment generatorAutomateFragment;
    private GeneratorOptionsFragment generatorOptionsFragment;

    //TODO: Might use same ViewModel, but it works tangible. Create new one? Works great, but diff is only ViewModel
    private GeneratorScheduleFragment scheduleFragment;//TODO: in schedule class, add viewModel and instanceof

    public void setCourses(List<Course> courses) {
        generatorAutomateFragment.setCourses(courses);
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
