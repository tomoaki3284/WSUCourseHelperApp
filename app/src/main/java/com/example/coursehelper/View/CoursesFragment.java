package com.example.coursehelper.View;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coursehelper.Model.Course;
import com.example.coursehelper.Model.courseArrayAdapter;
import com.example.coursehelper.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    private List<Course> courses;

    // for onclick purpose on Dialog class
    static private HashMap<String, Course> crnLinkedCourse;// maybe don't need this anymore

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        new ReadCourses().execute("https://coursehlperwsu.s3.amazonaws.com/current-semester.json", view);
        return view;
    }

    public void listUpCourses(View view) {
        ArrayAdapter<Course> adapter = new courseArrayAdapter(getActivity(), 0, courses);
        ListView listView = view.findViewById(R.id.customListView);
        listView.setAdapter(adapter);
    }

    private class ReadCourses extends AsyncTask<Object, Void, List<Course>> {

        View view;

        @Override
        protected List<Course> doInBackground(Object... objects) {
            this.view = (View) objects[1];

            ObjectMapper mapper = new ObjectMapper();
            try {
                String urlStr = (String) objects[0];
                URL url = new URL(urlStr);
                courses = mapper.readValue(url, new TypeReference<List<Course>>(){ });
                if(courses == null){
                    System.out.println("***Courses Object Null***");
                }
            } catch (IOException e){
                e.printStackTrace();
            }

            if(courses == null){
                System.out.println("********************courses is NULL in AsyncTask");
            }else{
                crnLinkedCourse = new HashMap();
                for(Course course : courses){
                    crnLinkedCourse.put(course.getCourseCRN(), course);
                }
            }

            return courses;
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            listUpCourses(view);
        }
    }
}
