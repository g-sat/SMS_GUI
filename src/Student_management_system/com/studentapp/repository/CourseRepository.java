package Student_management_system.com.studentapp.repository;

import Student_management_system.com.studentapp.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CourseRepository {
    private final ConcurrentMap<UUID, Course> store = new ConcurrentHashMap<>();

    public Course save(Course course) {
        store.put(course.getId(), course);
        return course;
    }

    public List<Course> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Course> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public void delete(UUID id) {
        store.remove(id);
    }
}
