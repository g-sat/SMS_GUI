package Student_management_system.com.studentapp.service;

import Student_management_system.com.studentapp.model.Course;
import Student_management_system.com.studentapp.repository.CourseRepository;

import java.util.List;
import java.util.UUID;

public class CourseService {
    private final CourseRepository repo;

    public CourseService(CourseRepository repo) {
        this.repo = repo;
        seed();
    }

    private void seed() {
        if (!repo.findAll().isEmpty()) return;
        repo.save(new Course(UUID.randomUUID(), "CS101", "Intro to CS", "Ada Lovelace", "Mon/Wed 9:00", 40, "Core"));
        repo.save(new Course(UUID.randomUUID(), "MAT205", "Linear Algebra", "Carl Gauss", "Tue/Thu 11:00", 35, "Mathematics"));
        repo.save(new Course(UUID.randomUUID(), "BUS310", "Project Management", "Peter Drucker", "Fri 13:00", 30, "Business"));
    }

    public List<Course> all() {
        return repo.findAll();
    }

    public Course create(String code, String title, String instructor, String schedule, int capacity, String category) {
        Course c = new Course(UUID.randomUUID(), code, title, instructor, schedule, capacity, category);
        return repo.save(c);
    }

    public void delete(UUID id) {
        repo.delete(id);
    }
}
