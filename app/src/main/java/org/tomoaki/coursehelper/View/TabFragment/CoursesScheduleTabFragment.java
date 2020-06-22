package org.tomoaki.coursehelper.View.TabFragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coursehelper.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.View.Adapter.PageAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesScheduleTabFragment extends Fragment {

    public final static String FRAG_TAG = "com.exmaple.coursehelper.View.CoursesScheduleTabFragment";
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private View view;
    private TabItem tabCourses;
    private TabItem tabSchedule;

    private List<Course> courses;

    public CoursesScheduleTabFragment() {
        // Required empty public constructor
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_main, container, false);

        fragmentManager = getChildFragmentManager();

        tabLayout = view.findViewById(R.id.tablayout);
        tabCourses = view.findViewById(R.id.coursesTab);
        tabSchedule = view.findViewById(R.id.scheduleTab);
        viewPager = view.findViewById(R.id.viewPager);

        pageAdapter = new PageAdapter(fragmentManager, tabLayout.getTabCount());
        pageAdapter.setCourses(courses);
        viewPager.setAdapter(pageAdapter);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //TODO: Back Stack one fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStackImmediate();// pop itself

                int top = fragmentManager.getBackStackEntryCount()-1;
                if(top >= 0){
                    FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(top);
                    Fragment currentFragment = fragmentManager.findFragmentByTag(backStackEntry.getName());
                    currentFragment.getView().setVisibility(View.VISIBLE);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // sync the swipe move with tabs
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
