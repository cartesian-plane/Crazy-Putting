package org.ken22.input.courseinput;

import java.util.ArrayList;
import java.util.List;

public class GolfCourseLoader {
    private GolfCourseLoader instance;
    private List<GolfCourse> courses;

    private GolfCourseLoader() {
        this.courses = new ArrayList<GolfCourse>();
        //load courses from assets/input
    }

    public GolfCourseLoader getInstance() {
        if (instance == null) {
            instance = new GolfCourseLoader();
        }
        return instance;
    }

    public List<GolfCourse> getCourses() {
        return courses;
    }

    public void addCourse(GolfCourse course) {
        courses.add(course);
    }

    public void removeCourse(GolfCourse course) {
        courses.remove(course);
    }

    public void saveCourses() {
        // save courses to file
    }
}
