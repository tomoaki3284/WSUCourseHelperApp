package org.tomoaki.coursehelper.View.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coursehelper.R;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.Model.Data.CourseOption;
import org.tomoaki.coursehelper.Model.Data.Schedule;
import org.tomoaki.coursehelper.Model.ViewModel.GeneratorOptionsObserver;
import org.tomoaki.coursehelper.Model.ViewModel.OptionVHScheduleObserver;
import org.tomoaki.coursehelper.View.Adapter.CourseOptionAdapter;

import java.util.ArrayList;
import java.util.List;


//TODO ing: Maybe implement ExpandableListView
public class GeneratorOptionsFragment extends Fragment {

    private View view;

    private GeneratorOptionsObserver optionsObserver;
    private OptionVHScheduleObserver optionVHScheduleObserver;

    private RecyclerView recyclerView;
    private CourseOptionAdapter adapter;

    private List<Schedule> schedules;
    private List<CourseOption> courseOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_generator_options, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        courseOptions = new ArrayList<CourseOption>();
        setCourseOptions();
        adapter = new CourseOptionAdapter(courseOptions,this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        optionsObserver = new ViewModelProvider(requireActivity()).get(GeneratorOptionsObserver.class);
        optionsObserver.getData().observe(getViewLifecycleOwner(), new Observer<List<Schedule>>() {
            @Override
            public void onChanged(List<Schedule> schedules) {
                updateSchedules(schedules);
            }
        });

        optionVHScheduleObserver = new ViewModelProvider(requireActivity()).get(OptionVHScheduleObserver.class);
    }

    // Called by CourseOptionViewHolder, who and only who knows which schedule option has selected by user
    public void displaySelectedOptionInScheduler(Schedule schedule) {
        optionVHScheduleObserver.setData(schedule);
    }

    public void updateSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        setCourseOptions();
    }

    private void setCourseOptions() {
        if(schedules == null) return;
        courseOptions.clear();
        for(int i=0; i<schedules.size(); i++){
            String title = "Option " + (i+1);
            List<Course> courses = schedules.get(i).getCourses();
            courseOptions.add(new CourseOption(title, courses));
        }
        //TODO: Come up with better way
        adapter = new CourseOptionAdapter(courseOptions,this);
        recyclerView.setAdapter(adapter);
    }
}
