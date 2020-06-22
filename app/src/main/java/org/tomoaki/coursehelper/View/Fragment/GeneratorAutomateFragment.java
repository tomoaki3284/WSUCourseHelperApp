package org.tomoaki.coursehelper.View.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coursehelper.R;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.Model.CoursesEditable;
import org.tomoaki.coursehelper.Model.Observable;
import org.tomoaki.coursehelper.Model.ViewModel.GeneratorObserver;
import org.tomoaki.coursehelper.Model.MultiFilterable;
import org.tomoaki.coursehelper.Model.PairableSpinner;
import org.tomoaki.coursehelper.Model.Data.Schedule;
import org.tomoaki.coursehelper.Model.ViewModel.GeneratorOptionsObserver;
import org.tomoaki.coursehelper.View.Adapter.CourseArrayAdapter;
import org.tomoaki.coursehelper.View.EncapsulatedPairableSpinners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneratorAutomateFragment extends Fragment implements MultiFilterable, CoursesEditable {

    private View view;

    //ViewModel for communication between Automate and BottomSheetDialogFragment
    private Observable generatorObserver;
    //TODO: Another observable class might needed, for communication between Options and Schedule in Options Fragment
    private GeneratorOptionsObserver generatorOptionsObserver;

    private Schedule consideration;
    private HashSet<String> courseTitleInConsideration;

    private List<Course> originalCourses;
    private List<Course> courses;// this only contains unique courses
    private List<Course> updatedCourses;
    private CourseArrayAdapter adapter;

    private ListView listView;
    private List<PairableSpinner> spinners;

    public GeneratorAutomateFragment() {
        // Required empty public constructor
    }

    public void setCourses(List<Course> courses) {
        this.originalCourses = courses;
        this.courses = new ArrayList<>();
        HashSet<String> titleSeen = new HashSet<>();
        for(Course course : courses){
            if(!titleSeen.contains(course.getTitle())){
                titleSeen.add(course.getTitle());
                this.courses.add(course);
            }
        }
        updateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_generator_automate, container, false);

        consideration = new Schedule();
        courseTitleInConsideration = new HashSet<>();
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
        generatorOptionsObserver = new ViewModelProvider(requireActivity()).get(GeneratorOptionsObserver.class);
        generatorObserver = new ViewModelProvider(requireActivity()).get(GeneratorObserver.class);
        generatorObserver.getData().observe(requireActivity(), new Observer<Schedule>() {
            @Override
            public void onChanged(Schedule schedule) {
                updateSchedule(schedule);
            }
        });
    }

    //call when user removed course from scheduleFragment or other
    public void updateSchedule(Schedule schedule) {
        this.consideration = schedule;
        courseTitleInConsideration.clear();
        for(Course course : schedule.getCourses()){
            courseTitleInConsideration.add(course.getTitle());
        }
    }

    //called by some dialog
    public void addCourseToSchedule(Course course) {
        if (!courseTitleInConsideration.contains(course.getTitle())) {
            consideration.addCourse(course);
            courseTitleInConsideration.add(course.getTitle());
            Toast.makeText(getContext(), "Added to your consideration", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "This class is already under your consideration", Toast.LENGTH_SHORT).show();
        }
    }

    //called by some dialog
    public void removeCourseFromSchedule(Course course) {
        if (consideration.removeCourse(course)) {
            Toast.makeText(getContext(), "You successfully removed class from consideration", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "You never added this class", Toast.LENGTH_SHORT).show();
        }
    }

    public void notifyScheduleChangesToBottomSheetObserver() {
        generatorObserver.setData(consideration);
    }

    public void notifyCombinationCompletedToOptionsObserver(List<Schedule> schedules) {
        generatorOptionsObserver.setData(schedules);
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
                bottomSheetDialogFragment.updateSchedule(consideration);
                notifyScheduleChangesToBottomSheetObserver();
            }
        });

        Button closeTheDistanceButton = view.findViewById(R.id.closeTheDistanceButton);
        closeTheDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Schedule> schedules = getCoursesCombination();
                notifyCombinationCompletedToOptionsObserver(schedules);
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

    private List<Schedule> getCoursesCombination() {
        // first, get all the classes that has a same name in list
        //[[Eng,Eng,Eng],[Math,Math,Math,Math],[Env]], something like this
        List<List<Course>> coursesList = findAllCourses();
        //now combination
        List<Schedule> res = new ArrayList<>();
        backtrack(coursesList, 0, new Schedule(), res, coursesList.size());

//        System.out.println("Printing Courses");
//        for(Schedule schedule : res){
//            for(Course course : schedule.getCourses()){
//                System.out.println(course.getTitle() + ": " + course.getTimeContent());
//            }
//            System.out.println("-----------------");
//        }
//        System.out.println("Finish Printing");

        return res;
    }

    private void backtrack(List<List<Course>> coursesList, int idx, Schedule aSchedule, List<Schedule> res, int scheduleSize) {
        if(aSchedule.getCourses().size() == scheduleSize){
            // if hours overlapped, then it's not a valid schedule
            if(aSchedule.isHoursOverlap()) return;
            res.add(new Schedule(aSchedule));
            return;
        }
        for(int i=0; i<coursesList.get(idx).size(); i++){
            aSchedule.addCourse(coursesList.get(idx).get(i));
            backtrack(coursesList, idx+1, aSchedule, res, scheduleSize);
            aSchedule.removeCourse(aSchedule.getCourses().size()-1);
        }
    }

    public List<List<Course>> findAllCourses() {
        List<List<Course>> res = new ArrayList<>();
        for(Course myCourse : consideration.getCourses()){
            String courseTitle = myCourse.getTitle();
            List<Course> courses = new ArrayList<>();
            boolean seen = false;
            for(Course course : originalCourses){
                if(seen && !course.getTitle().equals(courseTitle)) break;
                if(course.getTitle().equals(courseTitle)){
                    seen = true;
                    if(!course.getIsCancelled() && course.getTimeContent() != null && course.getTimeContent().length() != 0) courses.add(course);
                }
            }
            res.add(courses);
        }
        return res;
    }
}
