package org.tomoaki.coursehelper.View;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coursehelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment implements View.OnClickListener{

    public final static String FRAG_TAG = "com.exmaple.coursehelper.View.HomePageFragment";

    private CoursesScheduleTabFragment coursesScheduleTabFragment;

    private View view;
    private MainActivity activity;//need this reference for replacing/adding fragment with other one
    private FragmentManager fragmentManager;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public void setParentActivity(MainActivity mainActivity) {
        this.activity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homepage_gridcards_layout, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();

        setOnClickListener();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Back Button Pressed, but it is on HomePageFragment, so nothing should happen
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return view;
    }

    private void setOnClickListener() {
        CardView card_viewCourses = view.findViewById(R.id.homepage_card_viewCourses);
        CardView card_setting = view.findViewById(R.id.homepage_card_setting);
        CardView card_generator = view.findViewById(R.id.homepage_card_generator);
        CardView card_advGenerator = view.findViewById(R.id.homepage_card_advanceGenerator);
        CardView card_rateProfessor = view.findViewById(R.id.homepage_card_rateProfessor);
        CardView card_donation = view.findViewById(R.id.homepage_card_donation);
        card_viewCourses.setOnClickListener(this);
        card_setting.setOnClickListener(this);
        card_generator.setOnClickListener(this);
        card_advGenerator.setOnClickListener(this);
        card_rateProfessor.setOnClickListener(this);
        card_donation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //TODO: Generate each fragment if null, and replace it
        switch(v.getId()){
            case R.id.homepage_card_viewCourses:
                if(coursesScheduleTabFragment == null) coursesScheduleTabFragment = new CoursesScheduleTabFragment();
                activity.loadFragment(coursesScheduleTabFragment, coursesScheduleTabFragment.FRAG_TAG);
                break;

            // Fall through for now
            case R.id.homepage_card_setting:

            case R.id.homepage_card_generator:

            case R.id.homepage_card_advanceGenerator:

            case R.id.homepage_card_rateProfessor:

            case R.id.homepage_card_donation:
                Toast.makeText(getActivity(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
