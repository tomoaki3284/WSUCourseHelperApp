package com.example.coursehelper.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

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

//	public List<Course> filterByCourseCategory(String category) {
//		List<Course> res = new ArrayList();
//		for(Course course : courses){
//			if(course.getCourseCategory().equals(category)){
//				res.add(course);
//			}
//		}
//		return res;
//	}
//
//	public List<Course> filterByTitle(String title) {
//		List<Course> res = new ArrayList();
//		for(Course course : courses){
//			if(course.getTitle().equals(title)){
//				res.add(course);
//			}
//		}
//		return res;
//	}
//
//	public List<Course> filterByFaculty(String faculty) {
//		List<Course> res = new ArrayList();
//		for(Course course : courses){
//			if(course.getFaculty().toLowerCase().contains(faculty.toLowerCase())){
//				res.add(course);
//			}
//		}
//		return res;
//	}

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
     * Check if the courses have any overlapped hours
     *
     * @return True, if some course hours is overlapped
     */
    public boolean isHoursOverlap() {
        boolean anyOverlap = false;
        if (courses.size() <= 1) {
            return anyOverlap;
        }
        // loop courses
        // Store Hours in Map<DayOfWeek, List<int[]>>
        EnumMap<DayOfWeek, List<Hours>> map = new EnumMap<>(DayOfWeek.class);
        for (Course course : courses) {
            for (DayOfWeek day : course.getHoursOfDay().keySet()) {
                Hours hours = course.getHoursFromDay(day);
                if (map.get(day) == null) {
                    ArrayList<Hours> list = new ArrayList();
                    list.add(hours);
                    map.put(day, list);
                } else {
                    map.get(day).add(hours);
                }
            }
        }

        // detect overlap
        // sort by intervals by starting hours
        // if prevEndTime end exceed currStartTime, there is a overlap
        Comparator<Hours> com = new Comparator<Hours>() {
            @Override
            public int compare(Hours a, Hours b) {
                return a.getInIntervalForm()[0] - b.getInIntervalForm()[0];
            }
        };
        for (DayOfWeek day : map.keySet()) {
            List<Hours> hours = map.get(day);
            Collections.sort(hours, com);
            int len = hours.size();
            if (len <= 1) {
                continue;
            }

            int[] prev = hours.get(0).getInIntervalForm();
            for (int i = 1; i < len; i++) {
                int[] curr = hours.get(i).getInIntervalForm();
                int prevEnd = prev[1];
                int currStart = curr[0];
                if (prevEnd > currStart) {
                    anyOverlap = true;
                    // overlap exist
                    String warning = "CLASS TIME OVERLAPPED - " + day.name() + ": " +
                            hours.get(i - 1).toString() + " <-> " + hours.get(i).toString();
                    if (overlapWarning.get(day) == null) {
                        List<String> warings = new ArrayList();
                        warings.add(warning);
                        overlapWarning.put(day, warings);
                    } else {
                        overlapWarning.get(day).add(warning);
                    }
                }
            }
        }

        return anyOverlap;
    }
}