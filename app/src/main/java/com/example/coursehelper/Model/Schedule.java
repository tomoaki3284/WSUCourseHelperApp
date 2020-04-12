package com.example.coursehelper.Model;

import com.fasterxml.jackson.core.JsonpCharacterEscapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule {

    List<Course> courses;
    double totalCredit;
    // if overlapped class scheduled, put the warning message in this map
    EnumMap<DayOfWeek, List<String>> overlapWarning = new EnumMap<>(DayOfWeek.class);

    public Schedule() {
        this.courses = new ArrayList<Course>();
        totalCredit = 0;
    }

    public List<Course> getCourses() {
        return courses;
    }

    /**
     * Add course that is from param, then check if the course cause schedule conflict
     *
     * @param course
     * @return True, if added courses cause time overlap conflict with other courses
     */
    public boolean addCourse(Course course) {
        courses.add(course);
        totalCredit += course.getCredit();
        return isHoursOverlap();
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
        totalCredit -= course.getCredit();
        return courses.remove(course);
    }

    /**
     * Remove the courses based on index
     *
     * @param idx
     * @return Course
     */
    public Course removeCourse(int idx) {
        if(idx < 0 || idx >= courses.size()) return null;
        return courses.remove(idx);
    }

    /**
     * Check if the courses have any overlapped hours
     *
     * @return True, if some course hours is overlapped
     */
    //TODO: Not detecting the overlapped, need to fix this
    public boolean isHoursOverlap() {
//        boolean anyOverlap = false;
//        if (courses.size() <= 1) return anyOverlap;
//
//        EnumMap<DayOfWeek, List<Course>> map = new EnumMap(DayOfWeek.class);
//        for(Course course : courses){
//            for(DayOfWeek day : course.getHoursOfDay().keySet()){
//                if(map.get(day) == null){
//                    List<Course> courses = new ArrayList<Course>();
//                    courses.add(course);
//                    map.put(day, courses);
//                }else{
//                    map.get(day).add(course);
//                }
//            }
//        }
//
//        Comparator<int>
//        for(DayOfWeek day : map.keySet()){
//            List<Course> courses = map.get(day);
//            Collections.sort();
//        }
        return false;
    }
}