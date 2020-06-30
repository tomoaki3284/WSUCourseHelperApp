package org.tomoaki.coursehelper.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.tomoaki.coursehelper.Model.Data.Course;
import org.tomoaki.coursehelper.Model.Data.Schedule;

import com.example.coursehelper.R;

import java.util.ArrayList;
import java.util.List;

public class CourseArrayAdapter extends ArrayAdapter<Course> {

    private static Context context;
    private List<Course> courses;
    private List<Course> updatedCourses;
    private Schedule schedule;

    private int layoutToInflate;

    public CourseArrayAdapter(Context context, int resource, List<Course> objects, int layoutToInflate) {
        super(context, resource, objects);

        this.context = context;
        this.courses = objects;
        this.updatedCourses = new ArrayList<>(courses);
        this.layoutToInflate = layoutToInflate;
        schedule = new Schedule();
    }

    @Override
    public int getCount() {
        if(updatedCourses == null) return 0;
        return updatedCourses.size();
    }

    //called when rendering the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the property we are displaying
        Course course = updatedCourses.get(position);
        ViewHolder viewHolder;

        // if view is not created yet
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layoutToInflate, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String raw_title = course.getTitle();
        double raw_credit = course.getCredit();
        String raw_timeContent = course.getTimeContent();

        viewHolder.title.setText(raw_title);
        // Need to check for null, because Generator class (unique course) shouldn't have these info
        if(viewHolder.credit != null) viewHolder.credit.setText(Double.toString(raw_credit));
        if(viewHolder.timeContent != null) viewHolder.timeContent.setText(raw_timeContent);
        if(viewHolder.classCancelled != null){
            if(course.getIsCancelled()){
                viewHolder.classCancelled.setText("CANCELLED");
            }else{
                viewHolder.classCancelled.setText("");
            }
        }

        return convertView;
    }

    public void updateList(List<Course> courses) {
        this.updatedCourses = courses;
        notifyDataSetChanged();
    }


    //TODO: For future use of search function
    @NonNull
    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Course> filteredCourses = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredCourses.addAll(courses);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Course course : courses){
                    if(course.getTitle().toLowerCase().contains(filterPattern)){
                        filteredCourses.add(course);
                    }
                }
            }

            FilterResults res = new FilterResults();
            res.values = filteredCourses;
            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            updateList((List<Course>) results.values);
        }
    };

    class ViewHolder {
        private TextView title;
        private TextView credit;
        private TextView timeContent;
        private TextView classCancelled;
        private View colorCode;

        public ViewHolder(View view) {
            title = view.findViewById(R.id.title);
            credit = view.findViewById(R.id.credit);
            timeContent = view.findViewById(R.id.timeContent);
            classCancelled = view.findViewById(R.id.cancelled);
            colorCode = view.findViewById(R.id.colorCode);
        }
    }
}
