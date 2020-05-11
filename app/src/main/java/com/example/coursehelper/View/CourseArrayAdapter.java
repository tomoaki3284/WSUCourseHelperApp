package com.example.coursehelper.View;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.coursehelper.Model.Course;
import com.example.coursehelper.R;

import java.util.List;

public class CourseArrayAdapter extends ArrayAdapter<Course> {

    private Context context;
    private List<Course> courses;

    public CourseArrayAdapter(Context context, int resource, List<Course> objects) {
        super(context, resource, objects);

        this.context = context;
        this.courses = objects;
    }

    @Override
    public int getCount() {
        if(courses == null) return 0;
        return courses.size();
    }

    //called when rendering the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the property we are displaying
        Course course = courses.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.course_layout_simple, null);

        TextView title = view.findViewById(R.id.title);
        TextView credit = view.findViewById(R.id.credit);
        TextView timeContent = view.findViewById(R.id.timeContent);
        TextView classCancelled = view.findViewById(R.id.cancelled);
        View colorCode = view.findViewById(R.id.colorCode);

//        colorCode.setBackgroundColor(Color.parseColor("#043a78"));

        String raw_title = course.getTitle();
        double raw_credit = course.getCredit();
        String raw_timeContent = course.getTimeContent();

        title.setText(raw_title);
        credit.setText(Double.toString(raw_credit));
        timeContent.setText(raw_timeContent);
        if(course.getIsCancelled()){
            classCancelled.setText("CANCELLED");
        }

        return view;
    }

    public void updateList(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }
}
