package org.tomoaki.coursehelper.View;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.coursehelper.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.tomoaki.coursehelper.Model.Course;
import org.tomoaki.coursehelper.Model.CoursesEditable;
import org.tomoaki.coursehelper.Model.MultiFilterable;
import org.tomoaki.coursehelper.Model.PairableSpinner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneratorAutomateFragment extends Fragment implements MultiFilterable, CoursesEditable {

    private View view;

    private List<Course> courses;
    private List<Course> updatedCourses;
    private List<Course> uniqueCourses;
    private CourseArrayAdapter adapter;

    private ListView listView;
    private List<PairableSpinner> spinners;

    public GeneratorAutomateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_generator_automate, container, false);
        setUpListView();
        setUpSpinner();
        //TODO: setup bottom bar
        //TODO: need to create new ViewModel for GeneratorTab communication between tabs
        //TODO: complete addCourse and removeCourse method
        /*
        TODO: Maybe don't need to implement CoursesEditable, considering the dialog might not be use,
              b/c title and description is all need to display
        */

        if(courses == null) new ReadCourses().execute("https://wsucoursehelper.s3.amazonaws.com/current-semester.json");

        return view;
    }

    public void setUpListView() {
        listView = view.findViewById(R.id.customListView);
        Fragment f = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = updatedCourses.get(position);
                CourseDescriptionDialogFragment dialog = CourseDescriptionDialogFragment.newInstance(null);
                dialog.setTargetFragment(f,0);
                dialog.setCourse(course);
                dialog.show(getParentFragmentManager(), "dialog");
            }
        });
    }

    public void setUpSpinner() {
        EncapsulatedPairableSpinners ePairableSpinners = new EncapsulatedPairableSpinners(view,getContext(),this);
        spinners = ePairableSpinners.getSpinners();
    }

    public void listUpCourses() {
        if(courses == null) return;
        adapter = new CourseArrayAdapter(getActivity(), 0, uniqueCourses);
        listView.setAdapter(adapter);
        updatedCourses = uniqueCourses;
    }

    @Override
    //call by dialog
    public void addCourseToSchedule(Course course) {

    }

    @Override
    //call by dialog
    public void removeCourseFromSchedule(Course course) {

    }

    @Override
    public void filterCourses() {
        if(adapter == null){
            System.out.println("*********Adapter is null*********");
            return;
        }

        List<Course> filteredCourses = uniqueCourses;

        for(int i=0; i<spinners.size(); i++){
            PairableSpinner spinner = spinners.get(i);
            filteredCourses = spinner.filterCourses(filteredCourses);
        }

        updatedCourses = filteredCourses;
        adapter.updateList(updatedCourses);
    }

    private class ReadCourses extends AsyncTask<Object, Void, List<Course>> {
        ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = view.findViewById(R.id.progressBar);
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

            // filter duplicated course, filter by name
            HashSet<String> seenCourseTitle = new HashSet();
            uniqueCourses = new ArrayList<>();
            for(Course course : courses){
                if(!seenCourseTitle.contains(course.getTitle())){
                    uniqueCourses.add(course);
                    seenCourseTitle.add(course.getTitle());
                }
            }

            return courses;
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            progressBar.setVisibility(View.GONE);
            listUpCourses();
        }
    }
}
