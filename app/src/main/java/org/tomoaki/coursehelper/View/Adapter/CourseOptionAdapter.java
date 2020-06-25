package org.tomoaki.coursehelper.View.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.coursehelper.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.Model.Data.CourseOption;
import org.tomoaki.coursehelper.Model.Data.Schedule;
import org.tomoaki.coursehelper.View.Fragment.GeneratorOptionsFragment;
import org.tomoaki.coursehelper.View.ViewHolder.CourseOptionViewHolder;
import org.tomoaki.coursehelper.View.ViewHolder.CourseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CourseOptionAdapter extends ExpandableRecyclerViewAdapter<CourseOptionViewHolder, CourseViewHolder> {

    private List<Schedule> schedules;
    private Fragment parentFragment;

    public CourseOptionAdapter(List<CourseOption> groups, Fragment parentFragment) {
        super(groups);
        this.parentFragment = parentFragment;
        schedules = new ArrayList<>();
        for(CourseOption courseOption : groups){
            List<Course> courses = courseOption.getCourses();
            schedules.add(new Schedule(courses));
        }
    }

    @Override
    public CourseOptionViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.generator_course_layout_simple, parent, false);
        CourseOptionViewHolder courseOptionViewHolder = new CourseOptionViewHolder(view);
        courseOptionViewHolder.setParentFragment(parentFragment);
        return courseOptionViewHolder;
    }

    @Override
    public CourseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.generator_option_course_layout, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(CourseViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Course course = (Course) group.getItems().get(childIndex);
        holder.onBind(course);
    }

    @Override
    public void onBindGroupViewHolder(CourseOptionViewHolder holder, int flatPosition, ExpandableGroup group) {
        Schedule selectedSchedule = schedules.get(flatPosition);
        holder.setTitle(group);
        holder.set(selectedSchedule);
    }
}
