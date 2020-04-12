package com.example.coursehelper.View;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coursehelper.Model.Course;
import com.example.coursehelper.Model.DayOfWeek;
import com.example.coursehelper.Model.Hours;
import com.example.coursehelper.Model.Schedule;
import com.example.coursehelper.Model.ScheduleObserver;
import com.example.coursehelper.R;

import java.util.EnumMap;
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

    private RelativeLayout mondayTimeCol;
    private RelativeLayout tuesdayTimeCol;
    private RelativeLayout wednesdayTimeCol;
    private RelativeLayout thursdayTimeCol;
    private RelativeLayout fridayTimeCol;

    private View view;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        initialDayGraphSetup();
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

    public void initialDayGraphSetup() {
        View dayGraph = view.findViewById(R.id.dayGraph);
        mondayTimeGraph = dayGraph.findViewById(R.id.mondayCol);
        tuesdayTimeGraph = dayGraph.findViewById(R.id.tuesdayCol);
        wednesdayTimeGraph = dayGraph.findViewById(R.id.wednesdayCol);
        thursdayTimeGraph = dayGraph.findViewById(R.id.thursdayCol);
        fridayTimeGraph = dayGraph.findViewById(R.id.fridayCol);

        mondayTimeCol = mondayTimeGraph.findViewById(R.id.timeCol);
        tuesdayTimeCol = tuesdayTimeGraph.findViewById(R.id.timeCol);
        wednesdayTimeCol = wednesdayTimeGraph.findViewById(R.id.timeCol);
        thursdayTimeCol = thursdayTimeGraph.findViewById(R.id.timeCol);
        fridayTimeCol = fridayTimeGraph.findViewById(R.id.timeCol);

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
        if(schedule.isHoursOverlap()){
            Toast.makeText(getContext(), "Some of your classes have time conflict. But sorry, we are not capable to tell which on has conflict...", Toast.LENGTH_LONG);
        }

        //TODO: create TextView for block of class, size = course Hours
        //             Space for block of break, size = next startTime - prev endTime
        // Some class has two class in one day: apply changes according to this

        clearSchedule();// There has to be a better/efficient way to draw timeline
        int prevClassLength = 0;
        for(Course course : schedule.getCourses()){
            EnumMap<DayOfWeek, List<Hours>> dayOpenHours = course.getHoursOfDay();
            for(DayOfWeek day : dayOpenHours.keySet()){
                List<Hours> hoursList = dayOpenHours.get(day);
                for(Hours hours : hoursList){
                    int[] classTimeInterval = hours.getInIntervalForm();
                    switch(day){
                        case MONDAY:
                            displayClassToGraph(mondayTimeCol, course, classTimeInterval);
                            break;

                        case TUESDAY:
                            displayClassToGraph(tuesdayTimeCol, course, classTimeInterval);
                            break;

                        case WEDNESDAY:
                            displayClassToGraph(wednesdayTimeCol, course, classTimeInterval);
                            break;

                        case THURSDAY:
                            displayClassToGraph(thursdayTimeCol, course, classTimeInterval);
                            break;

                        case FRIDAY:
                            displayClassToGraph(fridayTimeCol, course, classTimeInterval);
                            break;
                    }
                }
            }
        }
    }

    public void displayClassToGraph(RelativeLayout dayTimeCol, Course course, int[] classTimeInterval) {
        int newClassStart = defaultMinToTimelineMin(classTimeInterval[0]);
        int newClassEnd = defaultMinToTimelineMin(classTimeInterval[1]);
        int newClassStartPx = dpToPx(newClassStart);
        int lengthDp = newClassEnd - newClassStart;
        int lengthPx = dpToPx(lengthDp);

        TextView newClass = new TextView(getContext());
        newClass.setHeight(lengthPx);
        newClass.setText(course.getTitle());
        newClass.setTextSize(12);
        newClass.setTextColor(Color.WHITE);
        newClass.setGravity(Gravity.CENTER);
        newClass.setBackground(getResources().getDrawable(R.drawable.course_cell));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPx(60), lengthPx);
        params.topMargin = newClassStartPx;
        dayTimeCol.addView(newClass, params);
    }

    public int defaultMinToTimelineMin(int minutesDefault) {
        int diffInMinutes = 7 * 60;
        return minutesDefault - diffInMinutes;
    }


    public int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public int pxToDp(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }

    public void clearSchedule() {
        mondayTimeCol.removeAllViewsInLayout();
        tuesdayTimeCol.removeAllViews();
        wednesdayTimeCol.removeAllViews();
        thursdayTimeCol.removeAllViews();
        fridayTimeCol.removeAllViews();
    }
}
