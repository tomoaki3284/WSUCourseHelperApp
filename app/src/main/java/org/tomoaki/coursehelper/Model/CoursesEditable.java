package org.tomoaki.coursehelper.Model;

import org.tomoaki.coursehelper.Model.Data.Course;

public interface CoursesEditable {
    //called by CourseDescriptionDialog
    public void addCourseToSchedule(Course course);

    //called by CourseDescriptionDialog
    public void removeCourseFromSchedule(Course course);
}
