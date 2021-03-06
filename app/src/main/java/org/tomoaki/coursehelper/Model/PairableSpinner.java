package org.tomoaki.coursehelper.Model;

import android.widget.AdapterView;
import android.widget.Spinner;

import org.tomoaki.coursehelper.Model.Data.Course;

import java.util.ArrayList;
import java.util.List;

public class PairableSpinner {

    private static final int LAB = 1;
    private static final int ONLINE = 2;
    private static final int REMOTE = 3;

    private String categoryName;
    private Spinner spinner;
    private int position;
    private AdapterView<?> parent;
    
    public PairableSpinner(String categoryName, Spinner spinner, int position, AdapterView<?> parent) {
        this.categoryName = categoryName;
        this.spinner = spinner;
        this.position = position;
        this.parent = parent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public AdapterView<?> getParent() {
        return parent;
    }

    public void setParent(AdapterView<?> parent) {
        this.parent = parent;
    }

    public List<Course> filterCourses(List<Course> courses) {
        String target = parent.getItemAtPosition(position).toString().split(" ")[0];

        if(position == 0){
            return courses;
        }

        List<Course> filteredCourses = new ArrayList<>();
        switch(categoryName){
            case "core":
                boolean doubleDipper = target.equals("DoubleDipper");
                System.out.println(courses.size());
                for(int i=0; i<courses.size(); i++){
                    Course course = courses.get(i);
                    if(doubleDipper && course.getCores().size() >= 2){
                        filteredCourses.add(course);
                    }else if(course.getCores().contains(target)){
                        filteredCourses.add(course);
                    }
                }
                break;

            case "subject":
                for(int i=0; i<courses.size(); i++){
                    Course course = courses.get(i);
                    if(course.getSubject() == null){
                        continue;
                    }
                    if(course.getSubject().equals(target)){
                        filteredCourses.add(course);
                    }
                }
                break;

            case "special":
                int selection = 0;
                if(target.equals("lab")) selection = LAB;
                else if(target.equals("online")) selection = ONLINE;
                else if(target.equals("remsyc")) selection = REMOTE;

                for(int i=0; i<courses.size(); i++){
                    Course course = courses.get(i);
                    if(selection == LAB && course.getIsLabCourse()){
                        filteredCourses.add(course);
                    }else if(selection == ONLINE && course.getRoom().toLowerCase().equals("online")){
                        filteredCourses.add(course);
                    }else if(selection == REMOTE && course.getRoom().toLowerCase().contains("remsyc")){
                        filteredCourses.add(course);
                    }
                }
                break;
        }
        System.out.println(filteredCourses.size());
        return filteredCourses;
    }
}
