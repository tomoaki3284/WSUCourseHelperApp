package org.tomoaki.coursehelper.Model.Data;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class CourseOption extends ExpandableGroup<Course> {

    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public CourseOption(String title, List<Course> courses) {
        super(title, courses);
        this.courses = courses;
    }
}
