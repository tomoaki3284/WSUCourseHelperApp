package org.tomoaki.coursehelper.View.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.coursehelper.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.View.TabFragment.CoursesScheduleTabFragment;
import org.tomoaki.coursehelper.View.TabFragment.GeneratorTabFragment;
import org.tomoaki.coursehelper.View.Activity.MainActivity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment implements View.OnClickListener{

    public final static String FRAG_TAG = "com.exmaple.coursehelper.View.HomePageFragment";

    private CoursesScheduleTabFragment coursesScheduleTabFragment;
    private GeneratorTabFragment generatorTabFragment;

    private View view;
    private MainActivity activity;//need this reference for replacing/adding fragment with other one
    private FragmentManager fragmentManager;

    private List<Course> courses;
    private List<Course> uniqueCourses;
    private HashMap<Course, List<Course>> labBindMap;

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

        if(courses == null || courses.size() < 1){
            new ReadCourses().execute("https://wsucoursehelper.s3.amazonaws.com/current-semester.json");
        }

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
                assert  courses != null && courses.size() != 0;
                coursesScheduleTabFragment.setCourses(courses);
                activity.loadFragment(coursesScheduleTabFragment, coursesScheduleTabFragment.FRAG_TAG);
                break;

            case R.id.homepage_card_generator:
                if(generatorTabFragment == null) generatorTabFragment = new GeneratorTabFragment();
                generatorTabFragment.setCourses(courses);
                generatorTabFragment.setLabBinder(labBindMap);
                activity.loadFragment(generatorTabFragment, generatorTabFragment.FRAG_TAG);
                break;

            // Fall through for now
            case R.id.homepage_card_setting:

            case R.id.homepage_card_advanceGenerator:

            case R.id.homepage_card_rateProfessor:

            case R.id.homepage_card_donation:
                Toast.makeText(getActivity(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class ReadCourses extends AsyncTask<Object, Void, List<Course>> {
        ProgressBar progressBar;
        GridLayout gridLayout;

        @Override
        protected void onPreExecute() {
            progressBar = view.findViewById(R.id.progressBar);
            gridLayout = view.findViewById(R.id.homepage_gridlayout);
            gridLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Course> doInBackground(Object... objects) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String urlStr = (String) objects[0];
                URL url = new URL(urlStr);
                courses = mapper.readValue(url, new TypeReference<List<Course>>(){ });
                if(courses == null){
                    System.out.println("***Courses Object Null***");
                }
            } catch (IOException e){
                e.printStackTrace();
            }

            if(courses == null){
                System.out.println("Courses is NULL in AsyncTask");
            }

            bindLabCourses();

            return courses;
        }

        private void bindLabCourses() {
            assert courses != null && courses.size() > 0;
            //Course with Labs
            labBindMap = new HashMap<>();
            Course prevCourse = courses.get(0);
            for(int i=1; i<courses.size(); i++){
                Course currCourse = courses.get(i);
                if(currCourse.getIsLabCourse()){
                    if(labBindMap.containsKey(prevCourse)){
                        labBindMap.get(prevCourse).add(currCourse);
                    }else{
                        labBindMap.put(prevCourse, new ArrayList<>(Arrays.asList(currCourse)));
                    }
                }else{
                    prevCourse = currCourse;
                }
            }
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            progressBar.setVisibility(View.GONE);
            gridLayout.setVisibility(View.VISIBLE);
        }
    }
}
