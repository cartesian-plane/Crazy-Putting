package org.ken22.input.courseinput;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class GolfCourseLoader {
    private static GolfCourseLoader instance;
    private final List<GolfCourse> courses;

    private GolfCourseLoader() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.courses = Files.list(Paths.get("input"))
                .filter(Files::isRegularFile)
                .map(path -> {
                    try {
                        return mapper.readValue(path.toFile(), GolfCourse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load courses", e);
        }
    }

    public static GolfCourseLoader getInstance() {
        if (instance == null) {
            instance = new GolfCourseLoader();
        }

        for(GolfCourse course : instance.getCourses()) {
            System.out.println(course.name());
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
