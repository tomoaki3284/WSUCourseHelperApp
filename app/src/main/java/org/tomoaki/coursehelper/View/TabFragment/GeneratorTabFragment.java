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
import com.google.android.material.tabs.TabLayout;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.View.Adapter.GeneratorPageAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneratorTabFragment extends Fragment {

    public static final String FRAG_TAG = "com.exmaple.coursehelper.View.GeneratorTabFragment";

    private View view;
    private TabLayout tabLayout;
    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private GeneratorPageAdapter pageAdapter;

    private List<Course> courses;
    private HashMap<Course, List<Course>> labBindMap;

    public GeneratorTabFragment() {
        // Required empty public constructor
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void setLabBinder(HashMap<Course, List<Course>> labBindMap) {
        this.labBindMap = labBindMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_generator, container, false);

        fragmentManager = getChildFragmentManager();

        tabLayout = view.findViewById(R.id.tablayout_generator);
        viewPager = view.findViewById(R.id.viewPager_generator);

        pageAdapter = new GeneratorPageAdapter(fragmentManager, tabLayout.getTabCount());
        pageAdapter.setCourses(courses);
        pageAdapter.setLabBinder(labBindMap);
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
}
