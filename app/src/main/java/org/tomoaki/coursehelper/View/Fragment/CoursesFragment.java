package org.tomoaki.coursehelper.View.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.Model.CoursesEditable;
import org.tomoaki.coursehelper.Model.MultiFilterable;
import org.tomoaki.coursehelper.Model.Observable;
import org.tomoaki.coursehelper.Model.PairableSpinner;
import org.tomoaki.coursehelper.Model.Data.Schedule;
import org.tomoaki.coursehelper.Model.ViewModel.ScheduleObserver;
import org.tomoaki.coursehelper.View.Adapter.CourseArrayAdapter;
import org.tomoaki.coursehelper.View.EncapsulatedPairableSpinners;

import com.example.coursehelper.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment implements MultiFilterable, CoursesEditable {

    private View view;

    private Observable scheduleObserver;
    private Schedule schedule;

    private ListView listView;
    private List<Course> courses;
    private List<Course> updatedCourses;
    private CourseArrayAdapter adapter;

    private List<PairableSpinner> spinners;
    private EditText searchBar;

    private CoursesBottomSheetDialogFragment bottomSheetDialogFragment;

    public CoursesFragment() {
        // Required empty public constructor
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_courses, container, false);

        adapter = new CourseArrayAdapter(getActivity(), 0, courses, R.layout.course_layout_simple);
        loadInternalFileStorageData();
        setUpListView();
        setUpSpinner();
        setupBottomBar();

        updateList();

        return view;
    }

    private void loadInternalFileStorageData() {
        schedule = new Schedule();
        try {
            File file = new File(getContext().getDir("data", MODE_PRIVATE), "scheduleFile.txt");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            schedule = (Schedule) ois.readObject();
            ois.close();
            System.out.println("Successfully read schedule object from local file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scheduleObserver = new ViewModelProvider(requireActivity()).get(ScheduleObserver.class);
        scheduleObserver.getData().observe(requireActivity(), new Observer<Schedule>() {
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
        try {
            File file = new File(getContext().getDir("data", MODE_PRIVATE), "scheduleFile.txt");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(schedule);
            oos.flush();
            oos.close();
            System.out.println("Successfully wrote schedule object to local file");
        } catch (Exception e) {
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
        if (course.getIsCancelled() == false && !schedule.getCourses().contains(course)) {
            schedule.addCourse(course);
            Toast.makeText(getContext(), "Added to your schedule", Toast.LENGTH_SHORT).show();
            notifyScheduleChangesToObserver();
        } else {
            if (course.getIsCancelled())
                Toast.makeText(getContext(), "This class is cancelled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "This class is already on your schedule", Toast.LENGTH_SHORT).show();
        }
    }

    //called by CourseDescriptionDialog
    public void removeCourseFromSchedule(Course course) {
        if (schedule.removeCourse(course)) {
            Toast.makeText(getContext(), "You successfully dropped class", Toast.LENGTH_SHORT).show();
            notifyScheduleChangesToObserver();
        } else {
            Toast.makeText(getContext(), "You never add this class", Toast.LENGTH_SHORT).show();
        }
    }

    public void notifyScheduleChangesToObserver() {
        scheduleObserver.setData(schedule);
    }

    public void setUpListView() {
        listView = view.findViewById(R.id.customListView);
        Fragment f = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = updatedCourses.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt(CourseDescriptionDialogFragment.LAYOUT_TO_INFLATE, R.layout.dialog_course_description);
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
        FloatingActionButton button = view.findViewById(R.id.fab);
        bottomSheetDialogFragment = new CoursesBottomSheetDialogFragment();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "ExampleBottomSheet");
                bottomSheetDialogFragment.updateSchedule(schedule);
                notifyScheduleChangesToObserver();
            }
        });
    }

    public void filterCourses() {
        if (adapter == null) {
            System.out.println("*********Adapter is null*********");
            return;
        }

        updatedCourses = courses;

        for (int i = 0; i < spinners.size(); i++) {
            PairableSpinner spinner = spinners.get(i);
            updatedCourses = spinner.filterCourses(updatedCourses);
        }

        adapter.updateList(updatedCourses);
    }

    public void updateList() {
        if (courses == null || courses.size() == 0 || adapter == null) return;
        adapter.updateList(courses);
        listView.setAdapter(adapter);
    }
}