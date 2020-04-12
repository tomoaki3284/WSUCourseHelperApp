package com.example.coursehelper.View;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coursehelper.Model.Course;
import com.example.coursehelper.Model.DayOfWeek;
import com.example.coursehelper.Model.Schedule;
import com.example.coursehelper.Model.ScheduleObserver;
import com.example.coursehelper.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    private ScheduleObserver scheduleObserver;
    private Schedule schedule;

    private ListView listView;
    private List<Course> courses;
    private List<Course> updatedCourses;
    private courseArrayAdapter adapter;

    private HashMap<String, List<Course>> coreCourses;
    private HashMap<String, List<Course>> subjectCourses;
    private List<Course> doubleDipperCourses;
    static private HashMap<String, Course> crnLinkedCourse;

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        schedule = new Schedule();
        setUpListView(view);
        setUpSpinner(view);

        if(courses == null || courses.size() < 1){
            new ReadCourses().execute("https://coursehlperwsu.s3.amazonaws.com/current-semester.json");
        }

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scheduleObserver = new ViewModelProvider(requireActivity()).get(ScheduleObserver.class);
        scheduleObserver.getSchedule().observe(requireActivity(), new Observer<Schedule>() {
            @Override
            public void onChanged(Schedule schedule) {
                updateSchedule(schedule);
            }
        });
    }

    //call when user removed course from scheduleFragment
    public void updateSchedule(Schedule schedule) {
        this.schedule = schedule;
        Toast.makeText(getContext(), "You have: " + schedule.getCourses().size() + " class", Toast.LENGTH_SHORT).show();
        System.out.println("User has: " + schedule.getCourses().size() + " courses");
    }

    //called by CourseDescriptionDialog
    public void addCourseToSchedule(String crn) {
        Course course = crnLinkedCourse.get(crn);
        schedule.addCourse(course);
        notifyScheduleChangesToObserver();
    }

    //called by CourseDescriptionDialog
    public void removeCourseFromSchedule(String crn) {
        Course course = crnLinkedCourse.get(crn);
        if(schedule.removeCourse(course)){
            Toast.makeText(getContext(), "You successfully dropped class", Toast.LENGTH_SHORT).show();
            notifyScheduleChangesToObserver();
        }else{
            Toast.makeText(getContext(), "You never add this class", Toast.LENGTH_SHORT).show();
        }
    }

    public void notifyScheduleChangesToObserver() {
        scheduleObserver.setSchedule(schedule);
    }




    public void setUpListView(View view) {
        listView = view.findViewById(R.id.customListView);
        Fragment f = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = updatedCourses.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", course.getTitle());
                bundle.putString("timeContent", course.getTimeContent());
                bundle.putString("faculty", course.getFaculty());
                bundle.putString("room", course.getRoom());
                bundle.putString("credit", Double.toString(course.getCredit()));
                bundle.putString("crn", course.getCourseCRN());
                bundle.putString("description", course.getCourseDescription());
                bundle.putString("cores", coresToString(course.getCores()));

                DialogFragment dialog = CourseDescriptionDialogFragment.newInstance(bundle);
                dialog.setTargetFragment(f,0);
                dialog.show(getFragmentManager(), "dialog");
            }

            public String coresToString(List<String> cores) {
                String res = "";
                for(String core : cores){
                    res += core;
                    res += "/";
                }
                return res.substring(0,res.length()-1);
            }
        });
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
                if(adapter == null) return;
                if(position == 0){ // first item = "None"
                    adapter.updateList(courses);
                    updatedCourses = courses;
                    return;
                }

                String targetCore = parent.getItemAtPosition(position).toString().split(" ")[0];
                assert coreCourses.get(targetCore) != null;
                if(targetCore.equals("DoubleDipper")){
                    adapter.updateList(doubleDipperCourses);
                    updatedCourses = doubleDipperCourses;
                }else{
                    adapter.updateList(coreCourses.get(targetCore));
                    updatedCourses = coreCourses.get(targetCore);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter<CharSequence> adapterSubject = ArrayAdapter.createFromResource(getContext(), R.array.subjects, android.R.layout.simple_spinner_item);
        adapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapterSubject);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(adapter == null) return;
                if(position == 0){ // first item = "None"
                    adapter.updateList(courses);
                    updatedCourses = courses;
                    return;
                }

                String targetSubject = parent.getItemAtPosition(position).toString().split(" ")[0];
                assert subjectCourses.get(targetSubject) != null;
                adapter.updateList(subjectCourses.get(targetSubject));
                updatedCourses = subjectCourses.get(targetSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void listUpCourses() {
        if(courses == null) return;
        adapter = new courseArrayAdapter(getActivity(), 0, courses);
        listView.setAdapter(adapter);
        updatedCourses = courses;
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
