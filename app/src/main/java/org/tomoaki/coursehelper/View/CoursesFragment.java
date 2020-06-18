package org.tomoaki.coursehelper.View;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.tomoaki.coursehelper.Model.Course;
import org.tomoaki.coursehelper.Model.MultiFilterable;
import org.tomoaki.coursehelper.Model.PairableSpinner;
import org.tomoaki.coursehelper.Model.Schedule;
import org.tomoaki.coursehelper.Model.ScheduleObserver;

import com.example.coursehelper.R;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment implements MultiFilterable {

    private View view;

    private ScheduleObserver scheduleObserver;
    private Schedule schedule;

    private ListView listView;
    private List<Course> courses;
    private List<Course> updatedCourses;
    private CourseArrayAdapter adapter;

    private List<PairableSpinner> spinners;

    private CoursesBottomSheetDialogFragment bottomSheetDialogFragment;

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
        setupBottomBar();
        System.out.println("onCreateView");

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
            System.out.println("Successfully read schedule object from local file");
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
        System.out.println("onViewCreated");
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
            System.out.println("Successfully wrote schedule object to local file");
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
                dialog.show(getParentFragmentManager(), "dialog");
            }
        });
    }

    public void setUpSpinner() {
        EncapsulatedPairableSpinners ePairableSpinners = new EncapsulatedPairableSpinners(view,getContext(),this);
        spinners = ePairableSpinners.getSpinners();
    }

    private void setupBottomBar() {
        FloatingActionButton button = view.findViewById(R.id.fab);
        bottomSheetDialogFragment = new CoursesBottomSheetDialogFragment();
        Context context = bottomSheetDialogFragment.getContext();
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: Generate BottomSheetDialog
                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "ExampleBottomSheet");
                bottomSheetDialogFragment.updateSchedule(schedule);
                notifyScheduleChangesToObserver();
            }
        });
    }

    public void filterCourses() {
        if(adapter == null){
            System.out.println("*********Adapter is null*********");
            return;
        }

        List<Course> filteredCourses = courses;

        for(int i=0; i<spinners.size(); i++){
            PairableSpinner spinner = spinners.get(i);
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
