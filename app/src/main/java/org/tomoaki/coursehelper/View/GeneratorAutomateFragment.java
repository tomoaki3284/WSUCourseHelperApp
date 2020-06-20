package org.tomoaki.coursehelper.View;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.transition.Scene;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.coursehelper.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tomoaki.coursehelper.Model.Course;
import org.tomoaki.coursehelper.Model.CoursesEditable;
import org.tomoaki.coursehelper.Model.GeneratorObserver;
import org.tomoaki.coursehelper.Model.MultiFilterable;
import org.tomoaki.coursehelper.Model.Observable;
import org.tomoaki.coursehelper.Model.PairableSpinner;
import org.tomoaki.coursehelper.Model.Schedule;
import org.tomoaki.coursehelper.Model.ScheduleObserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneratorAutomateFragment extends Fragment implements MultiFilterable, CoursesEditable {

    private View view;

    private Observable generatorObserver;
    //TODO: Another observable class might needed, for communication between Options and Automate

    private Schedule schedule;
    private List<Course> courses;
    private List<Course> updatedCourses;
    private CourseArrayAdapter adapter;

    private ListView listView;
    private List<PairableSpinner> spinners;

    public GeneratorAutomateFragment() {
        // Required empty public constructor
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_generator_automate, container, false);

        schedule = new Schedule();
        adapter = new CourseArrayAdapter(getActivity(), 0, courses, R.layout.generator_course_layout_simple);
        setUpListView();
        setUpSpinner();
        setupBottomBar();

        updateList();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generatorObserver = new ViewModelProvider(requireActivity()).get(GeneratorObserver.class);
        generatorObserver.getData().observe(requireActivity(), new Observer<Schedule>() {
            @Override
            public void onChanged(Schedule schedule) {
                updateSchedule(schedule);
            }
        });
        notifyScheduleChangesToObserver();
    }

    //call when user removed course from scheduleFragment or other
    public void updateSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    //called by some dialog
    public void addCourseToSchedule(Course course) {
        if (course.getIsCancelled() == false) {
            schedule.addCourse(course);
            Toast.makeText(getContext(), "Added to your consideration", Toast.LENGTH_SHORT).show();
            notifyScheduleChangesToObserver();
        } else {
            Toast.makeText(getContext(), "This class is already under your consideration", Toast.LENGTH_SHORT).show();
        }
    }

    //called by some dialog
    public void removeCourseFromSchedule(Course course) {
        if (schedule.removeCourse(course)) {
            Toast.makeText(getContext(), "You successfully removed class from consideration", Toast.LENGTH_SHORT).show();
            notifyScheduleChangesToObserver();
        } else {
            Toast.makeText(getContext(), "You never added this class", Toast.LENGTH_SHORT).show();
        }
    }

    public void notifyScheduleChangesToObserver() {
        generatorObserver.setData(schedule);
    }

    public void setUpListView() {
        listView = view.findViewById(R.id.customListView);
        Fragment f = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = updatedCourses.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt(CourseDescriptionDialogFragment.LAYOUT_TO_INFLATE, R.layout.generator_course_description_dialog);
                CourseDescriptionDialogFragment dialog = CourseDescriptionDialogFragment.newInstance(bundle);
                dialog.setTargetFragment(f, 0);
                dialog.setCourse(course);
                dialog.show(getParentFragmentManager(), "dialog");
            }
        });
    }

    public void setUpSpinner() {
        EncapsulatedPairableSpinners ePairableSpinners = new EncapsulatedPairableSpinners(view, getContext(), this);
        spinners = ePairableSpinners.getSpinners();
    }

    private void setupBottomBar() {
        Button viewConsiderationButton = view.findViewById(R.id.viewConsiderationButton);
        GeneratorBottomSheetDialogFragment bottomSheetDialogFragment = new GeneratorBottomSheetDialogFragment();
        viewConsiderationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "GeneratorExampleBottomSheet");
                bottomSheetDialogFragment.updateSchedule(schedule);
                notifyScheduleChangesToObserver();
            }
        });

        Button closeTheDistanceButton = view.findViewById(R.id.closeTheDistanceButton);
        closeTheDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Permuting Courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filterCourses() {
        if (adapter == null) {
            return;
        }

        List<Course> filteredCourses = courses;

        for (int i = 0; i < spinners.size(); i++) {
            PairableSpinner spinner = spinners.get(i);
            filteredCourses = spinner.filterCourses(filteredCourses);
        }

        updatedCourses = filteredCourses;
        adapter.updateList(updatedCourses);
    }

    public void updateList() {
        if (courses == null || courses.size() == 0 || adapter == null) return;
        adapter.updateList(courses);
        listView.setAdapter(adapter);
    }
}
