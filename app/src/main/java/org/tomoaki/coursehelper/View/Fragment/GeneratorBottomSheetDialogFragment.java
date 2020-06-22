package org.tomoaki.coursehelper.View.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.coursehelper.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.tomoaki.coursehelper.Model.BottomSheetListView;
import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.Model.ViewModel.GeneratorObserver;
import org.tomoaki.coursehelper.Model.Observable;
import org.tomoaki.coursehelper.Model.Data.Schedule;
import org.tomoaki.coursehelper.View.Adapter.CourseArrayAdapter;

import java.util.List;

public class GeneratorBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private View view;

    private Observable observer;
    private Schedule schedule;

    private List<Course> courses;

    private BottomSheetListView listView;
    private CourseArrayAdapter adapter;
    private Context context;

    public GeneratorBottomSheetDialogFragment() {
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

        TextView tv_header = view.findViewById(R.id.header);
        tv_header.setText("Courses to consider");
        adapter = new CourseArrayAdapter(context,0,courses,R.layout.generator_course_layout_simple);
        setupListView();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        observer = new ViewModelProvider(requireActivity()).get(GeneratorObserver.class);
        observer.getData().observe(requireActivity(), new Observer<Schedule>() {
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
        observer.setData(schedule);
    }

    private void setupListView() {
        listView = view.findViewById(R.id.bottomSheetListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = schedule.getCourses().get(position);
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
