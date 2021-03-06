package org.tomoaki.coursehelper.Model.Data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

public class Schedule implements Serializable {

    public static final String PREFS_KEY = "com.tomoaki.coursehelper.Model.Schedule.java.SharedPreferences";
    List<Course> courses;
    double totalCredit;
    // if overlapped class scheduled, put the warning message in this map
    private EnumMap<DayOfWeek, List<String>> overlapWarning = new EnumMap<>(DayOfWeek.class);

    public Schedule(Schedule schedule) {
        this.courses = new ArrayList<>(schedule.courses);
        this.totalCredit = schedule.totalCredit;
    }

    public Schedule(List<Course> courses) {
        this.courses = courses;
        for(Course course : this.courses){
            totalCredit += course.getCredit();
        }
    }

    public Schedule() {
        this.courses = new ArrayList<Course>();
        totalCredit = 0;
    }

    public EnumMap<DayOfWeek, List<String>> getOverlapWarning() {
        return overlapWarning;
    }

    public List<Course> getCourses() {
        return courses;
    }

    /**
     * Add course that is from param, then check if the course cause schedule conflict
     *
     * @param course
     */
    public void addCourse(Course course) {
        courses.add(course);
        totalCredit += course.getCredit();
    }

    public double getTotalCredit() {
        return totalCredit;
    }

    /**
     * Remove the course that matches the param course
     *
     * @return True, if successfully found course to be removed False, otherwise
     */
    public boolean removeCourse(Course course) {
        boolean containsCourse = courses.remove(course);
        if(containsCourse) totalCredit -= course.getCredit();
        return containsCourse;
    }

    /**
     * Remove the courses based on index
     *
     * @param idx
     * @return Course
     */
    public Course removeCourse(int idx) {
        if(idx < 0 || idx >= courses.size()) return null;
        Course removedCourse = courses.remove(idx);
        if(totalCredit > 0){
            totalCredit -= removedCourse.getCredit();
        }
        return removedCourse;
    }

    /**
     * Check if the courses have any overlapped hours
     *
     * @return True, if some course hours is overlapped
     */
    public boolean isHoursOverlap() {
        boolean anyOverlap = false;
        if (courses.size() <= 1) return anyOverlap;

        // put List<Course> in each corresponded day of week to make things simple
        EnumMap<DayOfWeek, List<Course>> map = new EnumMap(DayOfWeek.class);
        for(Course course : courses){
            for(DayOfWeek day : course.getHoursOfDay().keySet()){
                if(map.get(day) == null){
                    List<Course> courses = new ArrayList<Course>();
                    courses.add(course);
                    map.put(day, courses);
                }else{
                    map.get(day).add(course);
                }
            }
        }

        // sort List<Course> by time in each day of week using comparator
        for(DayOfWeek day : map.keySet()){
            Comparator<Course> com = new Comparator<Course>(){
                public int compare(Course o1, Course o2) {
                    return o1.getHoursOfDay().get(day).get(0).getInIntervalForm()[0] - o2.getHoursOfDay().get(day).get(0).getInIntervalForm()[0];
                }
            };
            List<Course> courses = map.get(day);
            Collections.sort(courses, com);
        }

        clearWarnings();// just clearing old warnings that possibly left out in warnings variable

        // From here, detect overlapped classes. In each day of week, List<Course> is sorted by time,
        // so it is relatively simple to detect overlapped.
        // -> IF previous class end time is "larger" than current class start time, then that's the overlapped
        for(DayOfWeek day : map.keySet()){
            List<Course> courses = map.get(day);
            int[] prevClassTime = courses.get(0).getHoursOfDay().get(day).get(0).getInIntervalForm();
            for(int i=1; i<courses.size(); i++){
                // courses.get(i)....get(day).get(0) <- why get(0)? Because some of the classes have multiple classes
                // in one day (very minor cases). So in here, I am only checking if first class won't overlapped with
                // other classes.
                // TODO: Cover the edge case, where some of the class has two class in one day.
                int[] currClassTime = courses.get(i).getHoursOfDay().get(day).get(0).getInIntervalForm();
                if(prevClassTime[1] > currClassTime[0]){
                    // overlapped
                    anyOverlap = true;
                    if(overlapWarning.get(day) == null){
                        List<String> warnings = new ArrayList();
                        warnings.add("Time conflict on " + day +
                                ":\n class1 @"
                                + minToMilitaryTime(prevClassTime[0]) + "-" + minToMilitaryTime(prevClassTime[1]) +
                                ", class2 @"
                                + minToMilitaryTime(currClassTime[0]) + "-" + minToMilitaryTime(currClassTime[1]));
                        overlapWarning.put(day, warnings);
                    }else{
                        overlapWarning.get(day).add("Time conflict on " + day +
                                ":\n class1 @"
                                + minToMilitaryTime(prevClassTime[0]) + "-" + minToMilitaryTime(prevClassTime[1]) +
                                ", class2 @"
                                + minToMilitaryTime(currClassTime[0]) + "-" + minToMilitaryTime(currClassTime[1]));
                    }
                }
                prevClassTime = currClassTime;
            }
        }

        return anyOverlap;
    }

    private void clearWarnings() {
        for(DayOfWeek day : overlapWarning.keySet()){
            overlapWarning.put(day, new ArrayList<String>());
        }
    }

    private String minToMilitaryTime(int min) {
        String h = Integer.toString(min / 60);
        String m = Integer.toString(min % 60);
        if(h.length() == 1) h = "0" + h;
        if(m.length() == 1) m = "0" + m;
        return h + ":" + m;
    }
}