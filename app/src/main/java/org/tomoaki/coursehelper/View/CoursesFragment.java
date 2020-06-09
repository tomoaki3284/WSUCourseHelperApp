package org.tomoaki.coursehelper.View;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Toast;

import org.tomoaki.coursehelper.Model.Course;
import org.tomoaki.coursehelper.Model.PairableSpinner;
import org.tomoaki.coursehelper.Model.Schedule;
import org.tomoaki.coursehelper.Model.ScheduleObserver;

import com.example.coursehelper.R;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    private View view;

    private ScheduleObserver scheduleObserver;
    private Schedule schedule;

    private ListView listView;
    private List<Course> courses;
    private List<Course> updatedCourses;
    private CourseArrayAdapter adapter;

    private int totalNumberOfCourses = 0;
//    private HashMap<String, List<Course>> coreCourses;
//    private HashMap<String, List<Course>> subjectCourses;
//    private List<Course> labCourses;
//    private List<Course> onlineCourses;
//    private List<Course> doubleDipperCourses;

    private List<PairableSpinner> spinners;
    private PairableSpinner spinner;

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_courses, container, false);
        loadInternalFileStorageData();
        setUpListView();
        setUpSpinner();

        if(courses == null || courses.size() < 1){
            new ReadCourses().execute("https://wsucoursehelper.s3.amazonaws.com/current-semester.json");
        }

        return view;
    }

    private void loadInternalFileStorageData() {
        schedule = new Schedule();
        try{
            File file = new File(getContext().getDir("data", MODE_PRIVATE), "scheduleFile.txt");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            schedule = (Schedule) ois.readObject();
            ois.close();
            System.out.println("***********Successfully read schedule object from local file**********");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scheduleObserver = new ViewModelProvider(requireActivity()).get(ScheduleObserver.class);
        scheduleObserver.getSchedule().observe(requireActivity(), new Observer<Schedule>() {
            @Override
            public void onChanged(Schedule schedule) {
                updateSchedule(schedule);
            }
        });
        notifyScheduleChangesToObserver();
    }

    // This is called when user navigates backwards, or fragment is replaced/remove
    // Called when fragment is added to back stack, then remove/replaced
    @Override
    public void onPause() {
        super.onPause();
        try{
            File file = new File(getContext().getDir("data", MODE_PRIVATE), "scheduleFile.txt");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(schedule);
            oos.flush();
            oos.close();
            System.out.println("**********Successfully wrote schedule object to local file*************");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //TODO
    //call when user removed course from scheduleFragment or other
    public void updateSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    //called by CourseDescriptionDialog
    public void addCourseToSchedule(Course course) {
        if(course.getIsCancelled() == false && !schedule.getCourses().contains(course)){
            schedule.addCourse(course);
            Toast.makeText(getContext(), "Added to your schedule", Toast.LENGTH_SHORT).show();
            notifyScheduleChangesToObserver();
        }else{
            if(course.getIsCancelled())
                Toast.makeText(getContext(), "This class is cancelled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "This class is already on your schedule", Toast.LENGTH_SHORT).show();
        }
    }

    //called by CourseDescriptionDialog
    public void removeCourseFromSchedule(Course course) {
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
                dialog.show(getActivity().getSupportFragmentManager(), "dialog");
            }
        });
    }

    public void setUpSpinner() {
        Spinner coreSpinner = view.findViewById(R.id.coreSpinner);
        Spinner subjectSpinner = view.findViewById(R.id.subjectSpinner);
        Spinner specialSpinner = view.findViewById(R.id.specialSpinner);
        PairableSpinner corePSpin = new PairableSpinner("core", coreSpinner, 0, null);
        PairableSpinner subjectPSpin = new PairableSpinner("subject", coreSpinner, 0, null);
        PairableSpinner specialPSpin = new PairableSpinner("special" ,coreSpinner, 0, null);
        spinners = new ArrayList<>(Arrays.asList(corePSpin, subjectPSpin, specialPSpin));

        ArrayAdapter<CharSequence> adapterCore = ArrayAdapter.createFromResource(getContext(), R.array.cores, android.R.layout.simple_spinner_item);
        adapterCore.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coreSpinner.setAdapter(adapterCore);
        coreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                corePSpin.setParent(parent);
                corePSpin.setPosition(position);
                filterCourses();
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
                subjectPSpin.setParent(parent);
                subjectPSpin.setPosition(position);
                filterCourses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter<CharSequence> adapterSpecial = ArrayAdapter.createFromResource(getContext(), R.array.special, android.R.layout.simple_spinner_item);
        adapterSpecial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialSpinner.setAdapter(adapterSpecial);
        specialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specialPSpin.setParent(parent);
                specialPSpin.setPosition(position);
                filterCourses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    /**
     * Apply multiple filter
     */
    private void filterCourses() {
        if(adapter == null){
            System.out.println("*********Adapter is null*********");
            return;
        }

        List<Course> filteredCourses = courses;

        for(int i=0; i<spinners.size(); i++){
            spinner = spinners.get(i);
            filteredCourses = spinner.filterCourses(filteredCourses);
        }

        updatedCourses = filteredCourses;
        adapter.updateList(updatedCourses);

    }

    public void listUpCourses() {
        if(courses == null) return;
        adapter = new CourseArrayAdapter(getActivity(), 0, courses);
        listView.setAdapter(adapter);
        updatedCourses = courses;
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

            return courses;
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            progressBar.setVisibility(View.GONE);
            listUpCourses();
        }
    }
}
