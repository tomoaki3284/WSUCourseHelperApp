package org.tomoaki.coursehelper.Model;

public interface CoursesEditable {
    //called by CourseDescriptionDialog
    public void addCourseToSchedule(Course course);

    //called by CourseDescriptionDialog
    public void removeCourseFromSchedule(Course course);
}
