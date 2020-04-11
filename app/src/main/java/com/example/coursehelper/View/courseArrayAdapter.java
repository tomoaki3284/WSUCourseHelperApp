package com.example.coursehelper.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.coursehelper.Model.Course;
import com.example.coursehelper.R;
import com.example.coursehelper.View.CourseDescriptionDialogFragment;

import java.util.List;

public class courseArrayAdapter extends ArrayAdapter<Course> {

    private Context context;
    private List<Course> courses;

    public courseArrayAdapter(Context context, int resource, List<Course> objects) {
        super(context, resource, objects);

        this.context = context;
        this.courses = objects;
    }

    //called when rendering the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the property we are displaying
        Course course = courses.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.course_layout2, null);

        TextView title = view.findViewById(R.id.title);
        TextView credit = view.findViewById(R.id.credit);
        TextView timeContent = view.findViewById(R.id.timeContent);

        String raw_title = course.getTitle();
        double raw_credit = course.getCredit();
        String raw_timeContent = course.getTimeContent();

        title.setText(raw_title);
        credit.setText(Double.toString(raw_credit));
        timeContent.setText(raw_timeContent);

        return view;
    }

    public void updateList(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }
}
