package com.example.coursehelper.View;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coursehelper.Model.Schedule;
import com.example.coursehelper.Model.ScheduleObserver;
import com.example.coursehelper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private LinearLayout mondayTimeGraph;
    private LinearLayout tuesdayTimeGraph;
    private LinearLayout wednesdayTimeGraph;
    private LinearLayout thursdayTimeGraph;
    private LinearLayout fridayTimeGraph;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        initialDayGraphSetup(view);
        updateUI(null);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ScheduleObserver scheduleObserver = new ViewModelProvider(requireActivity()).get(ScheduleObserver.class);
        scheduleObserver.getSchedule().observe(getViewLifecycleOwner(), new Observer<Schedule>() {
            @Override
            public void onChanged(Schedule schedule) {
                updateUI(schedule);
            }
        });
    }

    public void initialDayGraphSetup(View view) {
        View dayGraph = view.findViewById(R.id.dayGraph);
        LinearLayout mondayTimeGraph = dayGraph.findViewById(R.id.mondayCol);
        LinearLayout tuesdayTimeGraph = dayGraph.findViewById(R.id.tuesdayCol);
        LinearLayout wednesdayTimeGraph = dayGraph.findViewById(R.id.wednesdayCol);
        LinearLayout thursdayTimeGraph = dayGraph.findViewById(R.id.thursdayCol);
        LinearLayout fridayTimeGraph = dayGraph.findViewById(R.id.fridayCol);

        TextView mondayHeader = mondayTimeGraph.findViewById(R.id.header_dayOfWeek);
        mondayHeader.setText("Monday");
        TextView tuesdayHeader = tuesdayTimeGraph.findViewById(R.id.header_dayOfWeek);
        tuesdayHeader.setText("Tuesday");
        TextView wednesdayHeader = wednesdayTimeGraph.findViewById(R.id.header_dayOfWeek);
        wednesdayHeader.setText("Wednesday");
        TextView thursdayHeader = thursdayTimeGraph.findViewById(R.id.header_dayOfWeek);
        thursdayHeader.setText("Thursday");
        TextView fridayHeader = fridayTimeGraph.findViewById(R.id.header_dayOfWeek);
        fridayHeader.setText("Friday");
    }

    public void updateUI(Schedule schedule) {
        if(schedule == null) return;

        //TODO: create TextView for block of class, size = course Hours
        //             Space for block of break, size = next startTime - prev endTime
        System.out.println("************************** successful");
    }
}
