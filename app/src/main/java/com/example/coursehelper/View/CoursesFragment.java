package com.example.coursehelper.View;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.coursehelper.Model.Course;
import com.example.coursehelper.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    private ListView listView;
    private List<Course> courses;
    private HashMap<String, List<Course>> coreCourses;
    private HashMap<String, List<Course>> subjectCourses;
    private List<Course> doubleDipperCourses;

    // for onclick purpose on Dialog class
    static private HashMap<String, Course> crnLinkedCourse;// maybe don't need this anymore

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses, container, false);
        listView = view.findViewById(R.id.customListView);

        setUpSpinner(view);
        if(courses == null || courses.size() < 1){
            new ReadCourses().execute("https://coursehlperwsu.s3.amazonaws.com/current-semester.json");
        }
        return view;
    }

    public void setUpSpinner(View view) {
        Spinner coreSpinner = view.findViewById(R.id.coreSpinner);
        Spinner subjectSpinner = view.findViewById(R.id.subjectSpinner);

        ArrayAdapter<CharSequence> adapterCore = ArrayAdapter.createFromResource(getContext(), R.array.cores, android.R.layout.simple_spinner_item);
        adapterCore.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coreSpinner.setAdapter(adapterCore);

        coreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listUpCourses();
                if(position == 0){ // first item = "None"
                    listUpCourses();
                    return;
                }

                String targetCore = parent.getItemAtPosition(position).toString().split(" ")[0];
                assert coreCourses.get(targetCore) != null;
                if(targetCore.equals("Double Dipper")){
                    listUpFilteredCourses(doubleDipperCourses);
                }else{
                    listUpFilteredCourses(coreCourses.get(targetCore));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapterSubject = ArrayAdapter.createFromResource(getContext(), R.array.subjects, android.R.layout.simple_spinner_item);
        adapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapterSubject);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listUpCourses();
                if(position == 0){ // first item = "None"
                    listUpCourses();
                    return;
                }

                String targetSubject = parent.getItemAtPosition(position).toString().split(" ")[0];
                assert subjectCourses.get(targetSubject) != null;
                listUpFilteredCourses(subjectCourses.get(targetSubject));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void listUpFilteredCourses(List<Course> courses) {
        if(courses == null) return;
        ArrayAdapter<Course> adapter = new courseArrayAdapter(getActivity(), 0, courses);
        listView.setAdapter(adapter);
    }

    public void listUpCourses() {
        if(courses == null) return;
        ArrayAdapter<Course> adapter = new courseArrayAdapter(getActivity(), 0, courses);
        listView.setAdapter(adapter);
    }

    private class ReadCourses extends AsyncTask<Object, Void, List<Course>> {

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
                System.out.println("********************courses is NULL in AsyncTask");
            }else{
                crnLinkedCourse = new HashMap();
                coreCourses = new HashMap();
                subjectCourses = new HashMap();
                doubleDipperCourses = new ArrayList();
                for(Course course : courses){
                    crnLinkedCourse.put(course.getCourseCRN(), course);
                    linkCoreCourse(course);
                    linkSubjectCourse(course);
                    if(course.getCores() != null && course.getCores().size() > 1){
                        doubleDipperCourses.add(course);
                    }
                }
            }

            return courses;
        }

        public void linkCoreCourse(Course course) {
            List<String> cores = course.getCores();
            if(cores == null || cores.size() < 1) return;

            for(String core : cores){
                if(coreCourses.get(core) == null){
                    List<Course> courses = new ArrayList();
                    courses.add(course);
                    coreCourses.put(core, courses);
                }else{
                    coreCourses.get(core).add(course);
                }
            }
        }

        public void linkSubjectCourse(Course course) {
            String subject = course.getSubject();
            if(course.getSubject() == null) return;

            if(subjectCourses.get(subject) == null){
                List<Course> courses = new ArrayList();
                courses.add(course);
                subjectCourses.put(subject, courses);
            }else{
                subjectCourses.get(subject).add(course);
            }
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            listUpCourses();
        }
    }
}
