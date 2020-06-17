package org.tomoaki.coursehelper.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.coursehelper.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.tomoaki.coursehelper.Model.BottomSheetListView;
import org.tomoaki.coursehelper.Model.Course;
import org.tomoaki.coursehelper.Model.Schedule;
import org.tomoaki.coursehelper.Model.ScheduleObserver;

import java.util.List;

public class CoursesBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private View view;

    private ScheduleObserver scheduleObserver;
    private Schedule schedule;

    private List<Course> courses;

    private BottomSheetListView listView;
    private CourseArrayAdapter adapter;
    private Context context;

    public CoursesBottomSheetDialogFragment() {
        // empty constructor require
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        adapter = new CourseArrayAdapter(context,0,courses);
        setupListView();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        scheduleObserver = new ViewModelProvider(requireActivity()).get(ScheduleObserver.class);
        scheduleObserver.getSchedule().observe(requireActivity(), new Observer<Schedule>() {
            @Override
            public void onChanged(Schedule schedule) {
                updateSchedule(schedule);
            }
        });
        notifyScheduleChangesToObserver();
    }

    public void updateSchedule(Schedule schedule) {
        this.schedule = schedule;
        courses = schedule.getCourses();
        updateList();
    }

    private void notifyScheduleChangesToObserver() {
        scheduleObserver.setSchedule(schedule);
    }

    private void setupListView() {
        listView = view.findViewById(R.id.bottomSheetListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = schedule.getCourses().get(position);
                //TODO: show dialog
                new AlertDialog.Builder(getContext())
                        .setTitle("Remove Entry")
                        .setMessage(course.toPrettyFormatString())
                        .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                schedule.removeCourse(course);
                                adapter.updateList(schedule.getCourses());//update UI
                                notifyScheduleChangesToObserver();
                            }
                        })
                        .show();
            }
        });
    }

    public void updateList() {
        if(courses == null || courses.size() == 0 || adapter == null) return;
        courses = schedule.getCourses();
        adapter.updateList(courses);
        listView.setAdapter(adapter);
    }
}
