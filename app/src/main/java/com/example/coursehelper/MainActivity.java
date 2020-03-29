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

    private List<Course> courses = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ReadCourses().execute("https://coursehlperwsu.s3.amazonaws.com/current-semester.json");
    }

    private void listUpCourses() {
        ArrayAdapter<Course> adapter = new courseArrayAdapter(this, 0, courses);

        ListView listView = findViewById(R.id.customListView);
        listView.setAdapter(adapter);
    }

    private class ReadCourses extends AsyncTask<String, Void, List<Course>> {
        @Override
        protected List<Course> doInBackground(String... strings) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String urlStr = strings[0];
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
            }

            return courses;
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            listUpCourses();
        }
    }
}
