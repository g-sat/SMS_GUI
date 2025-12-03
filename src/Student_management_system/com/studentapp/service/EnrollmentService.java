package Student_management_system.com.studentapp.service;

import Student_management_system.com.studentapp.model.Enrollment;
import Student_management_system.com.studentapp.repository.EnrollmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class EnrollmentService {
    private final EnrollmentRepository repo;

    public EnrollmentService(EnrollmentRepository repo) {
        this.repo = repo;
    }

    public Enrollment enroll(UUID studentId, UUID courseId) {
        Enrollment e = new Enrollment(UUID.randomUUID(), studentId, courseId, LocalDate.now(), "Active");
        return repo.save(e);
    }

    public void drop(UUID enrollmentId) {
        repo.delete(enrollmentId);
    }

    public List<Enrollment> all() {
        return repo.findAll();
    }

    public List<Enrollment> findByStudent(UUID studentId) {
        return repo.findByStudent(studentId);
    }

    public List<Enrollment> findByCourse(UUID courseId) {
        return repo.findByCourse(courseId);
    }
}
