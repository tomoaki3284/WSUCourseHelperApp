package com.example.coursehelper;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.coursehelper.Model.Course;
import com.example.coursehelper.Model.courseArrayAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static List<Course> courses = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ReadCourses().execute();
    }

    private void listUpCourses() {
        ArrayAdapter<Course> adapter = new courseArrayAdapter(this, 0, courses);

        ListView listView = findViewById(R.id.customListView);
        listView.setAdapter(adapter);
    }


    private class ReadCourses extends AsyncTask<String, Void, List<Course>> {
        @Override
        protected List<Course> doInBackground(String... params) {
            List<Course> courses = null;
            ObjectMapper mapper = new ObjectMapper();
            try {
                URL url = new URL("https://coursehlperwsu.s3.amazonaws.com/current-semester.json");
                courses = mapper.readValue(url, new TypeReference<List<Course>>(){ });
                if(courses == null){
                    System.out.println("***Courses Object Null***");
                }
            } catch (IOException e){
                e.printStackTrace();
            }

            if(courses == null){
                System.out.println("********************courses is NULL in AsyncTask");
            }

            MainActivity.courses = courses;

            return courses;
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            listUpCourses();
        }
    }
}
