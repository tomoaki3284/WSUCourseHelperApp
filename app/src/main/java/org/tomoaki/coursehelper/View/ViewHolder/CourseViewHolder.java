package org.tomoaki.coursehelper.View.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.coursehelper.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import org.tomoaki.coursehelper.Model.Data.Course;

public class CourseViewHolder extends ChildViewHolder {

    private TextView courseTitle;
    private TextView courseCRN;
    private TextView faculty;
    private TextView time;
    private TextView room;

    public CourseViewHolder(View itemView) {
        super(itemView);
        courseTitle = itemView.findViewById(R.id.generator_option_courseTitle);
        courseCRN = itemView.findViewById(R.id.generator_option_crn);
        faculty = itemView.findViewById(R.id.generator_option_faculty);
        time = itemView.findViewById(R.id.generator_option_time);
        room = itemView.findViewById(R.id.generator_option_room);
    }

    public void onBind(Course course) {
        courseTitle.setText(course.getTitle());
        courseCRN.setText(course.getCourseCRN());
        faculty.setText(course.getFaculty());
        time.setText(course.getTimeContent());
        room.setText(course.getRoom());
    }
}
